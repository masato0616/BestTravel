package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.entity.Destination;
import jp.co.accel_road.besttravel.entity.MyAccount;

/**
 * 目的地のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class DestinationDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = 922767599667882743L;

    /** 目的地ID */
    public Long destinationId;
    /** ルートID */
    public Long routeId;
    /** 目的地名 */
    public String destinationName;
    /** 開始日 */
    public Date startDate;
    /** 開始時間 */
    public String startTime;
    /** 終了日 */
    public Date endDate;
    /** 終了時間 */
    public String endTime;
    /** 住所 */
    public String address;
    /** 緯度 */
    public Double latitude;
    /** 経度 */
    public Double longitude;
    /** メモ */
    public String memo;
    /** 非公開フラグ */
    public boolean privateDestinationFlg;
    /** 削除フラグ */
    public boolean deleteFlg;
    /** 更新日時 */
    public Date updateDate;

    /** アルバムデータのリスト */
    public List<AlbumDataDto> albumDataDtoList = new ArrayList<>();

    /**
     * ローカルDBの目的地情報を設定する
     *
     * @param destination
     */
    public void setDestination(Destination destination) {
        // 目的地ID
        destinationId = destination.destinationId;
        // ルートID
        routeId = destination.routeId;
        // 目的地名
        destinationName = destination.destinationName;
        // 開始日
        startDate = destination.startDate;
        // 開始時間
        startTime = destination.startTime;
        // 終了日
        endDate = destination.endDate;
        // 終了時間
        endTime = destination.endTime;
        // 住所
        address = destination.address;
        // 緯度
        latitude = destination.latitude;
        // 経度
        longitude = destination.longitude;
        // メモ
        memo = destination.memo;
        // 非公開フラグ
        privateDestinationFlg = destination.privateDestinationFlg;
        // 更新日時
        updateDate = destination.updateDate;
    }

    /**
     * ローカルDBの目的地情報を取得する
     *
     * @return
     */
    public Destination getDestination(MyAccount myAccount) {
        Destination destination = new Destination();
        // 目的地ID
        destination.destinationId = destinationId;
        // マイアカウントID
        destination.myAccountId = myAccount.accountId;
        // ルートID
        destination.routeId = routeId;
        // 目的地名
        destination.destinationName = destinationName;
        // 開始日
        destination.startDate = startDate;
        // 開始時間
        destination.startTime = startTime;
        // 終了日
        destination.endDate = endDate;
        // 終了時間
        destination.endTime = endTime;
        // 住所
        destination.address = address;
        // 緯度
        destination.latitude = latitude;
        // 経度
        destination.longitude = longitude;
        // メモ
        destination.memo = memo;
        // 非公開フラグ
        destination.privateDestinationFlg = privateDestinationFlg;
        // 更新日時
        destination.updateDate = updateDate;
        return destination;
    }
}
