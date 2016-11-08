package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.entity.AlbumData;
import jp.co.accel_road.besttravel.entity.ChatMessage;
import jp.co.accel_road.besttravel.entity.Destination;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.entity.RouteParticipant;

/**
 * ルートのDTOクラス
 *
 * Created by masato on 2015/11/12.
 */
public class RouteDto extends BaseDto implements Serializable {

    /**
	 * シリアルバージョンUID
	 */
    private static final long serialVersionUID = -7168118544181478854L;

    /** ルートID */
    public Long routeId;
    /** ルートタイトル */
    public String routeTitle;
    /** ヘッダー画像URL */
    public String headerImageUrl;
    /** アイコンURL */
    public String iconUrl;
    /** ルート説明 */
    public String routeDescription;
    /** 開始日 */
    public Date startDate;
    /** 終了日 */
    public Date endDate;
    /** マイルート公開範囲コード */
    public int myRouteOpenRangeCd;
    /** お気に入り数 */
    public int favoriteCount;
    /** 削除フラグ */
    public boolean deleteFlg;
    /** グループチャットコメント数 */
    public int groupChatCommentCount;
    /** 掲示板コメント数 */
    public int bbsCommentCount;
    /** 公開確認フラグ */
    public boolean openConfirmationFlg;
    /** 目的地取得最終日時 */
    public Date destinationGetLastDate;
    /** 更新日時 */
    public Date updateDate;

    /** 参加者リスト */
    public List<AccountDto> participantList;
    /** 目的地のリスト */
    public List<DestinationDto> destinationDtoList;
    /** グループチャットメッセージのリスト */
    public List<ChatMessageDto> chatMessageDtoList;

    /**
     * ルート情報をコピーする
     *
     * @param routeDto ルート情報
     */
    public void copyRouteDto(RouteDto routeDto) {
        // ルートID
        routeId = routeDto.routeId;
        // ルートタイトル
        routeTitle = routeDto.routeTitle;
        // ヘッダー画像URL
        headerImageUrl = routeDto.headerImageUrl;
        // アイコンURL
        iconUrl = routeDto.iconUrl;
        // ルート説明
        routeDescription = routeDto.routeDescription;
        // 開始日
        startDate = routeDto.startDate;
        // 終了日
        endDate = routeDto.endDate;
        // マイルート公開範囲コード
        myRouteOpenRangeCd = routeDto.myRouteOpenRangeCd;
        // お気に入り数
        favoriteCount = routeDto.favoriteCount;
        // グループチャットコメント数
        groupChatCommentCount = routeDto.groupChatCommentCount;
        // 掲示板コメント数
        bbsCommentCount = routeDto.bbsCommentCount;
        // 目的地最終更新日時
        destinationGetLastDate = routeDto.destinationGetLastDate;
        // 更新日時
        updateDate = routeDto.updateDate;
    }

    /**
     * ローカルDBのルート情報を設定する
     *
     * @param route ローカルDBのルート情報
     */
    public void setRoute(Route route) {
        // ルートID
        routeId = route.routeId;
        // ルートタイトル
        routeTitle = route.routeTitle;
        // ヘッダー画像URL
         headerImageUrl = route.thumbnailHeaderImageUrl;
        // アイコンURL
        iconUrl = route.thumbnailIconUrl;
        // ルート説明
        routeDescription = route.routeDescription;
        // 開始日
        startDate = route.startDate;
        // 終了日
        endDate = route.endDate;
        // マイルート公開範囲コード
        myRouteOpenRangeCd = route.myRouteOpenRangeCd;
        // お気に入り数
        favoriteCount = route.favoriteCount;
        // グループチャットコメント数
        groupChatCommentCount = route.groupChatCommentCount;
        // 掲示板コメント数
        bbsCommentCount = route.bbsCommentCount;
        // 目的地最終更新日時
        destinationGetLastDate = route.destinationLastUpdateDate;
        // 更新日時
        updateDate = route.updateDate;
    }

    /**
     * ローカルDBのマイルート情報を取得する
     *
     * @param myAccountId
     * @return ローカルDBのマイルート情報
     */
    public Route getMyRoute(Route routeParam, long myAccountId) {
        Route route = new Route();

        // ルートID
        route.routeId = routeId;
        // マイアカウントID
        route.myAccountId = myAccountId;
        // ルート区分コード
        route.routeKbnCd = BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE;
        // ルートタイトル
        route.routeTitle = routeTitle;
        // ヘッダー画像URL
        route.thumbnailHeaderImageUrl = headerImageUrl;
        // アイコンURL
        route.thumbnailIconUrl = iconUrl;
        // ルート説明
        route.routeDescription = routeDescription;
        // 開始日
        route.startDate = startDate;
        // 終了日
        route.endDate = endDate;
        // マイルート公開範囲コード
        route.myRouteOpenRangeCd = myRouteOpenRangeCd;
        // お気に入り数
        route.favoriteCount = favoriteCount;
        // グループチャットコメント数
        route.groupChatCommentCount = groupChatCommentCount;
        // 掲示板コメント数
        route.bbsCommentCount = bbsCommentCount;

        if (routeParam != null) {
            // 目的地最終更新日時
            route.destinationLastUpdateDate = routeParam.destinationLastUpdateDate;
        } else {
            // 目的地最終更新日時
            route.destinationLastUpdateDate = null;
        }
        // 更新日時
        route.updateDate = updateDate;

        return route;
    }

    /**
     * ローカルDBのお気に入りルート情報を取得する
     *
     * @return ローカルDBのマイルート情報
     */
    public Route getFavoriteRoute(Route routeParam, long myAccountId) {
        Route route = new Route();

        // ルートID
        route.routeId = routeId;
        // マイアカウントID
        route.myAccountId = myAccountId;
        // ルート区分コード
        route.routeKbnCd = BestTravelConstant.ROUTE_KBN_CD_FAVORITE_ROUTE;
        // ルートタイトル
        route.routeTitle = routeTitle;
        // ヘッダー画像URL
        route.thumbnailHeaderImageUrl = headerImageUrl;
        // アイコンURL
        route.thumbnailIconUrl = iconUrl;
        // ルート説明
        route.routeDescription = routeDescription;
        // 開始日
        route.startDate = startDate;
        // 終了日
        route.endDate = endDate;
        // マイルート公開範囲コード
        route.myRouteOpenRangeCd = myRouteOpenRangeCd;
        // お気に入り数
        route.favoriteCount = favoriteCount;
        // グループチャットコメント数
        route.groupChatCommentCount = groupChatCommentCount;
        // 掲示板コメント数
        route.bbsCommentCount = bbsCommentCount;
        if (routeParam != null) {
            // 目的地最終更新日時
            route.destinationLastUpdateDate = routeParam.destinationLastUpdateDate;
        } else {
            // 目的地最終更新日時
            route.destinationLastUpdateDate = null;
        }
        // 更新日時
        route.updateDate = updateDate;

        return route;
    }

    /**
     * ローカルDBのその他ルート情報を取得する
     *
     * @return ローカルDBのマイルート情報
     */
    public Route getOtherRoute(long myAccountId) {
        Route route = new Route();

        // ルートID
        route.routeId = routeId;
        // マイアカウントID
        route.myAccountId = myAccountId;
        // ルート区分コード
        route.routeKbnCd = BestTravelConstant.ROUTE_KBN_CD_OTHER_ROUTE;
        // ルートタイトル
        route.routeTitle = routeTitle;
        // ヘッダー画像URL
        route.thumbnailHeaderImageUrl = headerImageUrl;
        // アイコンURL
        route.thumbnailIconUrl = iconUrl;
        // ルート説明
        route.routeDescription = routeDescription;
        // 開始日
        route.startDate = startDate;
        // 終了日
        route.endDate = endDate;
        // マイルート公開範囲コード
        route.myRouteOpenRangeCd = myRouteOpenRangeCd;
        // お気に入り数
        route.favoriteCount = favoriteCount;
        // グループチャットコメント数
        route.groupChatCommentCount = groupChatCommentCount;
        // 掲示板コメント数
        route.bbsCommentCount = bbsCommentCount;
        // 目的地最終更新日時
        route.destinationLastUpdateDate = null;
        // 更新日時
        route.updateDate = updateDate;

        return route;
    }

    /**
     * ローカルDBのルート参加者情報を設定する
     *
     * @param routeParticipantList
     */
    public void setParticipantList(List<RouteParticipant> routeParticipantList) {

        //マップが存在しない場合は、新規作成
        if (participantList == null) {
            participantList = new ArrayList<>();
        } else {
            participantList.clear();
        }

        //リストの値を格納
        for (RouteParticipant routeParticipant: routeParticipantList) {
            AccountDto accountDto = new AccountDto();
            accountDto.setRouteParticipant(routeParticipant);
            participantList.add(accountDto);
        }
    }

    /**
     * ローカルDBの目的地情報を設定する
     *
     * @param destinationList
     */
    public void setDestinationDtoList(List<Destination> destinationList) {

        //リストが存在しない場合は、新規作成
        if (destinationDtoList == null) {
            destinationDtoList = new ArrayList<>();
        } else {
            destinationDtoList.clear();
        }

        //リストの値を格納
        for (Destination destination: destinationList) {
            DestinationDto destinationDto = new DestinationDto();
            destinationDto.setDestination(destination);
            destinationDtoList.add(destinationDto);
        }
    }

    /**
     * ローカルDBのアルバムデータ情報を設定する
     *
     * @param albumDataList
     */
    public void setAlbumDataDtoList(List<AlbumData> albumDataList) {

        //目的地のリストが存在しない場合は、処理を終了する
        if (destinationDtoList == null || destinationDtoList.isEmpty()) {
            return;
        }

        //リストの値を格納
        for (DestinationDto destinationDto: destinationDtoList) {

            destinationDto.albumDataDtoList.clear();

            for (AlbumData albumData: albumDataList) {
                if(destinationDto.destinationId.longValue() != albumData.destinationId) {
                    continue;
                }
                AlbumDataDto albumDataDto = new AlbumDataDto();
                albumDataDto.setAlbumData(albumData);
                destinationDto.albumDataDtoList.add(albumDataDto);
            }
        }
    }

    /**
     * ローカルDBのチャット情報を設定する
     *
     * @param chatMessageList
     */
    public void setChatMessageDtoList(List<ChatMessage> chatMessageList) {
        if (chatMessageDtoList == null) {
            chatMessageDtoList = new ArrayList<>();
        } else {
            chatMessageDtoList.clear();
        }

        for (ChatMessage chatMessage: chatMessageList) {
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setChatMessage(chatMessage);
            chatMessageDtoList.add(chatMessageDto);
        }
    }
}
