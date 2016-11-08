package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.entity.MyAccount_Table;

/**
 * マイアカウントの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class MyAccountDao {

    /**
     * マイアカウントを取得する
     *
     * @param accountId アカウントID
     * @return マイステータス
     */
    public MyAccount getMyAccount(long accountId) {
        MyAccount myAccount = SQLite.select().from(MyAccount.class).where(MyAccount_Table.accountId.eq(accountId)).querySingle();

        return myAccount;
    }

    /**
     * 最後にログインしたマイアカウントを取得する
     *
     * @return マイステータス
     */
    public MyAccount getLastLoginMyAccount() {
        MyAccount myAccount = SQLite.select().from(MyAccount.class).where(MyAccount_Table.lastLoginFlg.eq(true)).querySingle();

        return myAccount;
    }

    /**
     * マイアカウントを新規作成する。
     *
     * @param myAccount マイアカウント
     */
    public void insertMyAccount(MyAccount myAccount) {
        myAccount.insert();
    }

    /**
     * マイアカウントを更新する。
     *
     * @param myAccount マイアカウント
     */
    public void updateMyAccount(MyAccount myAccount) {
        myAccount.update();
    }

    /**
     * マイアカウントを削除する。
     *
     */
    public void deleteMyAccount() {
        SQLite.delete(MyAccount.class).execute();
    }

    /**
     * アクセストークンを更新する。
     *
     * @param accessToken アクセストークン
     * @param accountId アカウントID
     */
    public void saveAccessToken(String accessToken, long accountId) {
        SQLite.update(MyAccount.class).set(MyAccount_Table.accessToken.eq(accessToken)).where(MyAccount_Table.accountId.eq(accountId)).execute();

    }

    /**
     * 全ての最終ログインフラグを無効に更新する。
     */
    public void saveLastLoginFlgOff() {
        SQLite.update(MyAccount.class).set(MyAccount_Table.lastLoginFlg.eq(false)).execute();

    }
}
