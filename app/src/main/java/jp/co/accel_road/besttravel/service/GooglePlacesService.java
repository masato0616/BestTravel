package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.GooglePlacesDto;
import jp.co.accel_road.besttravel.model.GooglePlacesResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * GooglePlacesに関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface GooglePlacesService {

    /**
     * GooglePlacesの検索結果を取得する
     */
    @POST("/google_places/search_place")
    public Call<GooglePlacesResponseDto> getSearchPlace(@Body GooglePlacesDto googlePlacesDto);
}
