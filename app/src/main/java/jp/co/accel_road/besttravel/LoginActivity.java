package jp.co.accel_road.besttravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.model.MyAccountDto;
import jp.co.accel_road.besttravel.service.AccountService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ログイン画面のアクティビティ
 */
public class LoginActivity extends BaseActivity {

    /** ユーザーID */
    private EditText txtUserId;
    /** パスワード */
    private EditText txtPassword;
    /** ログインボタン */
    private Button btnLogin;
    /** 新規作成ボタン */
    private Button btnNewAccount;
    /** 未ログインリンク */
    private TextView lblNoAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ツールバーの設定
        setToolbar();

        //ユーザーID
        txtUserId = (EditText)findViewById(R.id.txtUserId);
        //パスワード
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        //ログインボタン
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ログインボタン押下時処理
                clickBtnLogin();
            }
        });

        //新規作成ボタン
        btnNewAccount = (Button)findViewById(R.id.btnNewAccount);
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ユーザー新規作成画面を表示
                Intent intent = new Intent(getApplicationContext(), MyAccountEditActivity.class);
                startActivity(intent);
            }
        });

        //未ログインボタン
        lblNoAccount = (TextView)findViewById(R.id.lblNoAccount);
        lblNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //マイステータスを登録・更新する
                MyAccountDao myAccountDao = new MyAccountDao();
                //最終ログインフラグを全てOFFに変更する
                myAccountDao.saveLastLoginFlgOff();

                MyAccount myStatus = myAccountDao.getMyAccount(BestTravelConstant.NOT_LOGIN_LOGIN_ID);
                if (myStatus == null) {
                    //マイステータスを登録する
                    myStatus = new MyAccount();
                    myStatus.accountId = BestTravelConstant.NOT_LOGIN_LOGIN_ID;
                    myStatus.lastLoginFlg = true;
                    myAccountDao.insertMyAccount(myStatus);
                } else {
                    //マイステータスを更新する
                    myStatus.lastLoginFlg = true;
                    myAccountDao.updateMyAccount(myStatus);
                }

                //ログインのアクティビティを終了
                //戻り値を設定して終了
                setResult(RESULT_OK);
                finish();

                //マイページ画面を表示
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    /**
     * ボタン押下時処理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //戻るボタン押下時処理
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //戻り値を設定して終了
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ログインボタン押下時処理
     */
    private void clickBtnLogin() {

        //ユーザーID、パスワードを取得
        String userId = txtUserId.getText().toString();
        String password = txtPassword.getText().toString();

        //入力値チェックでエラーの場合処理を終了する
        if (!validate()) {
            return;
        }

        // FCMアクセストークンを取得
        String fcmAccessToken = FirebaseInstanceId.getInstance().getToken();

        //ユーザーIDを元にログイン処理を行う。
        AccountService accountService = getRetrofit().create(AccountService.class);
        Call<MyAccountDto> reps = accountService.loginUserId(userId, password, fcmAccessToken);
        reps.enqueue(new Callback<MyAccountDto>() {
            @Override
            public void onResponse(Call<MyAccountDto> call, Response<MyAccountDto> response) {

                MyAccountDto myAccountDto = response.body();

                //ユーザーIDもしくはパスワードが異なる場合
                if (BestTravelConstant.RESULT_CODE_LOGIN_ERROR == myAccountDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_login));
                    return;
                }

                //ログインが正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == myAccountDto.resultCode) {
                    //マイステータスを登録・更新する
                    MyAccountDao myAccountDao = new MyAccountDao();
                    //最終ログインフラグを全てOFFに変更する
                    myAccountDao.saveLastLoginFlgOff();

                    MyAccount myAccount = myAccountDao.getMyAccount(myAccountDto.accountId);
                    if (myAccount == null) {
                        //マイステータスを登録する
                        myAccount = new MyAccount();
                        myAccount = getMyAccount(myAccount, myAccountDto);
                        myAccountDao.insertMyAccount(myAccount);
                    } else {
                        //マイステータスを更新する
                        myAccount = getMyAccount(myAccount, myAccountDto);
                        myAccountDao.updateMyAccount(myAccount);
                    }

                    //ログイン画面を閉じる
                    //戻り値を設定して終了
                    setResult(RESULT_OK);
                    finish();

                    //マイページ画面を表示
                    Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<MyAccountDto> call, Throwable t) {

                //エラーメッセージを表示
                dispToast(getResources().getString(R.string.error_transceiver));
                return;
            }
        });
    }

    /**
     * 取得したDTOよりアカウント情報を取得する。
     *
     * @param myAccountDto マイアカウントDTO
     * @return マイアカウント
     */
    private MyAccount getMyAccount(MyAccount myAccount, MyAccountDto myAccountDto) {

        myAccount.accountId = myAccountDto.accountId;
        myAccount.userId = myAccountDto.userId;
        myAccount.userName = myAccountDto.userName;
        myAccount.mailAddress = myAccountDto.mailAddress;
        myAccount.birthday = myAccountDto.birthday;
        myAccount.sexCd = myAccountDto.sexCd;
        myAccount.thumbnailHeaderImageUrl = myAccountDto.thumbnailHeaderImageUrl;
        myAccount.thumbnailIconUrl = myAccountDto.thumbnailIconUrl;
        myAccount.accessToken = myAccountDto.accessToken;
        myAccount.refreshToken = myAccountDto.refreshToken;
        myAccount.lastLoginFlg = true;

        return myAccount;
    }


    /**
     * 入力値チェックを行う。
     *
     * @return true：正常終了／false：エラー
     */
    private boolean validate() {
        //ユーザーID、パスワードを取得
        String userId = txtUserId.getText().toString();
        String password = txtPassword.getText().toString();

        //ユーザーIDが未入力の場合
        if (userId == null || "".equals(userId)) {
            //値を入力してください
            dispToast(getResources().getString(R.string.error_required));
            txtUserId.setError(getResources().getString(R.string.error_required));
            return false;
        }
        //パスワードが未入力の場合
        if (password == null || "".equals(password)) {
            //値を入力してください
            dispToast(getResources().getString(R.string.error_required));
            txtPassword.setError(getResources().getString(R.string.error_required));
            return false;
        }

        return true;
    }

}
