package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.List;


/**
 * Google Places用Dto
 *
 * @author masato
 *
 */
public class GooglePlacesResponseDto extends BaseDto implements Serializable {

    /**
     * シリアルバージョンUID
     */
    private static final long serialVersionUID = 8553892565998038904L;

    public List<GooglePlacesResultDto> results;
}