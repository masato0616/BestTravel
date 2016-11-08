package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.entity.AlbumData;
import jp.co.accel_road.besttravel.entity.AlbumData_Table;
import jp.co.accel_road.besttravel.entity.Destination;
import jp.co.accel_road.besttravel.entity.Destination_Table;

/**
 * アルバムデータの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class AlbumDataDao {

    /**
     * ルートIDを元にアルバムデータのリストを取得する
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     * @return アルバムデータのリスト
     */
    public List<AlbumData> getAlbumDataListByRouteId(Long routeId, long myAccountId) {
        List<AlbumData> albumDataList = SQLite.select().from(AlbumData.class).where(AlbumData_Table.routeId.eq(routeId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .orderBy(AlbumData_Table.albumDataId, false).queryList();

        return albumDataList;
    }

    /**
     * 表示対象のアルバムデータのリストを取得する
     *
     * @param routeId ルートID
     * @param dispDate 表示年月日
     * @param myAccountId マイアカウントID
     * @return アルバムデータのリスト
     */
    public List<AlbumData> getDispAlbumDataListByRouteId(Long routeId, Date dispDate, long myAccountId) {
        List<AlbumData> albumDataList = SQLite.select().from(AlbumData.class).innerJoin(Destination.class).on(AlbumData_Table.destinationId.withTable().eq(Destination_Table.destinationId.withTable())
                    , AlbumData_Table.myAccountId.withTable().eq(Destination_Table.myAccountId.withTable()))
                .where(AlbumData_Table.routeId.withTable().eq(routeId))
                .and(AlbumData_Table.myAccountId.withTable().eq(myAccountId))
                .and(Destination_Table.startDate.withTable().lessThanOrEq(dispDate))
                .and(Destination_Table.endDate.withTable().greaterThanOrEq(dispDate))
                .orderBy(Destination_Table.startDate.withTable(), true).orderBy(Destination_Table.startTime.withTable(), true)
                .orderBy(Destination_Table.endDate.withTable(), true).orderBy(Destination_Table.endTime.withTable(), true)
                .orderBy(AlbumData_Table.albumDataId, false).queryList();

        return albumDataList;
    }

    /**
     * 目的地IDを元にアルバムデータのリストを取得する
     *
     * @param destinationId 目的地ID
     * @param myAccountId マイアカウントID
     * @return アルバムデータのリスト
     */
    public List<AlbumData> getAlbumDataListByDestinationId(Long destinationId, long myAccountId) {
        List<AlbumData> albumDataList = SQLite.select().from(AlbumData.class).where(AlbumData_Table.destinationId.eq(destinationId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .orderBy(AlbumData_Table.routeId, false)
                .orderBy(AlbumData_Table.albumDataId, false).queryList();

        return albumDataList;
    }

    /**
     * アルバムデータIDを元にアルバムデータを取得する
     *
     * @param albumDataId アルバムデータID
     * @param myAccountId マイアカウントID
     * @return アルバムデータのリスト
     */
    public AlbumData getAlbumData(Long albumDataId, long myAccountId) {
        AlbumData albumData = SQLite.select().from(AlbumData.class).where(AlbumData_Table.albumDataId.eq(albumDataId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .querySingle();

        return albumData;
    }

    /**
     * アルバムデータを新規作成する。
     *
     * @param albumData アルバムデータ
     */
    public void insertAlbumData(AlbumData albumData) {
        albumData.insert();
    }

    /**
     * アルバムデータを更新する。
     *
     * @param albumData アルバムデータ
     */
    public void updateAlbumData(AlbumData albumData) {
        albumData.update();
    }

    /**
     * アルバムデータを削除する。
     *
     * @param albumDataId アルバムデータID
     * @param myAccountId マイアカウントID
     */
    public void deleteAlbumData(Long albumDataId, long myAccountId) {
        SQLite.delete(AlbumData.class).where(AlbumData_Table.albumDataId.eq(albumDataId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .execute();
    }

    /**
     * 目的地IDに紐づくアルバムデータを全て削除する。
     *
     * @param destinationId 目的地ID
     * @param myAccountId マイアカウントID
     */
    public void deleteAlbumDataByDestinationId(Long destinationId, long myAccountId) {
        SQLite.delete(AlbumData.class).where(AlbumData_Table.destinationId.eq(destinationId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .execute();
    }

    /**
     * ルートIDに紐づくアルバムデータを全て削除する。
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     */
    public void deleteAlbumDataByRouteId(Long routeId, long myAccountId) {
        SQLite.delete(AlbumData.class).where(AlbumData_Table.routeId.eq(routeId))
                .and(AlbumData_Table.myAccountId.eq(myAccountId))
                .execute();
    }
}
