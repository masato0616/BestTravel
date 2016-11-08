package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.FriendListDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * 友達情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface FriendService {

    /**
     * マイアカウントの友達アカウントを取得する
     */
    @GET("/friend/get_friend/{accessToken}")
    public Call<FriendListDto> getFriend(@Path("accessToken") final String accessToken);

    /**
     * キーワードを元に友達のアカウントのリストを取得する
     */
    @GET("/friend/search_friend/{searchKeyword}/{accessToken}")
    public Call<FriendListDto> searchFriend(@Path("searchKeyword") final String searchKeyword, @Path("accessToken") final String accessToken);

    /**
     * アカウントIDのユーザーを友達として登録する
     */
    @GET("/friend/insert_friend/{accountId}/{accessToken}")
    public Call<FriendListDto> insertFriend(@Path("accountId") final Long accountId, @Path("accessToken") final String accessToken);

    /**
     * アカウントIDのユーザーを友達解除する
     */
    @GET("/friend/remove_friend/{accountId}/{accessToken}")
    public Call<FriendListDto> removeFriend(@Path("accountId") final Long accountId, @Path("accessToken") final String accessToken);
}
