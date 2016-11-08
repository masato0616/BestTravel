package jp.co.accel_road.besttravel.model;


import java.io.Serializable;

/**
 * Google Places用Dto
 *
 * @author masato
 *
 */
public class GooglePlacesGeometryDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 5316986429525957388L;

	public GooglePlacesLocationDto location;
}