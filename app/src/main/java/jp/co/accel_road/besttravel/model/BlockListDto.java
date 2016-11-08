package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * ブロック情報のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class BlockListDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 4786570368308918547L;

    /** ブロックリスト */
	public List<AccountDto> blockList;
}
