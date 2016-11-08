package jp.co.accel_road.besttravel.model;


import java.io.Serializable;

/**
 * Google Places用Dto
 *
 * @author masato
 *
 */
public class GooglePlacesResultDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 3809661337663839112L;

	public GooglePlacesGeometryDto geometry;
	public String icon;
	public String id;
	public String name;
	public String place_id;
	public String rating;
	public String reference;
	public String[] types;
	public String vicinity;
}