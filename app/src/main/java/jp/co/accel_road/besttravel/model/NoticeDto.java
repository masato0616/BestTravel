package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.entity.Notice;

/**
 * お知らせ情報のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class NoticeDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 3366238109632856914L;

	/** お知らせID */
	public Long noticeId;
	/** 開始日 */
	public Date startDate;
	/** 終了日 */
	public Date endDate;
	/** タイトル */
	public String title;
	/** 内容 */
	public String contents;
	/** 削除フラグ */
	public boolean deleteFlg;
	/** 更新日時 */
	public Date updateDate;

	/**
	 * ローカルDBのお知らせ情報を設定する
	 *
	 * @param notice
     */
	public void setNotice(Notice notice) {

		// お知らせID
		noticeId = notice.noticeId;
		// 開始日
		startDate = notice.startDate;
		// 終了日
		endDate = notice.endDate;
		// タイトル
		title = notice.title;
		// 内容
		contents = notice.contents;
		// 削除フラグ
		deleteFlg = false;
		// 更新日時
		updateDate = notice.updateDate;
	}

	/**
	 * ローカルDBのお知らせ情報を設定する
	 *
	 * @return お知らせ
	 */
	public Notice getNotice() {

		Notice notice = new Notice();

		// お知らせID
		notice.noticeId = noticeId;
		// 開始日
		notice.startDate = startDate;
		// 終了日
		notice.endDate = endDate;
		// タイトル
		notice.title = title;
		// 内容
		notice.contents = contents;
		// 更新日時
		notice.updateDate = updateDate;

		return notice;
	}
}
