package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.entity.ChatMessage;
import jp.co.accel_road.besttravel.entity.MyAccount;

/**
 * グループチャットメッセージのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class ChatMessageDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 363703468215919131L;

	/** チャットメッセージID */
	public Long chatMessageId;
	/** 送信ユーザーID */
	public String sendUserId;
	/** 送信ユーザー名 */
	public String sendUserName;
	/** 送信ユーザーアイコンURL */
	public String sendUserIconUrl;
	/** ルートID */
	public Long routeId;
	/** メッセージ */
	public String message;
	/** 送信したファイルを取得する際のURL */
	public String fileUrl;
	/** サムネイルファイルUrl */
	public String thumbnailFileUrl;
	/** 送受信日時 */
	public Date transceiverDate;
	/** 更新日時 */
	public Date updateDate;

	/**
	 * ローカルDBのチャットメッセージを取得する
	 *
	 * @return
     */
	public ChatMessage getChatMessage(MyAccount myAccount) {
		ChatMessage chatMessage = new ChatMessage();

		//チャットメッセージID
		chatMessage.chatMessageId = chatMessageId;
		// マイアカウントID
		chatMessage.myAccountId = myAccount.accountId;
		// 送信ユーザーID
		chatMessage.sendUserId = sendUserId;
		// 送信ユーザー名
		chatMessage.sendUserName = sendUserName;
		// アイコンURL
		chatMessage.thumbnailSendUserIconUrl = sendUserIconUrl;
		// ルートID
		chatMessage.routeId = routeId;
		// メッセージ
		chatMessage.message = message;
		// 送信したファイルを取得する際のURL
		chatMessage.fileUrl = fileUrl;
		// サムネイルファイルUrl
		chatMessage.thumbnailFileUrl = thumbnailFileUrl;
		//送受信日時
		chatMessage.transceiverDate = transceiverDate;
		// 更新日時
		chatMessage.updateDate = updateDate;

		return chatMessage;
	}

	/**
	 * ローカルDBのチャットメッセージを設定する
	 *
	 * @param chatMessage チャットメッセージ
	 */
	public void setChatMessage(ChatMessage chatMessage) {

		//チャットメッセージID
		chatMessageId = chatMessage.chatMessageId;
		// 送信ユーザーID
		sendUserId = chatMessage.sendUserId;
		// 送信ユーザー名
		sendUserName = chatMessage.sendUserName;
		// アイコンURL
		sendUserIconUrl = chatMessage.thumbnailSendUserIconUrl;
		// ルートID
		routeId = chatMessage.routeId;
		// メッセージ
		message = chatMessage.message;
		// 送信したファイルを取得する際のURL
		fileUrl = chatMessage.fileUrl;
		// サムネイルファイルUrl
		thumbnailFileUrl = chatMessage.thumbnailFileUrl;
		//送受信日時
		transceiverDate = chatMessage.transceiverDate;
		// 更新日時
		updateDate = chatMessage.updateDate;
	}
}
