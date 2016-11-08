package jp.co.accel_road.besttravel.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import jp.co.accel_road.besttravel.BestTravelDatabase;

/**
 * チャットメッセージテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
@Table(database = BestTravelDatabase.class)
public class ChatMessage extends BaseModel {

    /** チャットメッセージID */
    @Column
    @PrimaryKey
    public Long chatMessageId;
    /** マイアカウントID */
    @Column
    @PrimaryKey
    public long myAccountId;
    /** 送信ユーザーID */
    @Column
    public String sendUserId;
    /** 送信ユーザー名 */
    @Column
    public String sendUserName;
    /** 送信ユーザーアイコンURL */
    @Column
    public String sendUserIconUrl;
    /** サムネイル送信ユーザーアイコンURL */
    @Column
    public String thumbnailSendUserIconUrl;
    /** ルートID */
    @Column
    public Long routeId;
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
