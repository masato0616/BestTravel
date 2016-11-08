package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.NoticeListDto;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * お知らせ情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface NoticeService {

    /**
     * 現在有効なお知らせ情報の一覧を取得する。
     */
    @GET("/notice/get_notice_list")
    public Call<NoticeListDto> getNoticeList();
}
