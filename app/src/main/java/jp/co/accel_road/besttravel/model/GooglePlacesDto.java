package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * Google Places apiのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class GooglePlacesDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -2117437113968153476L;

    /** 検索文字列 */
    public String searchKeyword;
    /** 緯度 */
    public Double latitude;
    /** 経度 */
    public Double longitude;
    /** 半径 */
    public Integer radius;


    /**
     * 未入力な項目が無いかチェックを行う。
     *
     * @return true:正常終了／false:エラー
     */
    public boolean checkInputValue() {
    	if (searchKeyword == null || "".equals(searchKeyword)) {
    		return false;
    	}

    	if (latitude == null) {
    		return false;
    	}

    	if (longitude == null) {
    		return false;
    	}

    	if (radius == null) {
    		return false;
    	}

    	return true;
    }
}
