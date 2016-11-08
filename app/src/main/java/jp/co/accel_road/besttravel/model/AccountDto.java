package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.entity.Block;
import jp.co.accel_road.besttravel.entity.Friend;
import jp.co.accel_road.besttravel.entity.MyAccount;
import jp.co.accel_road.besttravel.entity.RouteParticipant;

/**
 * アカウントテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
public class AccountDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 311652383243958705L;

    /** アカウントID */
    public Long accountId;
    /** ユーザー名 */
    public String userName;
    /** ユーザーID */
    public String userId;
    /** コメント */
    public String comment;
    /** 年齢 */
    public Integer age;
    /** 居住地 */
    public String prefectures;
    /** 性別コード */
    public Integer sexCd;
    /** ヘッダー画像URL */
    public String headerImageUrl;
    /** サムネイルヘッダー画像URL */
    public String thumbnailHeaderImageUrl;
    /** アイコンURL */
    public String iconUrl;
    /** サムネイルアイコンURL */
    public String thumbnailIconUrl;
    /** 更新日時 */
    public Date updateDate;

    /**
     * マイアカウントの値を設定する
     */
    public void setMyAccount(MyAccount myAccount) {

        // アカウントID
        accountId = myAccount.accountId;
        // ユーザー名
        userName = myAccount.userName;
        // ユーザーID
        userId = myAccount.userId;
        // コメント
        comment = myAccount.comment;
        // 年齢
        age = BestTravelUtil.getAge(myAccount.birthday);
        // 居住地
        prefectures = myAccount.prefectures;
        // 性別コード
        sexCd = myAccount.sexCd;
        // ヘッダー画像URL
        headerImageUrl = myAccount.headerImageUrl;
        // サムネイルヘッダー画像URL
        thumbnailHeaderImageUrl = myAccount.thumbnailHeaderImageUrl;
        // アイコンURL
        iconUrl = myAccount.iconUrl;
        // サムネイルアイコンURL
        thumbnailIconUrl = myAccount.thumbnailIconUrl;
        // 更新日時
        updateDate = myAccount.updateDate;
    }

    /**
     * 友達の値を設定する
     */
    public void setFriend(Friend friend) {

        // アカウントID
        accountId = friend.accountId;
        // ユーザー名
        userName = friend.userName;
        // ユーザーID
        userId = friend.userId;
        // コメント
        comment = friend.comment;
        // 年齢
        age = friend.age;
        // 居住地
        prefectures = friend.prefectures;
        // 性別コード
        sexCd = friend.sexCd;
        // ヘッダー画像URL
        headerImageUrl = friend.headerImageUrl;
        // サムネイルヘッダー画像URL
        thumbnailHeaderImageUrl = friend.thumbnailHeaderImageUrl;
        // アイコンURL
        iconUrl = friend.iconUrl;
        // サムネイルアイコンURL
        thumbnailIconUrl = friend.thumbnailIconUrl;
        // 更新日時
        updateDate = friend.updateDate;
    }

    /**
     * 友達の値を取得する
     */
    public Friend getFriend(MyAccount myAccount) {
        Friend friend = new Friend();
        // アカウントID
        friend.accountId = accountId;
        // マイアカウントID
        friend.myAccountId = myAccount.accountId;
        // ユーザー名
        friend.userName = userName;
        // ユーザーID
        friend.userId = userId;
        // コメント
        friend.comment = comment;
        // 年齢
        friend.age = age;
        // 居住地
        friend.prefectures = prefectures;
        // 性別コード
        friend.sexCd = sexCd;
        // ヘッダー画像URL
        friend.headerImageUrl = headerImageUrl;
        // サムネイルヘッダー画像URL
        friend.thumbnailHeaderImageUrl = thumbnailHeaderImageUrl;
        // アイコンURL
        friend.iconUrl = iconUrl;
        // サムネイルアイコンURL
        friend.thumbnailIconUrl = thumbnailIconUrl;
        // 更新日時
        friend.updateDate = updateDate;

        return friend;
    }

    /**
     * ブロック情報の値を設定する
     */
    public void setBlock(Block block) {

        // アカウントID
        accountId = block.accountId;
        // ユーザー名
        userName = block.userName;
        // ユーザーID
        userId = block.userId;
        // コメント
        comment = block.comment;
        // 年齢
        age = block.age;
        // 居住地
        prefectures = block.prefectures;
        // 性別コード
        sexCd = block.sexCd;
        // ヘッダー画像URL
        headerImageUrl = block.headerImageUrl;
        // サムネイルヘッダー画像URL
        thumbnailHeaderImageUrl = block.thumbnailHeaderImageUrl;
        // アイコンURL
        iconUrl = block.iconUrl;
        // サムネイルアイコンURL
        thumbnailIconUrl = block.thumbnailIconUrl;
        // 更新日時
        updateDate = block.updateDate;
    }

    /**
     * ブロック情報の値を取得する
     */
    public Block getBlock(MyAccount myAccount) {
        Block block = new Block();
        // アカウントID
        block.accountId = accountId;
        // マイアカウントID
        block.myAccountId = myAccount.accountId;
        // ユーザー名
        block.userName = userName;
        // ユーザーID
        block.userId = userId;
        // コメント
        block.comment = comment;
        // 年齢
        block.age = age;
        // 居住地
        block.prefectures = prefectures;
        // 性別コード
        block.sexCd = sexCd;
        // ヘッダー画像URL
        block.headerImageUrl = headerImageUrl;
        // サムネイルヘッダー画像URL
        block.thumbnailHeaderImageUrl = thumbnailHeaderImageUrl;
        // アイコンURL
        block.iconUrl = iconUrl;
        // サムネイルアイコンURL
        block.thumbnailIconUrl = thumbnailIconUrl;
        // 更新日時
        block.updateDate = updateDate;

        return block;
    }

    /**
     * 旅の参加者の値を設定する
     */
    public void setRouteParticipant(RouteParticipant routeParticipant) {

        // アカウントID
        accountId = routeParticipant.accountId;
        // ユーザーID
        userId = routeParticipant.userId;
        // ユーザー名
         userName = routeParticipant.userName;
        // アイコンURL
        iconUrl = routeParticipant.iconUrl;
        // サムネイルアイコンURL
        thumbnailIconUrl = routeParticipant.thumbnailIconUrl;
        // 更新日時
        updateDate = routeParticipant.updateDate;
    }

    /**
     * 旅の参加者の値を取得する
     */
    public RouteParticipant getRouteParticipant(MyAccount myAccount) {

        RouteParticipant routeParticipant = new RouteParticipant();

        // マイアカウントID */
        routeParticipant.myAccountId = myAccount.accountId;
        // アカウントID
        routeParticipant.accountId = accountId;
        // ユーザーID
        routeParticipant.userId = userId;
        // ユーザー名
        routeParticipant.userName = userName;
        // アイコンURL
        iconUrl = routeParticipant.iconUrl;
        // サムネイルアイコンURL
        routeParticipant.thumbnailIconUrl = thumbnailIconUrl;
        // 更新日時
        routeParticipant.updateDate = updateDate;

        return routeParticipant;
    }
}
