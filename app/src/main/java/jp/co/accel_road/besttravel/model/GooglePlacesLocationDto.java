package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * Google Places用Dto
 *
 * @author masato
 *
 */
public class GooglePlacesLocationDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -375051745187289183L;

	public double lat;
	public double lng;
}