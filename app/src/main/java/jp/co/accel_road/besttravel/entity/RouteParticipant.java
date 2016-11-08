package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * ルート参加者テーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class RouteParticipant extends BaseModel {

    /** ルート参加者ID */
    @Column
    @PrimaryKey
    public Long routeParticipantId;
    /** マイアカウントID */
    @Column
    @PrimaryKey
    public long myAccountId;
    /** ルートID */
    @Column
    public long routeId;
    /** アカウントID */
    @Column
    public long accountId;
    /** ユーザーID */
    @Column
    public String userId;
    /** ユーザー名 */
    @Column
    public String userName;
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
