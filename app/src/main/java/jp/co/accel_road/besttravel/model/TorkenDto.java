package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * トークンのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class TorkenDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = 9153344913040925591L;
    /** アクセストークン */
    public String accessToken;
}
