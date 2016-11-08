package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * ファイル送信時のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class SendFileDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = -8335059664018383579L;

    /** 送信したファイルを取得する際のURL */
	public String fileUrl;
	/** サムネイルファイルUrl */
	public String thumbnailFileUrl;
}
