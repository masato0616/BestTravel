package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 友達情報のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class FriendListDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 251730212316582995L;

    /** 友達リスト */
	public List<AccountDto> friendList;
}
