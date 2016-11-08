package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * マイアカウントと友達、ブロック情報を保持するモデルクラス
 *
 * Created by masato on 2015/11/12.
 */
public class MyAccountAllDataDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = -8053981530590141940L;

    /** マイアカウント */
    public MyAccountDto myAccountDto;
    /** マイアカウントに紐づく友達情報のリスト */
    public FriendListDto friendListDto;
    /** マイアカウントに紐づくブロック情報のリスト */
    public BlockListDto blockListDto;
    /** マイルートDTOのリスト */
    public List<RouteDto> myRouteDtoList;
    /** お気に入りルートDTOのリスト */
    public List<RouteDto> favoriteRouteDtoList;
    /** 取得最終日時 */
    public Date getLastDate;
}
