package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.ChatMessage;
import jp.co.accel_road.besttravel.entity.ChatMessage_Table;

/**
 * チャットメッセージの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class ChatMessageDao {

    /**
     * チャットメッセージのリストを取得する
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     * @return ルート参加者
     */
    public List<ChatMessage> getChatMessageList(Long routeId, long myAccountId) {
        List<ChatMessage> chatMessageList = SQLite.select().from(ChatMessage.class).where(ChatMessage_Table.routeId.eq(routeId) )
                .and(ChatMessage_Table.myAccountId.eq(myAccountId))
                .orderBy(ChatMessage_Table.transceiverDate, true).queryList();

        return chatMessageList;
    }

    /**
     * チャットメッセージのリストを取得する
     *
     * @param chatMessageId チャットメッセージID
     * @param myAccountId マイアカウントID
     * @return ルート参加者
     */
    public ChatMessage getChatMessage(Long chatMessageId, long myAccountId) {
        ChatMessage chatMessage = SQLite.select().from(ChatMessage.class).where(ChatMessage_Table.chatMessageId.eq(chatMessageId))
                .and(ChatMessage_Table.myAccountId.eq(myAccountId))
                .querySingle();

        return chatMessage;
    }

    /**
     * チャットメッセージを新規作成する。
     *
     * @param chatMessage チャットメッセージ
     */
    public void insertChatMessage(ChatMessage chatMessage) {
        chatMessage.insert();
    }

    /**
     * 一つのルートに含まれるチャットメッセージを全て削除する。
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     */
    public void deleteChatMessage(Long routeId, long myAccountId) {
        SQLite.delete(ChatMessage.class).where(ChatMessage_Table.routeId.eq(routeId))
                .and(ChatMessage_Table.myAccountId.eq(myAccountId))
                .execute();
    }
}
