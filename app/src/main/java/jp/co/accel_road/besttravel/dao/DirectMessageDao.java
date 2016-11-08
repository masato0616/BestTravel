package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.DirectMessage;
import jp.co.accel_road.besttravel.entity.DirectMessage_Table;

/**
 * ダイレクトメッセージの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class DirectMessageDao {

    /**
     * アカウントIDを元にダイレクトメッセージのリストを取得する
     *
     * @param accountId アカウントID
     * @param myAccountId マイアカウントID
     * @return ダイレクトメッセージのリスト
     */
    public List<DirectMessage> getDirectMessageListByAccountId(long accountId, long myAccountId) {
        List<DirectMessage> directMessageList = SQLite.select().from(DirectMessage.class).where(DirectMessage_Table.accountId.eq(accountId))
                .and(DirectMessage_Table.myAccountId.eq(myAccountId))
                .orderBy(DirectMessage_Table.transceiverDate, true).queryList();

        return directMessageList;
    }

    /**
     * ダイレクトメッセージIDを元にダイレクトメッセージを取得する
     *
     * @param directMessageId ダイレクトメッセージID
     * @return ダイレクトメッセージ
     */
    public DirectMessage getDirectMessage(Long directMessageId) {
        DirectMessage directMessage = SQLite.select().from(DirectMessage.class).where(DirectMessage_Table.directMessageId.eq(directMessageId)).querySingle();

        return directMessage;
    }

    /**
     * ダイレクトメッセージを新規作成する。
     *
     * @param directMessage ダイレクトメッセージ
     */
    public void insertDirectMessage(DirectMessage directMessage) {
        directMessage.insert();
    }

    /**
     * ダイレクトメッセージを更新する。
     *
     * @param directMessage ダイレクトメッセージ
     */
    public void updateDirectMessage(DirectMessage directMessage) {
        directMessage.update();
    }

    /**
     * ダイレクトメッセージを削除する。
     *
     * @param directMessageId ダイレクトメッセージID
     */
    public void deleteDirectMessage(Long directMessageId) {
        SQLite.delete(DirectMessage.class).where(DirectMessage_Table.directMessageId.eq(directMessageId)).execute();
    }

    /**
     * アカウントIDに紐づくダイレクトメッセージを全て削除する。
     *
     * @param accountId アカウントID
     * @param myAccountId マイアカウントID
     */
    public void deleteDirectMessageByDestinationId(long accountId, long myAccountId) {
        SQLite.delete(DirectMessage.class).where(DirectMessage_Table.accountId.eq(accountId))
                .and(DirectMessage_Table.myAccountId.eq(myAccountId)).execute();
    }
}
