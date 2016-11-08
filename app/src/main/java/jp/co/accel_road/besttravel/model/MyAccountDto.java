package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.entity.MyAccount;

/**
 * マイアカウントのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
public class MyAccountDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = 4421157606702544815L;

    /** アカウントID */
    public Long accountId;
    /** ユーザーID */
    public String userId;
    /** パスワード */
    public String password;
    /** ユーザー名 */
    public String userName;
    /** メールアドレス */
    public String mailAddress;
    /** コメント */
    public String comment;
    /** 誕生日 */
    public Date birthday;
    /** 居住地 */
    public String prefectures;
    /** 性別コード */
    public Integer sexCd;
    /** ヘッダー画像URL */
    public String headerImageUrl;
    /** アイコンURL */
    public String iconUrl;
    /** サムネイルヘッダー画像URL */
    public String thumbnailHeaderImageUrl;
    /** サムネイルアイコンURL */
    public String thumbnailIconUrl;
    /** アクセストークン */
    public String accessToken;
    /** リフレッシュトークン */
    public String refreshToken;
    /** FCMアクセストークン */
    public String fcmAccessToken;
    /** 最大ドライブ容量 */
    public long maxDriveSize;
    /** 使用ドライブ容量 */
    public long usedDriveSize;
    /** 更新日時 */
    public Date updateDate;

    /**
     * 最終ログインフラグがONの状態のマイアカウントの情報を取得する
     *
     * @param myAccountParam コピーするマイアカウント
     * @return マイアカウント
     */
    public MyAccount getLastLoginMyAccount(MyAccount myAccountParam) {
        MyAccount myAccount = new MyAccount();

        //アカウントID
        myAccount.accountId = accountId;
        // ユーザーID
        myAccount.userId = userId;
        // ユーザー名
        myAccount.userName = userName;
        // メールアドレス
        myAccount.mailAddress = mailAddress;
        // コメント
        myAccount.comment = comment;
        // 誕生日
        myAccount.birthday = birthday;
        // 居住地
        myAccount.prefectures = prefectures;
        // 性別コード
        myAccount.sexCd = sexCd;
        // 最大ドライブ容量
        myAccount.maxDriveSize = maxDriveSize;
        // 使用ドライブ容量
        myAccount.usedDriveSize = usedDriveSize;

        //最終ログインフラグ
        myAccount.lastLoginFlg = true;

        if (myAccountParam != null) {
            // アクセストークン
            myAccount.accessToken = myAccountParam.accessToken;
            // リフレッシュトークン
            myAccount.refreshToken = myAccountParam.refreshToken;
            //マイルート検索最終日時
            myAccount.myRouteGetLastDate = myAccountParam.myRouteGetLastDate;
            //お気に入りルート検索最終日時
            myAccount.favoriteGetLastDate = myAccountParam.favoriteGetLastDate;
            //ダイレクトメッセージ検索最終日時
            myAccount.directMessageGetLastDate = myAccountParam.directMessageGetLastDate;
            // ヘッダー画像URL
            myAccount.headerImageUrl = myAccountParam.headerImageUrl;
            // アイコンURL
            myAccount.iconUrl = myAccountParam.iconUrl;
            // サムネイルヘッダー画像URL
            myAccount.thumbnailHeaderImageUrl = myAccountParam.thumbnailHeaderImageUrl;
            // サムネイルアイコンURL
            myAccount.thumbnailIconUrl = myAccountParam.thumbnailIconUrl;
            //凍結フラグ
            myAccount.freezeFlg = myAccountParam.freezeFlg;

        } else {
            //ダイレクトメッセージ検索最終日時
            myAccount.directMessageGetLastDate = new Date();
            // ヘッダー画像URL
            myAccount.headerImageUrl = headerImageUrl;
            // アイコンURL
            myAccount.iconUrl = iconUrl;
            // サムネイルヘッダー画像URL
            myAccount.thumbnailHeaderImageUrl = thumbnailHeaderImageUrl;
            // サムネイルアイコンURL
            myAccount.thumbnailIconUrl = thumbnailIconUrl;
            // アクセストークン
            myAccount.accessToken = accessToken;
            // リフレッシュトークン
            myAccount.refreshToken = refreshToken;
            //凍結フラグ
            myAccount.freezeFlg = false;
        }

        // 更新日時
        myAccount.updateDate = new Date();

        return myAccount;
    }

}
