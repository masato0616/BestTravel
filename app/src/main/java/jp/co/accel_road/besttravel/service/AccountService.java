package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.AccountRouteDto;
import jp.co.accel_road.besttravel.model.MyAccountDto;
import jp.co.accel_road.besttravel.model.MyAccountAllDataDto;
import jp.co.accel_road.besttravel.model.MyAccountSendFileDto;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * アカウント情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface AccountService {

    /**
     * マイアカウントを取得する
     */
    @GET("/account/get_my_account_data/{accessToken}")
    public Call<MyAccountAllDataDto> getMyAccountData(@Path("accessToken") final String accessToken);

    /**
     * アカウントIDを元にアカウントを取得する
     */
    @GET("/account/get_account_route/{accountId}")
    public Call<AccountRouteDto> getAccountRoute(@Path("accountId") final long accountId);

    /**
     * アカウントIDを元にアカウントを取得する
     * 既にログインしている場合用
     */
    @GET("/account/get_account_route_login_user/{accountId}/{accessToken}")
    public Call<AccountRouteDto> getAccountRouteLoginUser(@Path("accountId") final long accountId, @Path("accessToken") final String accessToken);

    /**
     * ユーザーIDを元にアカウントを取得する
     */
    @GET("/account/get_account_user_id/{userId}")
    public Call<AccountDto> getAccountByUserId(@Path("userId") final String userId);

    /**
     * ユーザーIDとパスワードを元にログインを行う
     */
    @GET("/account/login_user_id/{userId}/{password}/{fcmAccessToken}")
    public Call<MyAccountDto> loginUserId(@Path("userId") final String userId, @Path("password") final String password, @Path("fcmAccessToken") final String fcmAccessToken);


    /**
     * アカウントを登録する
     */
    @POST("/account/insert_account")
    public Call<MyAccountDto> insertAccount(@Body MyAccountDto myAccountDto);

    /**
     * アカウントを更新する
     */
    @POST("/account/update_account/{accessToken}")
    public Call<MyAccountDto> updateAccount(@Body MyAccountDto myAccountDto, @Path("accessToken") final String accessToken);

    /**
     * アカウントのアイコン画像を更新する
     */
    @Multipart
    @POST("/account/update_account_icon_image/{accessToken}")
    public Call<MyAccountSendFileDto> updateAccountIconImage(@Part("description") RequestBody description, @Part MultipartBody.Part headerImageFile,
                                                             @Part MultipartBody.Part iconImageFile, @Path("accessToken") final String accessToken);
}
