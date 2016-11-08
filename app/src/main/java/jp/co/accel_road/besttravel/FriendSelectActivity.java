package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.FriendListDto;
import jp.co.accel_road.besttravel.service.FriendService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 友達選択アクティビティ
 */
public class FriendSelectActivity extends BaseActivity {

    /** リストビュー */
    private ListView lisFriendList;
    /** 友達リストのアダプター */
    private FriendSelectListAdapter friendSelectListAdapter;
    /** 友達リスト */
    private List<AccountDto> friendList;
    /** キャンセルボタン */
    private Button btnCancel;
    /** 登録ボタン */
    private Button btnResist;
    /** 元画面から渡された旅の参加者のリスト */
    private List<AccountDto> routeParticipantList;
    /** 選択位置のリスト */
    private List<Integer> selectPositionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_select);

        //ツールバーの設定
        setToolbar();

        //キャンセルボタン
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //キャンセルを設定して終了
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //登録ボタン
        btnResist = (Button)findViewById(R.id.btnResist);
        btnResist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<AccountDto> friendSelectList = new ArrayList<>();

                for (Integer selectPosition: selectPositionList) {
                    //選択された項目を抽出して設定
                    friendSelectList.add(friendList.get(selectPosition));
                }
                //ソート処理
                Collections.sort(friendSelectList, new Comparator<AccountDto>() {
                    @Override
                    public int compare(AccountDto accountDto1, AccountDto accountDto2) {

                        if (accountDto1.userName.compareTo(accountDto1.userName) > 0) {
                            return 1;
                        } else if (accountDto1.userName.compareTo(accountDto1.userName) == 0 &&
                                accountDto1.userId.compareTo(accountDto1.userId) > 0) {
                            return 1;
                        }
                        return -1;
                    }
                });

                //戻り値に選択したアカウントをセット
                Intent intent = new Intent();
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_PARTICIPANT_LIST, friendSelectList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        if (intent.hasExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_PARTICIPANT_LIST)) {
            routeParticipantList = (ArrayList<AccountDto>)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_PARTICIPANT_LIST);
        }

        //友達のリスト
        ListView lisFriendList = (ListView)findViewById(R.id.lisFriendList);

        //友達リストの初期化
        friendList = new ArrayList<>();

        //マイルートのアダプターを設定
        friendSelectListAdapter = new FriendSelectListAdapter();
        lisFriendList.setAdapter(friendSelectListAdapter);
        lisFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //リストビュー選択時の処理を行う
                CheckBox chkSelectFriend = (CheckBox) view.findViewById(R.id.chkSelectFriend);

                if (chkSelectFriend.isChecked()) {
                    //選択済みの場合
                    chkSelectFriend.setChecked(false);
                    for (Integer selectPosition: selectPositionList) {
                        if (selectPosition == position) {
                            selectPositionList.remove(selectPosition);
                            break;
                        }
                    }

                } else {
                    //未選択の場合
                    chkSelectFriend.setChecked(true);
                    selectPositionList.add(position);

                }
            }
        });

        //友達情報を取得する。
        getFriendList();
        //選択位置のリストの初期化
        selectPositionList = new ArrayList<>();
    }

    /**
     * ユーザーIDに紐づく友達情報を取得する
     */
    private void getFriendList() {

        //友達リストの取得
        //ユーザーIDを元に友達情報を取得する。
        FriendService friendService = getRetrofit().create(FriendService.class);
        Call<FriendListDto> reps = friendService.getFriend(myAccount.accessToken);
        reps.enqueue(new Callback<FriendListDto>() {

            @Override
            public void onResponse(Call<FriendListDto> call, Response<FriendListDto> response) {

                //フレンド情報の取得
                FriendListDto friendListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //フレンドリストを再取得する
                        getFriendList();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(friendListDto.resultCode, listener)) {
                    return ;
                }

                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == friendListDto.resultCode) {
                    //フレンドリストを取得する
                    friendList = friendListDto.friendList;

                    //前画面から渡された旅の参加者を選択状態に設定する
                    for (int i = 0; i < friendList.size(); i++) {

                        AccountDto friend = friendList.get(i);
                        for (AccountDto routeParticipant: routeParticipantList) {

                            if (friend.accountId == routeParticipant.accountId) {
                                selectPositionList.add(i);
                            }
                        }
                    }
                    //友達一覧を更新する。
                    friendSelectListAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<FriendListDto> call, Throwable t) {

            }
        });
    }

    /**
     * 友達リストのアダプタークラス
     */
    public class FriendSelectListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Object getItem(int i) {
            return friendList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.view_friend_select_item, viewGroup, false);
            }

            //友達情報の取得
            AccountDto accountDto = friendList.get(i);

            //選択状態の有無
            CheckBox chkSelectFriend = (CheckBox) view.findViewById(R.id.chkSelectFriend);
            //選択している場合は、選択状態に設定
            for (Integer selectPosition : selectPositionList) {
                if (selectPosition == i) {
                    chkSelectFriend.setChecked(true);
                    break;
                }
            }

            // アイコン画像
            ImageView imgIcon = (ImageView)view.findViewById(R.id.imgIcon);
            Picasso.with(getApplicationContext()).load(accountDto.thumbnailIconUrl).fit().centerInside().into(imgIcon);
            // 名前
            TextView lblUserName = (TextView)view.findViewById(R.id.lblUserName);
            lblUserName.setText(accountDto.userName);
            // ユーザーID
            TextView lblUserId = (TextView)view.findViewById(R.id.lblUserId);
            lblUserId.setText(accountDto.userId);

            //リスト選択
            view.setTag(accountDto.userId);

            return view;
        }
    }
}
