package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * お知らせテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class Notice extends BaseModel {

    /** お知らせID */
    @Column
    @PrimaryKey
    public Long noticeId;
    /** 開始日 */
    @Column
    public Date startDate;
    /** 終了日 */
    @Column
    public Date endDate;
    /** タイトル */
    @Column
    public String title;
    /** 内容 */
    @Column
    public String contents;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
