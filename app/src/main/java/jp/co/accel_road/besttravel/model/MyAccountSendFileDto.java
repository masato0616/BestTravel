package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * ファイル送信時のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class MyAccountSendFileDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -8335059664018383579L;

	/** ヘッダー画像URL */
	public String headerImageUrl;
	/** サムネイルヘッダー画像URL */
	public String thumbnailHeaderImageUrl;
	/** アイコンURL */
	public String iconUrl;
	/** サムネイルアイコンURL */
	public String thumbnailIconUrl;
}
