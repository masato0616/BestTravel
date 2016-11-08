package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.entity.Notice;
import jp.co.accel_road.besttravel.entity.Notice_Table;

/**
 * お知らせ情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class NoticeDao {

    /**
     * お知らせ情報を取得する
     *
     * @param noticeId お知らせID
     * @return お知らせ
     */
    public Notice getNotice(long noticeId) {
        Notice notice = SQLite.select().from(Notice.class).where(Notice_Table.noticeId.eq(noticeId)).querySingle();

        return notice;
    }

    /**
     * 現在有効なお知らせ情報の一覧を取得する。
     *
     * @return お知らせのリスト
     */
    public List<Notice> getNoticeList() {
        Date nowDate = new Date();
        List<Notice> notice = SQLite.select().from(Notice.class)
                .where(Notice_Table.startDate.lessThanOrEq(nowDate))
                .and(Notice_Table.endDate.greaterThanOrEq(nowDate))
                .orderBy(Notice_Table.startDate, true).orderBy(Notice_Table.endDate, true)
                .queryList();

        return notice;
    }

    /**
     * お知らせ情報を新規作成する。
     *
     * @param notice お知らせ
     */
    public void insertNotice(Notice notice) {
        notice.insert();
    }

    /**
     * お知らせ情報を更新する。
     *
     * @param notice お知らせ
     */
    public void updateNotice(Notice notice) {
        notice.update();
    }

    /**
     * お知らせ情報を削除する。
     *
     * @param noticeId お知らせID
     */
    public void deleteNotice(long noticeId) {
        SQLite.delete(Notice.class).where(Notice_Table.noticeId.eq(noticeId)).execute();
    }
}
