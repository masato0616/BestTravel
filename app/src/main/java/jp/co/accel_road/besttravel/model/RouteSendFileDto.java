package jp.co.accel_road.besttravel.model;

import java.io.Serializable;

/**
 * ルート情報のファイル送信時のDTO
 *
 * Created by masato on 2015/11/12.
 */
public class RouteSendFileDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 6726189404928682754L;

	/** 画像ファイルを登録したルートID */
	public Long routeId;

    /** 送信したヘッダーファイルを取得する際のURL */
	public String headerFileUrl;

	/** 送信したアイコンファイルを取得する際のURL */
	public String iconFileUrl;
}
