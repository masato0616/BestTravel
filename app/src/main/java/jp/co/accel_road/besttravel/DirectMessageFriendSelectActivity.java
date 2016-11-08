package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.dao.FriendDao;
import jp.co.accel_road.besttravel.entity.Friend;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.FriendListDto;
import jp.co.accel_road.besttravel.service.FriendService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ダイレクトメッセージ用の友達選択アクティビティ
 */
public class DirectMessageFriendSelectActivity extends BaseActivity {

    /** 友達リストのアダプター */
    private FriendListAdapter friendListAdapter;

    /** 友達リスト */
    private List<AccountDto> friendDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message_friend_select);

        //友達リスト
        ListView lisFriendList = (ListView)findViewById(R.id.lisFriendList);
        //友達リストのアダプターを設定
        friendListAdapter = new FriendListAdapter();
        lisFriendList.setAdapter(friendListAdapter);

        //友達情報を取得する。
        getFriendListServer();

        //ユーザーIDに紐づく友達情報をローカルDBより取得する
        getFriendListLocalDb();
    }

    /**
     * ユーザーIDに紐づく友達情報をローカルDBより取得する
     */
    private void getFriendListLocalDb() {
        //友達リストの初期化
        if (friendDtoList == null) {
            friendDtoList = new ArrayList<>();
        } else {
            friendDtoList.clear();
        }

        //友達のリストを取得する
        FriendDao friendDao = new FriendDao();
        List<Friend> friendList = friendDao.getFriendList(myAccount.accountId);
        for (Friend friend: friendList) {
            AccountDto accountDto = new AccountDto();
            accountDto.setFriend(friend);
            friendDtoList.add(accountDto);
        }

        //友達一覧を更新する。
        friendListAdapter.notifyDataSetChanged();
    }

    /**
     * ユーザーIDに紐づく友達情報を取得する
     */
    private void getFriendListServer() {

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
                        getFriendListServer();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(friendListDto.resultCode, listener)) {
                    return ;
                }

                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == friendListDto.resultCode) {

                    FriendDao friendDao = new FriendDao();

                    //友達を全て削除する
                    friendDao.deleteAllFriend(myAccount.accountId);

                    //フレンドリストを取得する
                    for (AccountDto friendDto: friendListDto.friendList) {
                        //ローカルのDBに友達情報を登録する
                        Friend friend = friendDao.getFriendByAccountId(myAccount.accountId, friendDto.accountId);
                        Friend resultFriend = friendDto.getFriend(myAccount);
                        resultFriend.updateDate = new Date();
                        friendDao.insertFriend(resultFriend);
                    }
                    friendDtoList = friendListDto.friendList;

                    //ユーザーIDに紐づく友達情報をローカルDBより取得する
                    getFriendListLocalDb();
                }
            }

            @Override
            public void onFailure(Call<FriendListDto> call, Throwable t) {
                //エラーメッセージを表示
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * 友達リストのアダプタークラス
     */
    public class FriendListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (friendDtoList == null) {
                return 0;
            }
            return friendDtoList.size();
        }

        @Override
        public Object getItem(int i) {
            if (friendDtoList == null) {
                return null;
            }
            return friendDtoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.view_friend_item, viewGroup, false);
            }

            //友達情報の取得
            AccountDto accountDto = friendDtoList.get(i);

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
            view.setTag(accountDto);
            //クリック時の処理
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ダイレクトメッセージ詳細画面を表示
                    Intent intent = new Intent(getApplicationContext(), DirectMessageDetailActivity.class);
                    //アカウントID
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, (AccountDto)v.getTag());
                    startActivity(intent);

                    //選択画面は終了しておく
                    finish();
                }
            });

            return view;
        }
    }
}
