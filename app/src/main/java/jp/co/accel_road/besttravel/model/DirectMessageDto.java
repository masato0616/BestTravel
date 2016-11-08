package jp.co.accel_road.besttravel.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.accel_road.besttravel.entity.DirectMessage;
import jp.co.accel_road.besttravel.entity.DirectMessageAccount;

/**
 * ダイレクトメッセージのDTO
 *
 * Created by masato on 2015/11/12.
 */
public class DirectMessageDto extends BaseDto implements Serializable {

	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 3022881581509739753L;

	/** ダイレクトメッセージID */
	public Long directMessageId;
	/** アカウントID */
	public Long accountId;
	/** ユーザーID */
	public String userId;
	/** ユーザー名 */
	public String userName;
	/** ユーザーアイコンURL */
	public String userIconUrl;
	/** 送信・受信区分 */
	public Integer sendReceiveKbn;
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
	 * ダイレクトメッセージアカウントの情報を設定する
	 *
	 * @param directMessageAccount
     */
	public void setDirectMessageAccount(DirectMessageAccount directMessageAccount) {

		// アカウントID
		accountId = directMessageAccount.accountId;
		// ユーザーID
		userId = directMessageAccount.userId;
		// ユーザー名
		userName = directMessageAccount.userName;
		// ユーザーアイコンURL
		userIconUrl = directMessageAccount.thumbnailUserIconUrl;
		// 最後に送受信したメッセージ
		message = directMessageAccount.lastMessage;
		// 最終送受信日時
		transceiverDate = directMessageAccount.lastTransceiverDate;
		// 更新日時
		updateDate = directMessageAccount.updateDate;
	}

	/**
	 * ダイレクトメッセージアカウントの情報を取得する
	 *
	 * @param myAccountId
	 * @return ダイレクトメッセージアカウント
	 */
	public DirectMessageAccount getDirectMessageAccount(long myAccountId) {

		DirectMessageAccount directMessageAccount = new DirectMessageAccount();

		// マイアカウントID
		directMessageAccount.myAccountId = myAccountId;
		// アカウントID
		directMessageAccount.accountId = accountId;
		// ユーザーID
		directMessageAccount.userId = userId;
		// ユーザー名
		directMessageAccount.userName = userName;
		// ユーザーアイコンURL
		directMessageAccount.thumbnailUserIconUrl = userIconUrl;
		// 最後に送受信したメッセージ
		directMessageAccount.lastMessage = message;
		// 最終送受信日時
		directMessageAccount.lastTransceiverDate = transceiverDate;
		// 更新日時
		directMessageAccount.updateDate = updateDate;

		return directMessageAccount;
	}

	/**
	 * アカウントDTOの情報を取得する
	 *
	 * @return アカウントDTO
	 */
	public AccountDto getAccountDto() {

		AccountDto accountDto = new AccountDto();

		// アカウントID
		accountDto.accountId = accountId;
		// ユーザーID
		accountDto.userId = userId;
		// ユーザー名
		accountDto.userName = userName;
		// ユーザーアイコンURL
		accountDto.thumbnailIconUrl = userIconUrl;

		return accountDto;
	}

	/**
	 * ダイレクトメッセージの情報を設定する
	 *
	 * @param directMessage ダイレクトメッセージ
	 */
	public void setDirectMessage(DirectMessage directMessage) {

		// ダイレクトメッセージID
		directMessageId = directMessage.directMessageId;
		// アカウントID
		accountId = directMessage.accountId;
		// 送信・受信区分
		sendReceiveKbn = directMessage.sendReceiveKbn;
		// メッセージ
		message = directMessage.message;
		// 送信したファイルを取得する際のURL
		fileUrl = directMessage.fileUrl;
		// サムネイルファイルUrl
		thumbnailFileUrl = directMessage.thumbnailFileUrl;
		// 送受信日時
		transceiverDate = directMessage.transceiverDate;
		// 更新日時
		updateDate = directMessage.updateDate;
	}

	/**
	 * ダイレクトメッセージの情報を取得する
	 *
	 * @param myAccountId
	 * @return ダイレクトメッセージ
	 */
	public DirectMessage getDirectMessage(long myAccountId) {

		DirectMessage directMessage = new DirectMessage();

		// ダイレクトメッセージID
		directMessage.directMessageId = directMessageId;
		// マイアカウントID
		directMessage.myAccountId = myAccountId;
		// アカウントID
		directMessage.accountId = accountId;
		// 送信・受信区分
		directMessage.sendReceiveKbn = sendReceiveKbn;
		// メッセージ
		directMessage.message = message;
		// 送信したファイルを取得する際のURL
		directMessage.fileUrl = fileUrl;
		// サムネイルファイルUrl
		directMessage.thumbnailFileUrl = thumbnailFileUrl;
		// 送受信日時
		directMessage.transceiverDate = transceiverDate;
		// 更新日時
		directMessage.updateDate = updateDate;

		return directMessage;
	}
}
