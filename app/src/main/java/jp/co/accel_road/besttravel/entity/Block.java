package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * ブロックテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class Block extends BaseModel {

    /** ブロックID */
    @Column
    @PrimaryKey
    public Long blockId;
    /** アカウントID */
    @Column
    public long accountId;
    /** マイアカウントID */
    @Column
    public long myAccountId;
    /** ユーザーID */
    @Column
    public String userId;
    /** ユーザー名 */
    @Column
    public String userName;
    /** コメント */
    @Column
    public String comment;
    /** 年齢 */
    @Column
    public Integer age;
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
    /** 更新日時 */
    @Column
    public Date updateDate;
}
