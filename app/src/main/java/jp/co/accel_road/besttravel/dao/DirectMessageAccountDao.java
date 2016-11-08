package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.DirectMessageAccount;
import jp.co.accel_road.besttravel.entity.DirectMessageAccount_Table;

/**
 * ダイレクトメッセージアカウントの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class DirectMessageAccountDao {

    /**
     * ダイレクトメッセージアカウントのリストを取得する
     *
     * @param myAccountId マイアカウントID
     * @return ダイレクトメッセージアカウントのリスト
     */
    public List<DirectMessageAccount> getDirectMessageAccountList(long myAccountId) {
        List<DirectMessageAccount> directMessageAccountList = SQLite.select().from(DirectMessageAccount.class)
                .where(DirectMessageAccount_Table.myAccountId.eq(myAccountId))
                .orderBy(DirectMessageAccount_Table.lastTransceiverDate, false).queryList();

        return directMessageAccountList;
    }

    /**
     * ダイレクトメッセージアカウントを取得する
     *
     * @param accountId アカウントID
     * @param myAccountId マイアカウントID
     * @return ダイレクトメッセージアカウント
     */
    public DirectMessageAccount getDirectMessageAccount(long accountId, long myAccountId) {
        DirectMessageAccount directMessageAccount = SQLite.select().from(DirectMessageAccount.class)
                .where(DirectMessageAccount_Table.accountId.eq(accountId))
                .and(DirectMessageAccount_Table.myAccountId.eq(myAccountId)).querySingle();

        return directMessageAccount;
    }

    /**
     * ダイレクトメッセージアカウントを新規作成する。
     *
     * @param directMessageAccount ダイレクトメッセージアカウント
     */
    public void insertDirectMessageAccount(DirectMessageAccount directMessageAccount) {
        directMessageAccount.insert();
    }

    /**
     * ダイレクトメッセージアカウントを更新する。
     *
     * @param directMessageAccount ダイレクトメッセージアカウント
     */
    public void updateDirectMessageAccount(DirectMessageAccount directMessageAccount) {
        directMessageAccount.update();
    }

    /**
     * ダイレクトメッセージアカウントを削除する。
     *
     * @param accountId アカウントID
     */
    public void deleteDirectMessageAccount(long accountId, long myAccountId) {
        SQLite.delete(DirectMessageAccount.class).where(DirectMessageAccount_Table.accountId.eq(accountId))
                .and(DirectMessageAccount_Table.myAccountId.eq(myAccountId)).execute();
    }
}
