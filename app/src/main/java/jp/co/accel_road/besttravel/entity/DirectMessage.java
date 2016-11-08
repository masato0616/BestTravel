package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * ダイレクトメッセージテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class DirectMessage extends BaseModel {

    /** ダイレクトメッセージID */
    @Column
    @PrimaryKey
    public Long directMessageId;
    /** マイアカウントID */
    @Column
    public long myAccountId;

    /** アカウントID */
    @Column
    public long accountId;
    /** 送信・受信区分 */
    @Column
    public Integer sendReceiveKbn;
    /** メッセージ */
    @Column
    public String message;
    /** 送信したファイルを取得する際のURL */
    @Column
    public String fileUrl;
    /** サムネイルファイルUrl */
    @Column
    public String thumbnailFileUrl;
    /** 送受信日時 */
    @Column
    public Date transceiverDate;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
