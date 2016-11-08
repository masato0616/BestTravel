package jp.co.accel_road.besttravel.listener;

import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.model.ChatMessageDto;
import jp.co.accel_road.besttravel.model.DestinationDto;

/**
 * ルート変更時の処理を行うリスナー
 *
 * Created by masato on 2015/12/30.
 */
public interface RouteListener
{
    /** 現在表示している日付の目的地のリストの中から、指定した目的地IDの目的地を取得する */
    public DestinationDto getDispDestinationDto(Long destinationId);

    /** 現在表示している日付の目的地のリストを取得する */
    public List<DestinationDto> getDispDestinationDtoList();

    /** ルート情報を最新化する */
    public void refreshLatestRoute();

    /** 現在表示している表示日付を取得する */
    public Date getDispDate();

    /** 現在表示している表示日付を変更する */
    public void changeDispDate(Date newDispDate);

    /** ルートIDを取得する */
    public Long getRouteId();

    /** ルート区分を取得する */
    public int getRouteKbnCd();

    /** チャットメッセージのリストを取得する */
    public List<ChatMessageDto> getChatMessageDtoList();

    /** 開始日を取得する */
    public Date getStartDate();

    /** 終了日を取得する */
    public Date getEndDate();
}
