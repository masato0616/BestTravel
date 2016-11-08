package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.RouteParticipant;
import jp.co.accel_road.besttravel.entity.RouteParticipant_Table;

/**
 * ルート参加者の情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class RouteParticipantDao {

    /**
     * ルート参加者のリストを取得する
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     * @return ルート参加者
     */
    public List<RouteParticipant> getRouteParticipantList(Long routeId, long myAccountId) {
        List<RouteParticipant> routeParticipantList = SQLite.select().from(RouteParticipant.class).where(RouteParticipant_Table.routeId.eq(routeId))
                .and(RouteParticipant_Table.myAccountId.eq(myAccountId))
                .orderBy(RouteParticipant_Table.userName, false).orderBy(RouteParticipant_Table.userId, false).queryList();

        return routeParticipantList;
    }

    /**
     * ルート参加者を新規作成する。
     *
     * @param routeParticipant ルート参加者
     */
    public void insertRouteParticipant(RouteParticipant routeParticipant) {
        routeParticipant.insert();
    }

    /**
     * 一つのルートに含まれるルート参加者を全て削除する。
     *
     * @param routeId ルートID
     * @param myAccountId マイアカウントID
     */
    public void deleteRouteParticipant(Long routeId, long myAccountId) {
        SQLite.delete(RouteParticipant.class).where(RouteParticipant_Table.routeId.eq(routeId))
                .and(RouteParticipant_Table.myAccountId.eq(myAccountId)).execute();
    }
}
