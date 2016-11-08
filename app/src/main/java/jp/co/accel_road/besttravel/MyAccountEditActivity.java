package jp.co.accel_road.besttravel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.fragment.DatePickerDialogFragment;
import jp.co.accel_road.besttravel.fragment.ProgressBarDialogFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.ProgressBarDialogListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.MyAccountDto;
import jp.co.accel_road.besttravel.model.MyAccountSendFileDto;
import jp.co.accel_road.besttravel.service.AccountService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * マイアカウント編集画面のアクティビティ
 */
public class MyAccountEditActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    /** ヘッダー画像 */
    private ImageView imgHeader;
    /** アイコン画像 */
    private ImageView imgIcon;
    /** 名前 */
    private EditText txtUserName;
    /** ユーザーID */
    private EditText txtUserId;
    /** ユーザーID使用可能チェックボタン */
    private Button btnUserIdCheck;
    /** ユーザーID使用可能チェック結果表示 */
    private TextView lblUserIdCheckResult;
    /** パスワード */
    private EditText txtPassword;
    /** パスワード確認用 */
    private EditText txtPasswordConfirm;
    /** メールアドレス */
    private EditText txtMailAddress;
    /** コメント */
    private EditText txtComment;
    /** 性別 */
    private RadioGroup rdoSexCd;
    /** 生年月日 */
    private Button btnBirthday;
    /** 都道府県 */
    private Spinner spnPrefectures;
    /** 登録ボタン */
    private Button btnRegist;

    /** 初期表示時のユーザーID */
    private String initUserId;

    /** 登録処理を行うダイアログ */
    private ProgressBarDialogFragment progressBarDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_edit);

        //ツールバーの設定
        setToolbar();

        // ヘッダー画像
        imgHeader = (ImageView)findViewById(R.id.imgHeader);
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像押下時処理
                //画像選択のためのアプリを表示
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }
                startActivityForResult(Intent.createChooser(intent,"select"), BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_HEADER);
            }
        });

        // アイコン画像
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像押下時処理
                //画像選択のためのアプリを表示
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }
                startActivityForResult(Intent.createChooser(intent,"select"), BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_ICON);
            }
        });

        // 名前
        txtUserName = (EditText)findViewById(R.id.txtUserName);
        // ユーザーID
        txtUserId = (EditText)findViewById(R.id.txtUserId);
        // ユーザーID使用可能チェックボタン
        btnUserIdCheck = (Button)findViewById(R.id.btnUserIdCheck);
        btnUserIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ユーザーIDの取得
                String userId = txtUserId.getText().toString();

                //ユーザーIDが設定されていない場合は、エラーを返して終了する
                if (userId == null || "".equals(userId)) {
                    dispToast(getResources().getString(R.string.error_not_user_id));
                    return;
                }

                //ユーザーIDが初期値からへんこうされていない場合
                if (userId.equals(initUserId)) {
                    lblUserIdCheckResult.setText(getResources().getString(R.string.ok));
                    return;
                }
                //ユーザーIDが既に登録されているかチェックする
                AccountService accountService = getRetrofit().create(AccountService.class);
                Call<AccountDto> reps = accountService.getAccountByUserId(userId);
                reps.enqueue(new Callback<AccountDto>() {
                    @Override
                    public void onResponse(Call<AccountDto> call, Response<AccountDto> response) {

                        //サーバーより取得したアカウント
                        AccountDto accountDto = response.body();

                        //ユーザーIDに紐づくデータが存在しない場合
                        if (BestTravelConstant.RESULT_CODE_NOTHING == accountDto.resultCode) {
                            lblUserIdCheckResult.setText(getResources().getString(R.string.ok));
                        } else if (BestTravelConstant.RESULT_CODE_OK == accountDto.resultCode) {
                            dispToast(getResources().getString(R.string.error_used_user_id));
                            lblUserIdCheckResult.setText(getResources().getString(R.string.ng));
                        } else {
                            dispToast(getResources().getString(R.string.error_transceiver));
                            lblUserIdCheckResult.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountDto> call, Throwable t) {
                        dispToast(getResources().getString(R.string.error_transceiver));
                        lblUserIdCheckResult.setText("");
                    }
                });
            }
        });
        /// ユーザーID使用可能チェック結果表示
        lblUserIdCheckResult = (TextView)findViewById(R.id.lblUserIdCheckResult);
        // パスワード
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        // パスワード確認用
        txtPasswordConfirm = (EditText)findViewById(R.id.txtPasswordConfirm);
        // メールアドレス
        txtMailAddress = (EditText)findViewById(R.id.txtMailAddress);
        // コメント
        txtComment = (EditText)findViewById(R.id.txtComment);
        // 性別
        rdoSexCd = (RadioGroup)findViewById(R.id.rdoSexCd);
        // 生年月日
        btnBirthday = (Button)findViewById(R.id.btnBirthday);
        btnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String birthday = ((Button) view).getText().toString();

                if (BestTravelConstant.MISETTEI.equals(birthday)) {
                    //日付入力ダイアログを表示
                    dispDatePickerDialog(null);
                } else {
                    //日付入力ダイアログを表示
                    dispDatePickerDialog(BestTravelUtil.convertStringToDate(((Button) view).getText().toString()));
                }
            }
        });
        // 都道府県
        spnPrefectures = (Spinner)findViewById(R.id.spnPrefectures);
        //登録ボタン
        btnRegist = (Button)findViewById(R.id.btnRegist);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //入力値チェックを行い、エラーの場合は、処理を終了する。
                if (!checkInput()) {
                    return;
                }

                //処理中を表すダイアログを表示する。
                progressBarDialogFragment = dispProgressBarDialog(getResources().getString(R.string.message_processing_account), new ProgressBarDialogListener() {
                    @Override
                    public void onFinishProgress(boolean errorFlg) {
                        //プログレスバーダイアログのボタン押下時処理

                        //エラーが発生していない場合
                        if (errorFlg == false) {
                            //アクティビティを終了する
                            finishActivity();
                        }
                    }
                });

                //登録ボタン押下時処理
                clickBtnRegist();
            }
        });

        //各ビューに値をセット
        setDispViewValue();
    }

    /**
     * 子画面からの戻り時処理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //画像ギャラリーアプリからの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_HEADER:
                if (resultCode == RESULT_OK) {
                    Picasso.with(getApplicationContext()).load(data.getData()).fit().centerInside().into(imgHeader);
                    //タグに画像のファイルパスを設定
                    imgHeader.setTag(data.getData());
                }
                break;
            //画像ギャラリーアプリからの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_ICON:
                if (resultCode == RESULT_OK) {
                    Picasso.with(getApplicationContext()).load(data.getData()).fit().centerInside().into(imgIcon);
                    //タグに画像のファイルパスを設定
                    imgIcon.setTag(data.getData());
                }
        }
    }

    /**
     * 入力値チェックを行う
     *
     * @return true:正常終了／false:エラー
     */
    private boolean checkInput() {

        // 名前
        String userName = txtUserName.getText().toString();
        if (userName == null || "".equals(userName)) {
            dispToast(getResources().getString(R.string.error_input));
            txtUserName.setError(getResources().getString(R.string.error_not_input, "名前"));
            return false;
        }
        // ユーザーID
        String userId = txtUserId.getText().toString();
        if (userId == null || "".equals(userId)) {
            dispToast(getResources().getString(R.string.error_input));
            txtUserId.setError(getResources().getString(R.string.error_not_input, "ユーザーID"));
            return false;
        }

        // パスワード
        String password = txtPassword.getText().toString();
        String passwordConfirm = txtPasswordConfirm.getText().toString();
        if (password != null && !"".equals(password)) {
            //パスワードの入力文字数
            if (password.length() < 8) {
                dispToast(getResources().getString(R.string.error_input));
                txtPassword.setError(getResources().getString(R.string.error_less_input_length, "パスワード", "8"));
                return false;
            }

            //確認入力と値が一致しているか
            if (!password.equals(passwordConfirm)) {
                dispToast(getResources().getString(R.string.error_input));
                txtPassword.setError(getResources().getString(R.string.error_not_match_password));
                return false;
            }
        }
        return true;
    }


    /**
     * 登録ボタン押下時処理
     */
    private void clickBtnRegist() {

        //新規作成の場合は、ユーザーIDの存在チェックを行う

        //画面の値を元にマイアカウントの情報を取得
        MyAccountDto myAccountDto = getDispViewValue();

        //ユーザーIDが登録されていない場合、新規作成とみなす
        if (myAccount == null || myAccount.accountId == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {

            //登録処理を行う
            AccountService accountService = getRetrofit().create(AccountService.class);
            Call<MyAccountDto> reps = accountService.insertAccount(myAccountDto);
            reps.enqueue(new Callback<MyAccountDto>() {
                @Override
                public void onResponse(Call<MyAccountDto> call, Response<MyAccountDto> response) {

                    //サーバーより取得したアカウント
                    MyAccountDto myAccountDto = response.body();

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == myAccountDto.resultCode) {

                        //マイアカウントを保存
                        saveMyAccount(myAccountDto);

                        //画像データのアップデート
                        updateImage();

                    } else {
                        //エラーの場合
                        dispToast(getResources().getString(R.string.error_regist));

                        //ダイアログを処理終了状態に変更
                        finishProgressBarDialog(getResources().getString(R.string.error_regist), false);
                    }
                }

                @Override
                public void onFailure(Call<MyAccountDto> call, Throwable t) {
                    dispToast(getResources().getString(R.string.error_transceiver));

                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.error_transceiver), true);
                }
            });
        } else {
            //更新の場合

            //登録可能な場合は、登録処理を行う
            AccountService accountService = getRetrofit().create(AccountService.class);
            Call<MyAccountDto> reps = accountService.updateAccount(myAccountDto, myAccount.accessToken);
            reps.enqueue(new Callback<MyAccountDto>() {
                @Override
                public void onResponse(Call<MyAccountDto> call, Response<MyAccountDto> response) {

                    //サーバーより取得したアカウント
                    MyAccountDto myAccountDto = response.body();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //マイアカウントを再登録する
                            clickBtnRegist();
                        }
                    };
                    //アクセストークンのチェック
                    if(!checkAccessToken(myAccountDto.resultCode, listener)) {
                        return ;
                    }

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == myAccountDto.resultCode) {

                        //マイアカウントを保存
                        saveMyAccount(myAccountDto);

                        //画像データのアップデート
                        updateImage();

                    } else {
                        //エラーの場合
                        dispToast(getResources().getString(R.string.error_update));

                        //ダイアログを処理終了状態に変更
                        finishProgressBarDialog(getResources().getString(R.string.error_update), true);
                    }
                }

                @Override
                public void onFailure(Call<MyAccountDto> call, Throwable t) {
                    dispToast(getResources().getString(R.string.error_transceiver));

                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.error_transceiver), true);
                }
            });
        }
    }

    /**
     * アイコン画像ヘッダー画像のアップデートを行う。
     *
     * @return true:正常終了／false:アイコン画像なし
     */
    private void updateImage() {

        //ヘッダー画像の送信設定
        MultipartBody.Part updateHeaderImagePart = null;

        Uri headerFilePassUri = (Uri)imgHeader.getTag();
        //ファイルパスを取得
        String headerFilePass = BestTravelUtil.getFilePath(headerFilePassUri, getContentResolver());

        //パスが取得できなかった場合は、処理を終了する
        if (headerFilePass != null && !"".equals(headerFilePass)) {
            RequestBody updateHeaderImageRequestBody = BestTravelUtil.getImageRequestBody((Uri)imgHeader.getTag(), getContentResolver());

            updateHeaderImagePart =
                    MultipartBody.Part.createFormData("headerImageFile", headerFilePass, updateHeaderImageRequestBody);

        }

        //アイコン画像の送信設定
        MultipartBody.Part updateIconImagePart = null;

        Uri iconFilePassUri = (Uri)imgIcon.getTag();
        //ファイルパスを取得
        String iconFilePass = BestTravelUtil.getFilePath(iconFilePassUri, getContentResolver());

        //パスが取得できなかった場合は、処理を終了する
        if (iconFilePass != null && !"".equals(iconFilePass)) {
            RequestBody updateIconImageRequestBody = BestTravelUtil.getImageRequestBody((Uri)imgIcon.getTag(), getContentResolver());

            updateIconImagePart =
                    MultipartBody.Part.createFormData("iconImageFile", iconFilePass, updateIconImageRequestBody);

        }

        //画像がどちらも設定されていない場合は、処理を終了する
        if (updateHeaderImagePart == null && updateIconImagePart == null) {
            //ダイアログを処理終了状態に変更
            finishProgressBarDialog(getResources().getString(R.string.message_save_account), false);
            return;
        }

        //送信ファイルの説明
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        //アイコン画像の更新処理を行う
        AccountService accountService = getRetrofit().create(AccountService.class);
        Call<MyAccountSendFileDto> reps = accountService.updateAccountIconImage(description, updateHeaderImagePart, updateIconImagePart, myAccount.accessToken);
        reps.enqueue(new Callback<MyAccountSendFileDto>() {
            @Override
            public void onResponse(Call<MyAccountSendFileDto> call, Response<MyAccountSendFileDto> response) {

                //サーバーより取得したアカウント
                MyAccountSendFileDto myAccountSendFileDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //アイコン画像ヘッダー画像を再登録する
                        updateImage();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(myAccountSendFileDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == myAccountSendFileDto.resultCode) {

                    //マイアカウントを登録
                    MyAccountDao myAccountDao = new MyAccountDao();
                    //端末に登録されているマイアカウント
                    MyAccount myAccountLocal = myAccountDao.getMyAccount(myAccount.accountId);
                    //サーバーより受信したヘッダーのURL
                    myAccountLocal.headerImageUrl = myAccountSendFileDto.headerImageUrl;
                    myAccountLocal.thumbnailHeaderImageUrl = myAccountSendFileDto.thumbnailHeaderImageUrl;
                    //サーバーより受信したアイコンのURL
                    myAccountLocal.iconUrl = myAccountSendFileDto.iconUrl;
                    myAccountLocal.thumbnailIconUrl = myAccountSendFileDto.thumbnailIconUrl;
                    myAccountDao.updateMyAccount(myAccountLocal);

                    setResult(Activity.RESULT_OK);

                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.message_save_account), false);
                } else {
                    //エラーの場合
                    dispToast(getResources().getString(R.string.error_save_image));

                    //ダイアログを処理終了状態に変更
                    finishProgressBarDialog(getResources().getString(R.string.error_save_image), true);
                }
            }

            @Override
            public void onFailure(Call<MyAccountSendFileDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));

                //ダイアログを処理終了状態に変更
                finishProgressBarDialog(getResources().getString(R.string.error_transceiver), true);
            }
        });
        return;
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
     * アクティビティの終了処理を行う
     */
    private void finishActivity() {

        finish();

        //マイページ画面を表示
        Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        dispToast(getResources().getString(R.string.message_save_account));
    }

    /**
     * マイアカウントを保存する
     *
     * @param myAccountDto マイアカウントDTO
     */
    private void saveMyAccount(MyAccountDto myAccountDto) {

        //マイアカウントを取得
        MyAccountDao myAccountDao = new MyAccountDao();
        //最終ログインフラグを全てOFFに変更する
        myAccountDao.saveLastLoginFlgOff();

        MyAccount myAccount = myAccountDao.getMyAccount(myAccountDto.accountId);

        if (myAccount == null) {
            //マイアカウントを登録
            myAccountDao.insertMyAccount(myAccountDto.getLastLoginMyAccount(null));
        } else {
            //マイアカウントを登録
            myAccountDao.updateMyAccount(myAccountDto.getLastLoginMyAccount(myAccount));
        }

        //マイアカウントのステータスを更新する
        refreshMyAccount();
    }

    /**
     * 日付入力ダイアログを表示する。
     * @param date
     */
    private void dispDatePickerDialog(Date date) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BestTravelConstant.PARAMETER_KEY_DATE, date);
        datePickerDialogFragment.setArguments(args);
        datePickerDialogFragment.show(getSupportFragmentManager(), BestTravelConstant.FRAGMENT_KEY_DATE_PICKER_DIALOG);
    }
    /**
     * 日付のダイアログでの戻り時処理
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        //現在の日付より前かどうかチェックを行う。
        if (calendar.getTime().compareTo(new Date()) > 0) {
            dispToast(getResources().getString(R.string.error_fraud, "生年月日"));
        } else {
            btnBirthday.setText(BestTravelUtil.convertDateToString(calendar.getTime()));
        }
    }

    /**
     * 各ビューにマイアカウントの値を設定する
     */
    private void setDispViewValue() {

        //値が存在しない場合は終了する
        if (myAccount == null || myAccount.userId == null || "".equals(myAccount.userId)) {
            return;
        }

        //ヘッダー画像
        Picasso.with(this).load(myAccount.thumbnailHeaderImageUrl).fit().centerInside().into(imgHeader);
        // アイコン画像
        Picasso.with(this).load(myAccount.thumbnailIconUrl).fit().centerInside().into(imgIcon);
        // 名前
        txtUserName.setText(myAccount.userName);
        // ユーザーID
        txtUserId.setText(myAccount.userId);
        initUserId = myAccount.userId;
        // メールアドレス
        txtMailAddress.setText(myAccount.mailAddress);
        // コメント
        txtComment.setText(myAccount.comment);
        // 性別
        setSexCd(myAccount.sexCd);
        // 生年月日
        if (myAccount.birthday != null) {
            btnBirthday.setText(BestTravelUtil.convertDateToString(myAccount.birthday));
        } else {
            //未設定と表示
            btnBirthday.setText(BestTravelConstant.MISETTEI);
        }
        // 都道府県
        setPrefectures(myAccount.prefectures);
    }

    /**
     * 各ビューよりマイアカウントの値を取得する
     */
    private MyAccountDto getDispViewValue() {

        MyAccountDto myAccountDto = new MyAccountDto();

        //画面の値をマイアカウントのステータスに設定する
        if (myAccount != null) {
            //画像のURLは以前の設定のままを送信する
            myAccountDto.headerImageUrl = myAccount.headerImageUrl;
            myAccountDto.iconUrl = myAccount.iconUrl;
            myAccountDto.thumbnailHeaderImageUrl = myAccount.thumbnailHeaderImageUrl;
            myAccountDto.thumbnailIconUrl = myAccount.thumbnailIconUrl;
        }
        // 名前
        myAccountDto.userName = txtUserName.getText().toString();
        // ユーザーID
        myAccountDto.userId = txtUserId.getText().toString();
        // パスワード
        myAccountDto.password = txtPassword.getText().toString();
        // メールアドレス
        myAccountDto.mailAddress = txtMailAddress.getText().toString();
        // コメント
        myAccountDto.comment = txtComment.getText().toString();
        // 性別
        myAccountDto.sexCd = getSexCd();
        // 生年月日
        if (BestTravelConstant.MISETTEI.equals((btnBirthday.getText().toString()))) {
            myAccountDto.birthday = null;
        } else {
            myAccountDto.birthday = BestTravelUtil.convertStringToDate(btnBirthday.getText().toString());
        }
        // 都道府県
        myAccountDto.prefectures = (String)spnPrefectures.getSelectedItem();

        //FCMアクセストークン
        String fcmAccessToken = FirebaseInstanceId.getInstance().getToken();
        myAccountDto.fcmAccessToken = fcmAccessToken;

        return myAccountDto;
    }

    /**
     * コード値を元に性別の初期値を設定する。
     *
     * @return
     */
    private void setSexCd(Integer id) {

        if (id == null) {
            return;
        }

        if (BestTravelConstant.SEX_CD_MAN == id) {
            rdoSexCd.check(R.id.rdoSexCdMan);
        } else if (BestTravelConstant.SEX_CD_WOMAN == id) {
            rdoSexCd.check(R.id.rdoSexCdWoman);
        }
    }

    /**
     * コード値を元に性別を取得する。
     *
     * @return
     */
    private Integer getSexCd() {

        int checkedId = rdoSexCd.getCheckedRadioButtonId();
        if (R.id.rdoSexCdMan == checkedId) {
            return BestTravelConstant.SEX_CD_MAN;
        } else if (R.id.rdoSexCdWoman == checkedId) {
            return BestTravelConstant.SEX_CD_WOMAN;
        }

        return null;
    }

    /**
     * 都道府県の初期値を設定する。
     *
     * @param prefectures 都道府県
     */
    private void setPrefectures(String prefectures) {

        if (prefectures == null || "".equals(prefectures)) {
            // 都道府県が設定されていない場合は、未設定を設定する。
            spnPrefectures.setSelection(0);
        }

        //都道府県の文字列を取得する
        String[] prefecturesArray = getResources().getStringArray(R.array.prefectures);

        for (int i = 0; i < prefecturesArray.length; i++) {
            if (prefecturesArray[i].equals(prefectures)) {
                spnPrefectures.setSelection(i);
                break;
            }
        }
    }
}
