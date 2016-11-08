package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.fragment.ProgressBarDialogFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.ProgressBarDialogListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.FriendListDto;
import jp.co.accel_road.besttravel.service.FriendService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 友達検索画面のアクティビティ
 */
public class FriendSearchActivity extends BaseActivity {

    /** 検索条件入力欄 */
    private EditText txtSearchKeyword;
    /** 検索ボタン */
    private Button btnSearch;
    /** 処理中を表すダイアログ */
    private ProgressBarDialogFragment progressBarDialogFragment;

    /** 友達リストのアダプター */
    private AccountListAdapter accountListAdapter;
    /** 友達リスト */
    private List<AccountDto> accountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        //ツールバーの設定
        setToolbar();

        //友達リストの初期化
        accountList = new ArrayList<>();

        //検索条件入力欄
        txtSearchKeyword = (EditText)findViewById(R.id.txtSearchKeyword);
        txtSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //検索実行時
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //プログレスバーのダイアログを表示する
                    dispProgressBarDiarog();
                    //友達情報を検索する
                    searchFriend();
                }
                return false;
            }
        });

        //検索ボタン
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //プログレスバーのダイアログを表示する
                dispProgressBarDiarog();
                //友達情報を検索する
                searchFriend();
            }
        });

        //友達のリスト
        ListView lisAccountList = (ListView)findViewById(R.id.lisAccountList);
        //マイルートのアダプターを設定
        accountListAdapter = new AccountListAdapter();
        lisAccountList.setAdapter(accountListAdapter);

    }

    /**
     * プログレスバーのダイアログを表示する
     */
    private void dispProgressBarDiarog() {

        //処理中を表すダイアログを表示する。
        progressBarDialogFragment = dispProgressBarDialog(getResources().getString(R.string.message_processing_account), new ProgressBarDialogListener() {
            @Override
            public void onFinishProgress(boolean errorFlg) {
                //プログレスバーダイアログの終了時処理
            }
        });
    }

    /**
     * プログレスバーダイアログの終了時処理を行う
     */
    private void finishProgressBarDialog(String message, boolean errorFlg) {

        //ダイアログを処理終了状態に変更
        if (progressBarDialogFragment != null) {
            progressBarDialogFragment.setCancelable(true);
            progressBarDialogFragment.finishProgress(message, errorFlg);
        }
    }

    /**
     * 友達情報を検索する
     */
    private void searchFriend() {

        //検索条件の文字列を取得
        String searchKeyword = txtSearchKeyword.getText().toString();
        //値が設定されていない場合
        if (searchKeyword== null || "".equals(searchKeyword)) {
            dispToast(getResources().getString(R.string.error_required));
            txtSearchKeyword.setError(getResources().getString(R.string.error_required));
            return;
        }

        //検索結果のアカウントリストをクリアする
        accountList.clear();
        //友達リストの取得
        //ユーザーIDを元に友達情報を取得する。
        FriendService friendService = getRetrofit().create(FriendService.class);
        Call<FriendListDto> reps = friendService.searchFriend(searchKeyword, myAccount.accessToken);
        reps.enqueue(new Callback<FriendListDto>() {
            @Override
            public void onResponse(Call<FriendListDto> call, Response<FriendListDto> response) {

                //フレンド情報の取得
                FriendListDto friendListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //友達情報を再検索する
                        searchFriend();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(friendListDto.resultCode, listener)) {
                    return ;
                }

                //対象が存在しない場合
                if (BestTravelConstant.RESULT_CODE_NOTHING == friendListDto.resultCode) {
                    dispToast(getResources().getString(R.string.error_not_exist_user));
                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.error_not_exist_user), true);
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == friendListDto.resultCode) {

                    List<AccountDto> friendSearchResultList = new ArrayList();
                    for (AccountDto accountDto: friendListDto.friendList) {
                        //自分自身は対象から除外する
                        if (myAccount.accountId.longValue() == accountDto.accountId.longValue()) {
                            continue;
                        }
                        friendSearchResultList.add(accountDto);
                    }

                    //対象ユーザーが存在しない場合
                    if (friendSearchResultList.isEmpty()) {
                        dispToast(getResources().getString(R.string.error_not_exist_user));
                        //ダイアログを処理終了状態に変更
                        finishProgressBarDialog(getResources().getString(R.string.error_not_exist_user), true);
                        return ;
                    }

                    //フレンドリストを取得する
                    accountList = friendSearchResultList;

                    //友達一覧を更新する。
                    accountListAdapter.notifyDataSetChanged();

                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.message_friend_search_end), true);

                    return ;
                }
                //ダイアログを処理終了状態に変更
                finishProgressBarDialog(getResources().getString(R.string.error_transceiver), true);
            }

            @Override
            public void onFailure(Call<FriendListDto> call, Throwable t) {
                //エラーメッセージを表示
                dispToast(getResources().getString(R.string.error_transceiver));
                //ダイアログを処理終了状態に変更
                finishProgressBarDialog(getResources().getString(R.string.error_transceiver), true);
            }
        });
    }


    /**
     * アカウントリストのアダプタークラス
     */
    public class AccountListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return accountList.size();
        }

        @Override
        public Object getItem(int i) {
            return accountList.get(i);
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
            AccountDto accountDto = accountList.get(i);

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
                    //アカウント詳細を表示
                    Intent intent = new Intent(v.getContext(), AccountDetailActivity.class);
                    //アカウントDTO
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, (AccountDto)v.getTag());
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
