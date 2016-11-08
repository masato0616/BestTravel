package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * マイデータテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class MyAccount extends BaseModel {

    /** アカウントID */
    @Column
    @PrimaryKey
    public Long accountId;
    /** ユーザーID */
    @Column
    public String userId;
    /** ユーザー名 */
    @Column
    public String userName;
    /** メールアドレス */
    @Column
    public String mailAddress;
    /** コメント */
    @Column
    public String comment;
    /** 誕生日 */
    @Column
    public Date birthday;
    /** 居住地 */
    @Column
    public String prefectures;
    /** 性別コード */
    @Column
    public Integer sexCd;
    /** ヘッダー画像URL */
    @Column
    public String headerImageUrl;
    /** サムネイルヘッダー画像URL */
    @Column
    public String thumbnailHeaderImageUrl;
    /** アイコンURL */
    @Column
    public String iconUrl;
    /** サムネイルアイコンURL */
    @Column
    public String thumbnailIconUrl;
    /** アクセストークン */
    @Column
    public String accessToken;
    /** リフレッシュトークン */
    @Column
    public String refreshToken;
    /** 最大ドライブ容量 */
    @Column
    public long maxDriveSize;
    /** 使用ドライブ容量 */
    @Column
    public long usedDriveSize;
    /** 最終ログインフラグ */
    @Column
    public boolean lastLoginFlg;
    /** マイルート取得最終日時 */
    @Column
    public Date myRouteGetLastDate;
    /** お気に入りルート取得最終日時 */
    @Column
    public Date favoriteGetLastDate;
    /** ダイレクトメッセージ取得最終日時 */
    @Column
    public Date directMessageGetLastDate;
    /** 凍結フラグ */
    @Column
    public boolean freezeFlg;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
