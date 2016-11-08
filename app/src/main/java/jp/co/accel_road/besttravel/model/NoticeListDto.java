package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * お知らせ情報リストのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class NoticeListDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -7047178228610805502L;

	/** お知らせDTOのリスト */
	public List<NoticeDto> noticeDtoList;
}
