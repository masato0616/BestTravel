package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.model.RouteListDto;
import jp.co.accel_road.besttravel.model.RouteSendFileDto;
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
 * ルート情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface RouteService {

    /**
     * マイルートのリストを取得する
     */
    @GET("/route/get_my_route_list/{accessToken}")
    public Call<RouteListDto> getMyRouteList(@Path("accessToken") final String accessToken);

    /**
     * マイルートのリストを更新日付以降の差分のみ取得する
     */
    @GET("/route/get_difference_my_route_list/{updateDate}/{accessToken}")
    public Call<RouteListDto> getDifferenceMyRouteList(@Path("updateDate") final String updateDate, @Path("accessToken") final String accessToken);

    /**
     * ルートの詳細情報を取得する
     */
    @GET("/route/get_route_detail/{routeId}/{accessToken}")
    public Call<RouteDto> getRouteDetail(@Path("routeId") final Long routeId, @Path("accessToken") final String accessToken);

    /**
     * 編集対象のルートの詳細情報を取得する
     */
    @GET("/route/get_edit_route_detail/{routeId}/{accessToken}")
    public Call<RouteDto> getEditRouteDetail(@Path("routeId") final Long routeId, @Path("accessToken") final String accessToken);

    /**
     * ルートの詳細情報を更新日付以降の差分のみ取得する
     */
    @GET("/route/get_route_difference_detail/{routeId}/{updateDate}/{accessToken}")
    public Call<RouteDto> getRouteDifferenceDetail(@Path("routeId") final Long routeId, @Path("updateDate") final String updateDate, @Path("accessToken") final String accessToken);

    /**
     * 編集対象のルートの詳細情報を更新日付以降の差分のみ取得する
     */
    @GET("/route/get_edit_route_difference_detail/{routeId}/{updateDate}/{accessToken}")
    public Call<RouteDto> getEditRouteDifferenceDetail(@Path("routeId") final Long routeId, @Path("updateDate") final String updateDate, @Path("accessToken") final String accessToken);

    /**
     * ルートの情報を登録する
     */
    @POST("/route/insert_route/{accessToken}")
    public Call<RouteDto> insertRoute(@Body RouteDto routeDto, @Path("accessToken") final String accessToken);

    /**
     * ルートの情報を更新する
     */
    @POST("/route/update_route/{accessToken}")
    public Call<RouteDto> updateRoute(@Body RouteDto routeDto, @Path("accessToken") final String accessToken);

    /**
     * ルートの情報を削除する
     */
    @GET("/route/delete_route/{routeId}/{accessToken}")
    public Call<RouteDto> deleteRoute(@Path("routeId") final Long routeId, @Path("accessToken") final String accessToken);

    /**
     * ルートのヘッダー・アイコン画像を更新する
     */
    @Multipart
    @POST("/route/update_route_header_icon_image/{routeId}/{accessToken}")
    public Call<RouteSendFileDto> updateRouteHeaderIconImage(@Path("routeId") final Long routeId, @Part("description") RequestBody description, @Part MultipartBody.Part headerImageFile,
                                                             @Part MultipartBody.Part iconImageFile, @Path("accessToken") final String accessToken);
}
