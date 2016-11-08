package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * ルートテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class Route extends BaseModel implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 5970947901710404571L;
    /** ルートID */
    @Column
    @PrimaryKey
    public Long routeId;
    /** マイアカウントID */
    @Column
    @PrimaryKey
    public long myAccountId;

    /** ルート区分コード */
    @Column
    @PrimaryKey
    public Integer routeKbnCd;
    /** ルートタイトル */
    @Column
    public String routeTitle;
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
    /** ルート説明 */
    @Column
    public String routeDescription;
    /** 開始日 */
    @Column
    public Date startDate;
    /** 終了日 */
    @Column
    public Date endDate;
    /** マイルート公開範囲コード */
    @Column
    public int myRouteOpenRangeCd;
    /** お気に入り数 */
    @Column
    public int favoriteCount;
    /** グループチャットコメント数 */
    @Column
    public int groupChatCommentCount;
    /** 掲示板コメント数 */
    @Column
    public int bbsCommentCount;
    /** 目的地最終更新日時 */
    @Column
    public Date destinationLastUpdateDate;
    /** 更新日時 */
    @Column
    public Date updateDate;

}
