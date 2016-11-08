package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;

/**
 * アカウントとルートテーブルのモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
public class AccountRouteDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = -8872728227194332381L;

    /** アカウントDTO */
    public AccountDto accountDto;
    /** ルートDTOのリスト */
    public List<RouteDto> routeDtoList;
}
