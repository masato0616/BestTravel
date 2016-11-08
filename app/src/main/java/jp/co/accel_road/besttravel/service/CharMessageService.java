package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.ChatMessageDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * チャットメッセージに関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface CharMessageService {

    /**
     * チャットメッセージを登録する
     */
    @POST("/chat_message/insert_chat_message/{accessToken}")
    public Call<ChatMessageDto> insertChatMessage(@Body ChatMessageDto chatMessageDto, @Path("accessToken") final String accessToken);
}
