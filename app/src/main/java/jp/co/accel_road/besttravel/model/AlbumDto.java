package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * アルバムデータリストのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class AlbumDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -3700194370764437722L;

	/** アルバムデータのリスト */
	public List<AlbumDataDto> albumDataDtoList;
}
