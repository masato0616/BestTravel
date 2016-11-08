package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.fragment.InfomationDialogFragment;
import jp.co.accel_road.besttravel.fragment.ProgressBarDialogFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.ProgressBarDialogListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.TorkenDto;
import jp.co.accel_road.besttravel.service.TokenService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * アクティビティのスーパークラス
 *
 * Created by masato on 2016/05/23.
 */
public class BaseActivity extends AppCompatActivity {

    /** 未ログイン */
    private TextView lblNotLogin;

    /** マイアカウント */
    public MyAccount myAccount;

    /** アクセストークン再取得後の再実行処理を行うリスナー */
    private  AccessTokenListener accessTokenListener;

    /** リフレッシュトークンのコールバック */
    private Callback<TorkenDto> refreshTokenCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //マイアカウントのステータスを更新する
        refreshMyAccount();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //ツールバーが設定されていない場合は処理を行わない
        if (lblNotLogin == null) {
            return;
        }

        //ログイン状態となっていない場合
        if (myAccount == null || myAccount.userId == null || "".equals(myAccount.userId)) {
            lblNotLogin.setVisibility(View.VISIBLE);
        } else {
            lblNotLogin.setVisibility(View.GONE);
        }
    }

    /**
     * マイアカウントのステータスを最新化する。
     */
    public void refreshMyAccount() {
        MyAccountDao myAccountDao = new MyAccountDao();
        myAccount = myAccountDao.getLastLoginMyAccount();
    }

    /**
     * ツールバーの設定を行う
     */
    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //未ログイン
        lblNotLogin = (TextView)findViewById(R.id.lblNotLogin);
    }

    /**
     * ツールバーのメニュー表示を設定する
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ログイン状態となっていない場合
        if (myAccount == null || myAccount.userId == null || "".equals(myAccount.userId)) {
            getMenuInflater().inflate(R.menu.not_login_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.login_menu, menu);
        }
        return true;
    }

    /**
     * ツールバーのアイテム選択時の処理を行う
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnuAccount) {
            //アカウント表示時の処理

            //アカウント詳細画面を表示する
            Intent intent = new Intent(this, AccountDetailActivity.class);
            AccountDto accountDto = new AccountDto();
            accountDto.setMyAccount(myAccount);
            intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, accountDto);
            startActivity(intent);

            return true;
        } else if (id == R.id.mnuLogin) {
            //ログイン時の処理

            //ログイン画面を表示する
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.mnuLogout) {
            //ログアウト時の処理
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ログアウト時の処理を行う
     */
    public void logout() {

        //マイアカウントの削除
        MyAccountDao myAccountDao = new MyAccountDao();
        myAccount.lastLoginFlg = false;
        myAccountDao.updateMyAccount(myAccount);
        //メモリ上のデータも削除
        myAccount = null;

        //全てのアクティビティを削除してマイページに遷移する。
        Intent intent = new Intent(this, MyPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    /**
     * トーストの表示処理を行う。
     *
     * @param message 表示するメッセージ
     */
    public void dispToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * サーバーへの接続情報を取得する。
     *
     * @return 接続情報
     */
    public Retrofit getRetrofit() {

        // 日付型のフォーマットを設定
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd_HH:mm:ss.SSS")
                .create();

        //タイムアウトの設定
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        //サーバーへの接続情報を作成する
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.11.4:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    /**
     * アクセストークンのチェックを行い、エラーだった場合は、再取得後再度処理を行う。
     *
     * @param resultCode サーバーからの戻り値
     * @param listener アクセストークン再取得後の再実行処理
     * @return true：アクセストークンエラー以外／false：アクセストークンエラー
     */
    public boolean checkAccessToken(int resultCode, AccessTokenListener listener) {
        //アクセストークンのエラー以外の場合、処理を終了する
        if (BestTravelConstant.RESULT_CODE_ACCESS_TOKEN_ERROR != resultCode) {
            return true;
        }
        accessTokenListener = listener;

        //リフレッシュトークンでアクセストークンの再取得を行う
        sendRefreshToken(new Callback<TorkenDto>() {
            @Override
            public void onResponse(Call<TorkenDto> call, Response<TorkenDto> response) {

                TorkenDto torkenDto = response.body();

                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == torkenDto.resultCode) {
                    //フレンドリストを再取得する
                    accessTokenListener.onAgainSend();
                } else {
                    //失敗した場合
                    dispToast(getResources().getString(R.string.error_refresh_token));

                    //ログイン情報を削除してマイページ画面を表示する
                    logout();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //失敗した場合
                dispToast(getResources().getString(R.string.error_refresh_token));

                //ログイン情報を削除してマイページ画面を表示する
                logout();
            }
        });
        return false;
    }


    /**
     * リフレッシュトークンを送信し、アクセストークンを取得する。
     *
     * @param callback 通信後のコールバック
     * @return true:正常終了／false:エラー
     */
    public boolean sendRefreshToken(Callback<TorkenDto> callback) {

        refreshTokenCallback = callback;

        //リフレッシュトークンが取得できない場合は処理を終了する
        if (myAccount == null || myAccount.refreshToken == null || "".equals(myAccount.refreshToken)) {
            return false;
        }

        //サーバーへの接続情報を取得する
        Retrofit retrofit = getRetrofit();

        //ユーザーIDを元にアカウント情報を取得する。
        TokenService tokenService = retrofit.create(TokenService.class);
        Call<TorkenDto> reps = tokenService.getTokenRefreshTorken(myAccount.refreshToken);
        reps.enqueue(new Callback<TorkenDto>() {
            @Override
            public void onResponse(Call<TorkenDto> call, Response<TorkenDto> response) {

                TorkenDto torkenDto = response.body();
                //アクセストークンを更新する
                MyAccountDao myStatusDao = new MyAccountDao();
                myStatusDao.saveAccessToken(torkenDto.accessToken, myAccount.accountId);

                //マイアカウントのステータスを取得する
                MyAccountDao myAccountDao = new MyAccountDao();
                myAccount = myAccountDao.getLastLoginMyAccount();

                refreshTokenCallback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<TorkenDto> call, Throwable t) {

                refreshTokenCallback.onFailure(call, t);
            }
        });

        return true;
    }

    /**
     * ネットワークに接続されているかチェックを行う。
     *
     * @return
     */
    public boolean checkNetwork(){
        ConnectivityManager cm =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if( info == null ){
            return false;
        }
        return info.isConnected();
    }

    /**
     * 情報ダイアログを表示する
     *
     * @param message ダイアログに表示するメッセージ
     */
    public void dispInfomationDialog(String message) {
        //情報ダイアログを表示する
        InfomationDialogFragment infomationDialogFragment = new InfomationDialogFragment();
        Bundle args = new Bundle();
        args.putString(BestTravelConstant.PARAMETER_KEY_DISP_MESSAGE, message);
        infomationDialogFragment.setArguments(args);
        infomationDialogFragment.show(getSupportFragmentManager(), "InfomationDialog");

    }

    /**
     * プログレスバーダイアログを表示する
     *
     * @param message ダイアログに表示するメッセージ
     */
    public ProgressBarDialogFragment dispProgressBarDialog(String message, ProgressBarDialogListener progressBarDialogListener) {
        //ダイアログを表示する
        ProgressBarDialogFragment progressBarDialogFragment = new ProgressBarDialogFragment();
        Bundle args = new Bundle();
        args.putString(BestTravelConstant.PARAMETER_KEY_DISP_MESSAGE, message);
        progressBarDialogFragment.setArguments(args);
        progressBarDialogFragment.setCancelable(false);
        progressBarDialogFragment.setProgressBarDialogListener(progressBarDialogListener);
        progressBarDialogFragment.show(getSupportFragmentManager(), "ProgressBarDialog");

        return progressBarDialogFragment;
    }
}
