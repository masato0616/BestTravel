package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.DirectMessageDto;
import jp.co.accel_road.besttravel.model.DirectMessageListDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * ダイレクトメッセージに関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface DirectMessageService {

    /**
     * ダイレクトメッセージを登録する
     */
    @POST("/direct_message/insert_direct_message/{accessToken}")
    public Call<DirectMessageDto> insertDirectMessage(@Body DirectMessageDto directMessageDto, @Path("accessToken") final String accessToken);

    /**
     * ダイレクトメッセージを取得する
     */
    @GET("/direct_message/get_direct_message/{accessToken}")
    public Call<DirectMessageListDto> getDirectMessage(@Path("accessToken") final String accessToken);

    /**
     * ダイレクトメッセージを更新日付以降の差分のみ取得する
     */
    @GET("/direct_message/get_direct_message_difference/{updateDate}/{accessToken}")
    public Call<DirectMessageListDto> getDirectMessageDifference(@Path("updateDate") final String updateDate, @Path("accessToken") final String accessToken);
}
