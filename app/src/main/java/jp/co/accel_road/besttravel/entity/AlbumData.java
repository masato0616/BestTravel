package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * アルバムデータテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class AlbumData extends BaseModel implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = -3080303517954029119L;

    /** アルバムデータID */
    @Column
    @PrimaryKey
    public Long albumDataId;
    /** マイアカウントID */
    @Column
    @PrimaryKey
    public long myAccountId;
    /** 目的地ID */
    @Column
    public long destinationId;
    /** ルートID */
    @Column
    public long routeId;
    /** ファイルUrl */
    @Column
    public String fileUrl;
    /** サムネイルファイルUrl */
    @Column
    public String thumbnailFileUrl;
    /** 所有者アカウントID */
    @Column
    public long ownerAccountId;
    /** 非公開フラグ */
    @Column
    public boolean privateDestinationFlg;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
