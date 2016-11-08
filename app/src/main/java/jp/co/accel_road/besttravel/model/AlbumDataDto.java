package jp.co.accel_road.besttravel.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.entity.AlbumData;
import jp.co.accel_road.besttravel.entity.MyAccount;

/**
 * アルバムデータのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class AlbumDataDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = -3788284508283621706L;

    /** アルバムデータID */
    public Long albumDataId;
    /** 目的地ID */
    public Long destinationId;
    /** ルートID */
    public Long routeId;
    /** ファイルUrl */
    public String fileUrl;
    /** サムネイルファイルUrl */
    public String thumbnailFileUrl;
    /** ローカルファイルUri */
    public Uri localFileUrl;
    /** 所有者アカウントID */
    public Long ownerAccountId;
    /** 非公開フラグ */
    public boolean privateDestinationFlg;
    /** 削除フラグ */
    public boolean deleteFlg;
    /** 新規フラグ */
    public boolean newFlg;
    /** 更新日時 */
    public Date updateDate;


    /**
     * ローカルDBのアルバムデータ情報を設定する
     *
     * @param albumData
     */
    public void setAlbumData(AlbumData albumData) {

        // アルバムデータID
        albumDataId = albumData.albumDataId;
        // 目的地ID
        destinationId = albumData.destinationId;
        // ルートID
        routeId = albumData.routeId;
        // ファイルUrl
        fileUrl = albumData.fileUrl;
        // サムネイルファイルUrl
        thumbnailFileUrl = albumData.thumbnailFileUrl;
        // ローカルファイルUri
        localFileUrl = null;
        // 所有者アカウントID
        ownerAccountId = albumData.ownerAccountId;
        // 非公開フラグ
        privateDestinationFlg = albumData.privateDestinationFlg;
        // 削除フラグ
        deleteFlg =false;
        // 新規フラグ
        newFlg = false;
        // 更新日時
        updateDate = albumData.updateDate;
    }

    /**
     * ローカルDBのアルバムデータ情報を取得する
     *
     * @return アルバムデータ情報
     */
    public AlbumData getAlbumData(MyAccount myAccount) {

        AlbumData albumData = new AlbumData();

        // アルバムデータID
        albumData.albumDataId = albumDataId;
        // マイアカウントID
        albumData.myAccountId = myAccount.accountId;
        // 目的地ID
        albumData.destinationId = destinationId;
        // ルートID
        albumData.routeId = routeId;
        // ファイルUrl
        albumData.fileUrl = fileUrl;
        // サムネイルファイルUrl
        albumData.thumbnailFileUrl = thumbnailFileUrl;
        // ローカルファイルUri
        localFileUrl = null;
        // 所有者アカウントID
        albumData.ownerAccountId =ownerAccountId;
        // 非公開フラグ
        albumData.privateDestinationFlg = privateDestinationFlg;
        // 削除フラグ
        deleteFlg =false;
        // 新規フラグ
        newFlg = false;
        // 更新日時
        albumData.updateDate = updateDate;

        return albumData;
    }
}
