package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 目的地情報のファイル送信時のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class DestinationSendFileDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 7344332613753939563L;

	/** アルバムデータのリスト */
	public List<AlbumDataDto> albumDataDtoList;
}
