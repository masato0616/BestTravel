package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.TorkenDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * トークン情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface TokenService {

    /**
     * リフレッシュトークンを元にアクセストークンを取得する
     */
    @GET("/token/get_token_refresh_token/{refreshToken}")
    public Call<TorkenDto> getTokenRefreshTorken(@Path("refreshToken") final String refreshToken);

    /**
     * FCMアクセストークンを更新する
     */
    @GET("/token/update_fcm_access_token/{accountId}/{fcmAccessToken}/{accessToken}")
    public Call<TorkenDto> updateFcmAccessToken(@Path("accountId") final long accountId,
                                                @Path("fcmAccessToken") final String fcmAccessToken, @Path("accessToken") final String accessToken);
}
