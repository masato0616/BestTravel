package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ダイレクトメッセージユーザーのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class DirectMessageListDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -7253808221280171937L;

	/** ダイレクトメッセージDTOのリスト */
	public List<DirectMessageDto> directMessageDtoList;

	/** ダイレクトメッセージ検索最終日時 */
	public Date directMessageLastDate;
}
