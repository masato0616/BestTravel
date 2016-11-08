package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.Friend;
import jp.co.accel_road.besttravel.entity.Friend_Table;

/**
 * 友達の情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class FriendDao {

    /**
     * 友達を取得する
     *
     * @param friendId フレンドID
     * @return 友達
     */
    public Friend getFriend(long friendId) {
        Friend friend = SQLite.select().from(Friend.class).where(Friend_Table.friendId.eq(friendId)).querySingle();

        return friend;
    }

    /**
     * 友達のリストを取得する
     *
     * @param myAccountId マイアカウントID
     * @return 友達のリスト
     */
    public List<Friend> getFriendList(long myAccountId) {
        List<Friend> friendList = SQLite.select().from(Friend.class).where(Friend_Table.myAccountId.eq(myAccountId)).queryList();

        return friendList;
    }

    /**
     * 対象アカウントの友達を取得する
     *
     * @param myAccountId マイアカウントID
     * @param accountId アカウントID
     * @return 友達情報
     */
    public Friend getFriendByAccountId(long myAccountId, long accountId) {
        Friend friend = SQLite.select().from(Friend.class).where(Friend_Table.myAccountId.eq(myAccountId))
                .and(Friend_Table.accountId.eq(accountId)).querySingle();

        return friend;
    }


    /**
     * 友達を新規作成する。
     *
     * @param friend 友達
     */
    public void insertFriend(Friend friend) {
        friend.insert();
    }

    /**
     * 友達を更新する。
     *
     * @param friend 友達
     */
    public void updateFriend(Friend friend) {
        friend.update();
    }

    /**
     * 友達を削除する。
     *
     * @param friendId フレンドID
     */
    public void deleteFriend(long friendId) {
        SQLite.delete(Friend.class).where(Friend_Table.friendId.eq(friendId)).execute();
    }

    /**
     * 友達を全て削除する。
     *
     * @param myAccountId マイアカウントID
     */
    public void deleteAllFriend(long myAccountId) {
        SQLite.delete(Friend.class).where(Friend_Table.myAccountId.eq(myAccountId)).execute();
    }
}
