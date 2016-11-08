package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 友達情報のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class RouteListDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 2410547129467672956L;

    /** ルートリスト */
	public List<RouteDto> routeList;

	/** 取得最終日時 */
	public Date getLastDate;
}
