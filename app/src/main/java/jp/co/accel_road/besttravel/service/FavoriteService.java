package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.model.RouteListDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * お気に入りのルート情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface FavoriteService {

    /**
     * お気に入りルートのリストを取得する
     */
    @GET("/favorite/get_favorite_route_list/{accessToken}")
    public Call<RouteListDto> getFavoriteRouteList(@Path("accessToken") final String accessToken);

    /**
     * お気に入りルートのリストを更新日付以降の差分のみ取得する
     */
    @GET("/favorite/get_difference_favorite_route_list/{updateDate}/{accessToken}")
    public Call<RouteListDto> getDifferenceFavoriteRouteList(@Path("updateDate") final String updateDate, @Path("accessToken") final String accessToken);


    /**
     * お気に入りルートの情報を登録する
     */
    @POST("/favorite/insert_favorite_route/{routeId}/{accessToken}")
    public Call<RouteDto> insertFavoriteRoute(@Path("routeId") final Long routeId, @Path("accessToken") final String accessToken);

    /**
     * お気に入りルートの情報を削除する
     */
    @GET("/favorite/delete_favorite_route/{routeId}/{accessToken}")
    public Call<RouteDto> deleteFavoriteRoute(@Path("routeId") final Long routeId, @Path("accessToken") final String accessToken);
}
