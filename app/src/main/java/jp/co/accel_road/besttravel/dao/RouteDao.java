package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.entity.Route_Table;

/**
 * ルートの情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class RouteDao {

    /**
     * マイルートのリストを取得する
     *
     * @param myAccountId マイアカウントID
     * @return マイルートのリスト
     */
    public List<Route> getMyRouteList(long myAccountId) {
        List<Route> routeList = SQLite.select().from(Route.class).where(Route_Table.routeKbnCd.eq(BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE))
                .and(Route_Table.myAccountId.eq(myAccountId))
                .orderBy(Route_Table.startDate, false).orderBy(Route_Table.endDate, false).orderBy(Route_Table.routeId, true).queryList();

        return routeList;
    }

    /**
     * お気に入りルートのリストを取得する
     *
     * @param myAccountId マイアカウントID
     * @return マイルートのリスト
     */
    public List<Route> getFavoriteRouteList(long myAccountId) {
        List<Route> routeList = SQLite.select().from(Route.class).where(Route_Table.routeKbnCd.eq(BestTravelConstant.ROUTE_KBN_CD_FAVORITE_ROUTE))
                .and(Route_Table.myAccountId.eq(myAccountId))
                .orderBy(Route_Table.startDate, false).orderBy(Route_Table.endDate, false).orderBy(Route_Table.routeId, true).queryList();

        return routeList;
    }

    /**
     * その他ルートを全て取得する。
     */
    public List<Route> getOtherRouteList() {
        List<Route> routeList = SQLite.select().from(Route.class).where(Route_Table.routeKbnCd.eq(BestTravelConstant.ROUTE_KBN_CD_OTHER_ROUTE)).queryList();

        return routeList;
    }

    /**
     * ルートIDを元にマイルートを取得する
     *
     * @param myAccountId マイアカウントID
     * @return マイルートのリスト
     */
    public Route getRoute(Long routeId, long myAccountId) {
        Route route = SQLite.select().from(Route.class).where(Route_Table.routeId.eq(routeId))
                .and(Route_Table.myAccountId.eq(myAccountId))
                .orderBy(Route_Table.startDate, false).orderBy(Route_Table.endDate, false).querySingle();

        return route;
    }

    /**
     * ルートを新規作成する。
     *
     * @param route ルート
     */
    public void insertRoute(Route route) {
        route.insert();
    }

    /**
     * ルートを更新する。
     *
     * @param route ルート
     */
    public void updateRoute(Route route) {
        route.update();
    }

    /**
     * ルートを削除する。
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     */
    public void deleteRoute(Long routeId, long myAccountId) {
        SQLite.delete(Route.class).where(Route_Table.routeId.eq(routeId))
                .and(Route_Table.myAccountId.eq(myAccountId)).execute();
    }
}
