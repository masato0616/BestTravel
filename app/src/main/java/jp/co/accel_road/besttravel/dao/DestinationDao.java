package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.entity.Destination;
import jp.co.accel_road.besttravel.entity.Destination_Table;

/**
 * 目的地の情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class DestinationDao {

    /**
     * 目的地を取得する
     *
     * @param destinationId 目的地ID
     * @param myAccountId マイアカウントID
     * @return 目的地
     */
    public Destination getDestination(Long destinationId, long myAccountId) {
        Destination destination = SQLite.select().from(Destination.class).where(Destination_Table.destinationId.eq(destinationId))
                .and(Destination_Table.myAccountId.eq(myAccountId))
                .querySingle();

        return destination;
    }

    /**
     * 目的地のリストを取得する
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     * @return 目的地
     */
    public List<Destination> getDestinationList(Long routeId, long myAccountId) {
        List<Destination> destinationDbList = SQLite.select().from(Destination.class).where(Destination_Table.routeId.eq(routeId))
                .and(Destination_Table.myAccountId.eq(myAccountId))
                .orderBy(Destination_Table.startDate, false).orderBy(Destination_Table.startTime, false).
                orderBy(Destination_Table.endDate, false).orderBy(Destination_Table.endTime, false).queryList();

        return destinationDbList;
    }

    /**
     * 表示対象の目的地のリストを取得する
     *
     * @param routeId ルートID
     * @param dispDate 表示年月日
     * @param myAccountId マイアカウントID
     * @return 目的地
     */
    public List<Destination> getDispDestinationList(Long routeId, Date dispDate, long myAccountId) {
        List<Destination> destinationDbList = SQLite.select().from(Destination.class)
                .where(Destination_Table.routeId.eq(routeId))
                .and(Destination_Table.myAccountId.eq(myAccountId))
                .and(Destination_Table.startDate.lessThanOrEq(dispDate))
                .and(Destination_Table.endDate.greaterThanOrEq(dispDate))
                .orderBy(Destination_Table.startDate, true).orderBy(Destination_Table.startTime, true).
                orderBy(Destination_Table.endDate, true).orderBy(Destination_Table.endTime, true).queryList();

        return destinationDbList;
    }

    /**
     * 目的地を新規作成する。
     *
     * @param destination 目的地
     */
    public void insertDestination(Destination destination) {
        destination.insert();
    }

    /**
     * 目的地を更新する。
     *
     * @param destination 目的地
     */
    public void updateDestination(Destination destination) {
        destination.update();
    }

    /**
     * 目的地IDに紐づく目的地を削除する。
     *
     * @param destinationId 目的地ID
     * @param myAccountId マイアカウントID
     */
    public void deleteDestinationByDestinationId(Long destinationId, long myAccountId) {
        SQLite.delete(Destination.class).where(Destination_Table.destinationId.eq(destinationId))
                .and(Destination_Table.myAccountId.eq(myAccountId)).execute();
    }

    /**
     * ルートIDに紐づく目的地を全て削除する。
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     */
    public void deleteDestinationByRouteId(Long routeId, long myAccountId) {
        SQLite.delete(Destination.class).where(Destination_Table.routeId.eq(routeId))
                .and(Destination_Table.myAccountId.eq(myAccountId)).execute();
    }
}
