package jp.co.accel_road.besttravel.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.model.TorkenDto;
import jp.co.accel_road.besttravel.service.TokenService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * プッシュ通知に使用する登録トークンの生成、更新をハンドルするサービスです。
 *
 * Created by masato on 2016/09/25.
 */
public class BestTravelFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // FCMアクセストークンを取得
        String fcmAccessToken = FirebaseInstanceId.getInstance().getToken();

        //マイアカウントを取得する
        MyAccountDao myAccountDao = new MyAccountDao();
        MyAccount myAccount = myAccountDao.getLastLoginMyAccount();

        //ログイン済みの場合
        if (myAccount != null && myAccount.accountId != BestTravelConstant.NOT_LOGIN_LOGIN_ID) {

            //FCMトークンの更新
            TokenService tokenService = getRetrofit().create(TokenService.class);
            Call<TorkenDto> reps = tokenService.updateFcmAccessToken(myAccount.accountId, fcmAccessToken, myAccount.accessToken);
            reps.enqueue(new Callback<TorkenDto>() {
                @Override
                public void onResponse(Call<TorkenDto> call, Response<TorkenDto> response) {

                }

                @Override
                public void onFailure(Call<TorkenDto> call, Throwable t) {

                }
            });
        }
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
}
