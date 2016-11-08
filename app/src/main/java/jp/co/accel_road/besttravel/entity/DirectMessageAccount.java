package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * ダイレクトメッセージユーザーテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class DirectMessageAccount extends BaseModel {

    /** ダイレクトメッセージアカウントID */
    @Column
    @PrimaryKey
    public Long directMessageAccountId;
    /** マイアカウントID */
    @Column
    public long myAccountId;

    /** アカウントID */
    @Column
    public long accountId;
    /** ユーザーID */
    @Column
    public String userId;
    /** ユーザー名 */
    @Column
    public String userName;
    /** ユーザーアイコンURL */
    @Column
    public String userIconUrl;
    /** サムネイルユーザーアイコンURL */
    @Column
    public String thumbnailUserIconUrl;
    /** 最後に送受信したメッセージ */
    @Column
    public String lastMessage;
    /** 最終送受信日時 */
    @Column
    public Date lastTransceiverDate;
    /** 更新日時 */
    @Column
    public Date updateDate;
}
