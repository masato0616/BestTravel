package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * 目的地テーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class Destination extends BaseModel implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = -3080303517954029119L;
    /** 目的地ID */
    @Column
    @PrimaryKey
    public Long destinationId;
    /** マイアカウントID */
    @Column
    @PrimaryKey
    public long myAccountId;
    /** ルートID */
    @Column
    public long routeId;
    /** 開始日 */
    @Column
    public Date startDate;
    /** 開始時間 */
    @Column
    public String startTime;
    /** 終了日 */
    @Column
    public Date endDate;
    /** 終了時間 */
    @Column
    public String endTime;
    /** 目的地名 */
    @Column
    public String destinationName;
    /** 住所 */
    @Column
    public String address;
    /** 緯度 */
    @Column
    public Double latitude;
    /** 経度 */
    @Column
    public Double longitude;
    /** メモ */
    @Column
    public String memo;
    /** 非公開フラグ */
    @Column
    public boolean privateDestinationFlg;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
