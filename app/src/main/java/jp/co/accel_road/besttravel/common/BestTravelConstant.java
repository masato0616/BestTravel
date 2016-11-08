package jp.co.accel_road.besttravel.common;

/**
 * 定数クラス
 *
 * Created by masato on 2015/11/22.
 */
public class BestTravelConstant {

    /** 性別コード */
    /** 都道府県コード */
    /** マイルートデフォルト公開範囲コード */
    /** アルバムデフォルト公開範囲コード */

    /** ルート区分コード　マイルート */
    public static final int ROUTE_KBN_CD_MY_ROUTE = 1;
    /** ルート区分コード　お気に入りルート */
    public static final int ROUTE_KBN_CD_FAVORITE_ROUTE = 2;
    /** ルート区分コード　その他ルート */
    public static final int ROUTE_KBN_CD_OTHER_ROUTE = 9;

    /** パラメーターキー　ユーザーID */
    public static final String PARAMETER_KEY_USER_ID = "userId";
    /** パラメーターキー　ルートID */
    public static final String PARAMETER_KEY_ROUTE_ID = "routeId";
    /** パラメーターキー　アカウントID */
    public static final String PARAMETER_KEY_ACCOUNT_ID = "accountId";
    /** パラメーターキー　ルート区分コード */
    public static final String PARAMETER_KEY_ROUTE_KBN_CD = "routeKbnCd";
    /** パラメーターキー　アカウントDTO */
    public static final String PARAMETER_KEY_ACCOUNT_DTO = "accountDto";
    /** パラメーターキー　目的地DTO */
    public static final String PARAMETER_KEY_DESTINATION_DTO = "destinationDto";
    /** パラメーターキー　ルートDTO */
    public static final String PARAMETER_KEY_ROUTE_DTO = "routeDto";
    /** パラメーターキー　日付 */
    public static final String PARAMETER_KEY_DATE = "date";
    /** パラメーターキー　時間 */
    public static final String PARAMETER_KEY_TIME = "time";
    /** パラメーターキー　緯度 */
    public static final String PARAMETER_KEY_LATITUDE = "latitude";
    /** パラメーターキー　経度 */
    public static final String PARAMETER_KEY_LONGITUDE = "longitude";
    /** パラメーターキー　編集コード */
    public static final String PARAMETER_KEY_EDIT_CD = "editCd";
    /** パラメーターキー　参加者リスト */
    public static final String PARAMETER_KEY_ROUTE_PARTICIPANT_LIST = "routeParticipantList";
    /** パラメーターキー　表示日付 */
    public static final String PARAMETER_KEY_DISP_DATE = "dispDate";
    /** パラメーターキー　アルバムデータIndex */
    public static final String PARAMETER_KEY_ALBUM_DATA_INDEX = "albumDataIndex";
    /** パラメーターキー　アルバムデータDTOのリスト */
    public static final String PARAMETER_KEY_ALBUM_DTO = "albumDto";
    /** パラメーターキー　表示文字列 */
    public static final String PARAMETER_KEY_DISP_MESSAGE = "dispMessage";
    /** パラメーターキー　お知らせID */
    public static final String PARAMETER_KEY_NOTICE_ID = "noticeId";

    /** フラグメントキー　ルート追加ダイアログ */
    public static final String FRAGMENT_KEY_ROUTE_ADD_DIALOG = "routeAddDialogFragment";
    /** フラグメントキー　目的地追加ダイアログ */
    public static final String FRAGMENT_KEY_DESTINATION_ADD_DIALOG = "destinationAddDialogFragment";
    /** フラグメントキー　日付入力ダイアログ */
    public static final String FRAGMENT_KEY_DATE_PICKER_DIALOG = "datePickerDialogFragment";
    /** フラグメントキー　時間入力ダイアログ */
    public static final String FRAGMENT_KEY_TIME_PICKER_DIALOG = "timePickerDialogFragment";
    /** フラグメントキー　マップ選択ダイアログ */
    public static final String FRAGMENT_KEY_SELECT_MAP_DIALOG = "selectMapDialogFragment";

    /** アクティビティリクエストキー ルート編集アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_ROUTE_EDIT = 1;
    /** アクティビティリクエストキー 目的地編集アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_DESTINATION_EDIT = 2;
    /** アクティビティリクエストキー ログイン画面アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_LOGIN = 3;
    /** アクティビティリクエストキー 友達検索画面アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_FRIEND_SEARCH = 4;
    /** アクティビティリクエストキー 画像ギャラリーアプリ */
    public static final int ACTIVITY_REQUEST_KEY_IMAGE_GALLERY = 5;
    /** アクティビティリクエストキー 友達選択アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_FRIEND_SELECT = 6;
    /** アクティビティリクエストキー 画像ギャラリーアプリ ヘッダー画像用 */
    public static final int ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_HEADER = 7;
    /** アクティビティリクエストキー 画像ギャラリーアプリ アイコン画像用 */
    public static final int ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_ICON = 8;
    /** アクティビティリクエストキー アルバムデータ詳細画面アクティビティ */
    public static final int ACTIVITY_REQUEST_KEY_ALBUM_DATA_DETAIL = 9;
    /** リソリューションリクエストキー GPS起動 */
    public static final int RESOLUTION_REQUEST_KEY_GPS = 901;

    /** 処理結果 正常終了 */
    public static final int RESULT_CODE_OK = 0;
    /** 処理結果 ワーニング */
    public static final int RESULT_CODE_WORNING = 1;
    /** 処理結果 データ無し */
    public static final int RESULT_CODE_NOTHING = 2;
    /** 処理結果 ブロック */
    public static final int RESULT_CODE_BLOCK = 3;
    /** 処理結果 エラー */
    public static final int RESULT_CODE_ERROR = -1;
    /** 処理結果 トークンエラー */
    public static final int RESULT_CODE_ACCESS_TOKEN_ERROR = -2;
    /** 処理結果 ログイン認証エラー */
    public static final int RESULT_CODE_LOGIN_ERROR = -3;
    /** 処理結果 容量超過エラー */
    public static final int RESULT_CODE_MAX_SIZE_OVER_ERROR = -4;
    /** 処理結果 凍結エラー */
    public static final int RESULT_CODE_FREEZE_ERROR = -5;

    /** 未ログイン時　ログインID */
    public static final long NOT_LOGIN_LOGIN_ID = -1;

    /** 性別　男性 */
    public static final int SEX_CD_MAN = 1;
    /** 性別　女性 */
    public static final int SEX_CD_WOMAN = 2;

    /** マイルート公開範囲コード　全体 */
    public static final int MY_ROUTE_OPEN_RANGE_CD_ALL = 1;
    /** マイルート公開範囲コード　参加者のみ */
    public static final int MY_ROUTE_OPEN_RANGE_CD_PARTICIPANT = 2;
    /** マイルート公開範囲コード　友達のみ */
    public static final int MY_ROUTE_OPEN_RANGE_CD_FRIEND = 3;

    /** アルバム公開範囲コード　全体 */
    public static final int ALBUM_OPEN_RANGE_CD_ALL = 1;
    /** アルバム公開範囲コード　参加者のみ */
    public static final int ALBUM_OPEN_RANGE_CD_PARTICIPANT = 2;
    /** アルバム公開範囲コード　友達のみ */
    public static final int ALBUM_OPEN_RANGE_CD_FRIEND = 3;

    /** 編集コード 追加 */
    public static final int EDIT_CD_ADD = 1;
    /** 編集コード 変更 */
    public static final int EDIT_CD_EDIT = 2;
    /** 編集コード 削除 */
    public static final int EDIT_CD_DELETE = 3;

    /** 送信受信区分 送信 */
    public static final int SEND_RECEIVE_KBN_SEND = 1;
    /** 送信受信区分 受信 */
    public static final int SEND_RECEIVE_KBN_RECEIVE = 2;

    /** 日付入力ダイアログを表示した日付　開始日 */
    public static final int SELECT_DATE_PICKER_DIALOG_START_DATE = 1;
    /** 日付入力ダイアログを表示した日付　終了日 */
    public static final int SELECT_DATE_PICKER_DIALOG_END_DATE = 2;

    /** 表示文字 未設定 */
    public static final String MISETTEI = "未設定";
}
