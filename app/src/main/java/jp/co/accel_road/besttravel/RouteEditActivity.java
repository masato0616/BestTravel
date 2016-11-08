package jp.co.accel_road.besttravel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.AlbumDataDao;
import jp.co.accel_road.besttravel.dao.ChatMessageDao;
import jp.co.accel_road.besttravel.dao.DestinationDao;
import jp.co.accel_road.besttravel.dao.RouteDao;
import jp.co.accel_road.besttravel.dao.RouteParticipantDao;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.entity.RouteParticipant;
import jp.co.accel_road.besttravel.fragment.DatePickerDialogFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.model.RouteSendFileDto;
import jp.co.accel_road.besttravel.service.RouteService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ルートを編集するアクティビティ
 */
public class RouteEditActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    /** ヘッダー画像 */
    private ImageView imgHeader;
    /** アイコン画像 */
    private ImageView imgIcon;
    /** ルート名 */
    private EditText txtRouteTitle;
    /** 滞在開始日付 */
    private Button btnStayStartDate;
    /** 滞在終了日付 */
    private Button btnStayEndDate;
    /** 旅の参加者一覧*/
    private GridView grdParticipantList;
    /** 旅の参加者選択ボタン */
    private Button btnSelectParticipant;
    /** 公開範囲 */
    private RadioGroup rdoMyRouteOpenRangeCd;
    /** 旅の説明 */
    private EditText txtDescription;
    /** 削除ボタン */
    private Button btnDelete;
    /** キャンセルボタン */
    private Button btnCancel;
    /** 登録ボタン */
    private Button btnResist;
    /** 日付入力ダイアログを表示した日付がどっちなのか判断する区分 */
    private int selectDatePickerDialog;

    /** 旅の参加者リスト */
    private List<AccountDto> participantList;
    /** 旅の参加者一覧のアダプター*/
    private ParticipantListAdapter participantListAdapter;

    /** ルートID（更新処理の場合のみ設定される） */
    private Long routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_edit);

        //ツールバーの設定
        setToolbar();

        //ビューの作成を行う
        createView();

        //旅の参加者リストの初期化
        participantList = new ArrayList<>();

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        RouteDto routeDto = (RouteDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO);

        //画面の項目に値を設定
        setDispValue(routeDto);

        //編集の場合は、旅の参加者リストをローカルDBより取得する。
        if (routeDto != null) {
            //旅の参加者リストに値を設定
            RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
            List<RouteParticipant> routeParticipantList = routeParticipantDao.getRouteParticipantList(routeDto.routeId, myAccount.accountId);
            for (RouteParticipant routeParticipant: routeParticipantList) {
                AccountDto participant = new AccountDto();
                participant.setRouteParticipant(routeParticipant);
                participantList.add(participant);
            }
        } else {
            //新規の場合は、自分自身を追加する
            AccountDto accountDto = new AccountDto();
            accountDto.setMyAccount(myAccount);
            participantList.add(accountDto);
        }

        // 編集の場合は、値を設定する
        if (routeDto != null) {
            //ルートの詳細情報を取得する
            getEditRouteDetail();
        }

        //ヘッダー画像
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像押下時処理
                //画像選択のためのアプリを表示
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }
                startActivityForResult(Intent.createChooser(intent,"select"), BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_HEADER);
            }
        });

        //アイコン画像
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像押下時処理
                //画像選択のためのアプリを表示
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }
                startActivityForResult(Intent.createChooser(intent,"select"), BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_ICON);
            }
        });

        //滞在開始日付入力
        btnStayStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDatePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_START_DATE;
                //日付入力ダイアログを表示
                dispDatePickerDialog(BestTravelUtil.convertStringToDate(((Button) v).getText().toString()));
            }
        });

        //滞在終了日付入力
        btnStayEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDatePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_END_DATE;
                //日付入力ダイアログを表示
                dispDatePickerDialog(BestTravelUtil.convertStringToDate(((Button) v).getText().toString()));
            }
        });

        //旅の参加者一覧のアダプター設定
        participantListAdapter = new ParticipantListAdapter();
        grdParticipantList.setAdapter(participantListAdapter);

        // 旅の参加者選択ボタン
        btnSelectParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //友達選択画面を表示
                Intent intent = new Intent(getApplicationContext(), FriendSelectActivity.class);
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_PARTICIPANT_LIST, (ArrayList)participantList);
                startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_FRIEND_SELECT);
            }
        });

        //削除ボタン押下
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ルート情報を削除する
                deleteRoute();
            }
        });

        //キャンセルボタン押下
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                //アクティビティを終了
                finish();
            }
        });

        //登録ボタン押下
        btnResist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ルートIDが設定されていない場合は、新規登録する
                if (routeId == null) {
                    registRoute();
                } else {
                    //更新する
                    updateRoute();
                }
            }
        });
    }

    /**
     * ルートの詳細情報を取得する
     */
    private void getEditRouteDetail() {

        //マイルートを取得
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.getEditRouteDetail(routeId, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {
                RouteDto routeDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイアカウントを再取得する
                        getEditRouteDetail();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(routeDto.resultCode, listener)) {
                    return ;
                }

                //画面の項目に値を設定
                setDispValue(routeDto);

                //参加者のリストを設定
                if (routeDto.participantList != null) {
                    participantList = routeDto.participantList;
                    participantListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {

            }
        });
    }

    /**
     * ルート情報を削除する
     */
    private void deleteRoute() {

        //ルート情報を削除
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.deleteRoute(routeId, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {
                RouteDto routeDto = response.body();

                //ローカルDBからも削除する

                //参加者情報の削除
                RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
                routeParticipantDao.deleteRouteParticipant(routeDto.routeId, myAccount.accountId);

                //目的地情報の削除
                DestinationDao destinationDao = new DestinationDao();
                destinationDao.deleteDestinationByRouteId(routeDto.routeId, myAccount.accountId);

                //アルバム情報の削除
                AlbumDataDao albumDataDao = new AlbumDataDao();
                albumDataDao.deleteAlbumDataByRouteId(routeDto.routeId, myAccount.accountId);

                //チャット情報の削除
                ChatMessageDao chatMessageDao = new ChatMessageDao();
                chatMessageDao.deleteChatMessage(routeDto.routeId, myAccount.accountId);

                //ルート情報の削除
                RouteDao routeDao = new RouteDao();
                routeDao.deleteRoute(routeDto.routeId, myAccount.accountId);

                setResult(Activity.RESULT_OK);
                //アクティビティを終了
                finish();
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {

            }
        });
    }

    /**
     * 画面に表示する値を設定する
     *
     * @param routeDto
     */
    private void setDispValue(RouteDto routeDto) {

        if (routeDto == null) {

            //ルートID
            routeId = null;
            //ルート名
            txtRouteTitle.setText("");

            //滞在開始日付
            btnStayStartDate.setText(BestTravelUtil.convertDateToString(new Date()));
            //滞在終了日付
            btnStayEndDate.setText(BestTravelUtil.convertDateToString(new Date()));
            //公開範囲
            setMyRouteOpenRangeCd(BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_PARTICIPANT);
            //メモ
            txtDescription.setText("");
        } else {
            //ルートID
            routeId = routeDto.routeId;
            //ヘッダー画像
            Picasso.with(this).load(routeDto.headerImageUrl).fit().centerInside().into(imgHeader);
            //アイコン画像
            Picasso.with(this).load(routeDto.iconUrl).fit().centerInside().into(imgIcon);
            //ルートタイトル
            txtRouteTitle.setText(routeDto.routeTitle);
            //滞在開始日付
            btnStayStartDate.setText(BestTravelUtil.convertDateToString(routeDto.startDate));
            //滞在終了日付
            btnStayEndDate.setText(BestTravelUtil.convertDateToString(routeDto.endDate));
            //公開範囲
            setMyRouteOpenRangeCd(routeDto.myRouteOpenRangeCd);
            //メモ
            txtDescription.setText(routeDto.routeDescription);
        }
    }

    /**
     * ルート情報を登録する
     */
    private void registRoute() {
        //ルート情報をサーバーに登録する
        RouteDto routeDto = new RouteDto();
        //ルートID
        //ルートタイトル
        routeDto.routeTitle = txtRouteTitle.getText().toString();
        //ヘッダー画像URL
        //アイコンURL
        //ルート説明
        routeDto.routeDescription = txtDescription.getText().toString();
        //開始日
        String stayStartTime = btnStayStartDate.getText().toString();
        routeDto.startDate = BestTravelUtil.convertStringToDate(stayStartTime);
        //終了日
        String stayEndTime = btnStayEndDate.getText().toString();
        routeDto.endDate = BestTravelUtil.convertStringToDate(stayEndTime);
        //マイルート公開範囲コード
        routeDto.myRouteOpenRangeCd = getMyRouteOpenRangeCd();
        //参加者リスト
        routeDto.participantList = participantList;

        //ルート情報を保存
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.insertRoute(routeDto, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //サーバーより取得したアカウント
                RouteDto routeDto = response.body();

                //正常終了しなかった場合
                if (BestTravelConstant.RESULT_CODE_OK != routeDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_save_route));
                    return;
                }
                //ローカルのルート情報を登録する
                Route route = routeDto.getMyRoute(null, myAccount.accountId);
                RouteDao routeDao = new RouteDao();
                routeDao.insertRoute(route);

                //参加者情報をローカルDBに削除し追加する
                RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
                //参加者情報の登録
                for (AccountDto participant: routeDto.participantList) {
                    RouteParticipant routeParticipant = participant.getRouteParticipant(myAccount);
                    routeParticipant.routeId = routeDto.routeId;
                    routeParticipantDao.insertRouteParticipant(routeParticipant);
                }

                //ヘッダー画像または、アイコン画像が変更されている場合
                if (imgHeader.getTag()!= null || imgIcon.getTag() != null) {
                    //ヘッダー・アイコン画像の保存
                    updateImage();
                } else {

                    setResult(Activity.RESULT_OK);
                    //アクティビティを終了
                    finish();
                }

            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {

            }
        });
    }

    /**
     * ルート情報を更新する
     */
    private void updateRoute() {
        //ルート情報をサーバーに登録する
        RouteDto routeDto = new RouteDto();
        //ルートID
        routeDto.routeId = routeId;
        //ルートタイトル
        routeDto.routeTitle = txtRouteTitle.getText().toString();
        //ヘッダー画像URL
        //アイコンURL
        //ルート説明
        routeDto.routeDescription = txtDescription.getText().toString();
        //開始日
        String stayStartTime = btnStayStartDate.getText().toString();
        routeDto.startDate = BestTravelUtil.convertStringToDate(stayStartTime);
        //終了日
        String stayEndTime = btnStayEndDate.getText().toString();
        routeDto.endDate = BestTravelUtil.convertStringToDate(stayEndTime);
        //マイルート公開範囲コード
        routeDto.myRouteOpenRangeCd = getMyRouteOpenRangeCd();
        //参加者リスト
        routeDto.participantList = participantList;

        //ルート情報を保存
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.updateRoute(routeDto, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //サーバーより取得したアカウント
                RouteDto routeDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //ルート情報を再更新する
                        updateRoute();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(routeDto.resultCode, listener)) {
                    return ;
                }

                //正常終了しなかった場合
                if (BestTravelConstant.RESULT_CODE_OK != routeDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_save_route));
                    return;
                }

                //ローカルのルート情報を登録する
                RouteDao routeDao = new RouteDao();
                Route routeDb = routeDao.getRoute(routeDto.routeId, myAccount.accountId);
                Route route = routeDto.getMyRoute(routeDb, myAccount.accountId);
                routeDao.updateRoute(route);

                //参加者情報をローカルDBに削除し追加する
                RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
                //参加者情報の削除
                routeParticipantDao.deleteRouteParticipant(routeId, myAccount.accountId);

                //参加者情報の登録
                for (AccountDto participant: routeDto.participantList) {
                    RouteParticipant routeParticipant = participant.getRouteParticipant(myAccount);
                    routeParticipant.routeId = routeId;
                    routeParticipantDao.insertRouteParticipant(routeParticipant);
                }

                //ヘッダー画像または、アイコン画像が変更されている場合
                if (imgHeader.getTag()!= null || imgIcon.getTag() != null) {
                    //ヘッダー・アイコン画像の保存
                    updateImage();
                } else {
                    setResult(Activity.RESULT_OK);
                    //アクティビティを終了
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {

            }
        });
    }

    /**
     * アイコン画像ヘッダー画像のアップデートを行う。
     *
     * @return true:正常終了／false:アイコン画像なし
     */
    private void updateImage() {

        //ヘッダー画像の送信設定
        MultipartBody.Part updateHeaderImagePart = null;

        Uri headerFilePassUri = (Uri)imgHeader.getTag();
        //ファイルパスを取得
        String headerFilePass = BestTravelUtil.getFilePath(headerFilePassUri, getContentResolver());

        //パスが取得できなかった場合は、処理を終了する
        if (headerFilePass != null && !"".equals(headerFilePass)) {
            RequestBody updateHeaderImageRequestBody = BestTravelUtil.getImageRequestBody((Uri)imgHeader.getTag(), getContentResolver());

            updateHeaderImagePart =
                    MultipartBody.Part.createFormData("headerImageFile", headerFilePass, updateHeaderImageRequestBody);

        }

        //アイコン画像の送信設定
        MultipartBody.Part updateIconImagePart = null;

        Uri iconFilePassUri = (Uri)imgIcon.getTag();
        //ファイルパスを取得
        String iconFilePass = BestTravelUtil.getFilePath(iconFilePassUri, getContentResolver());

        //パスが取得できなかった場合は、処理を終了する
        if (iconFilePass != null && !"".equals(iconFilePass)) {
            RequestBody updateIconImageRequestBody = BestTravelUtil.getImageRequestBody((Uri)imgIcon.getTag(), getContentResolver());

            updateIconImagePart =
                    MultipartBody.Part.createFormData("iconImageFile", iconFilePass, updateIconImageRequestBody);

        }

        //画像がどちらも設定されていない場合は、処理を終了する
        if (updateHeaderImagePart == null && updateIconImagePart == null) {
            return;
        }

        //送信ファイルの説明
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        //アイコン画像の更新処理を行う
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteSendFileDto> reps = routeService.updateRouteHeaderIconImage(routeId, description, updateHeaderImagePart, updateIconImagePart, myAccount.accessToken);
        reps.enqueue(new Callback<RouteSendFileDto>() {
            @Override
            public void onResponse(Call<RouteSendFileDto> call, Response<RouteSendFileDto> response) {

                //サーバーより取得したアカウント
                RouteSendFileDto routeSendFileDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //アイコン画像ヘッダー画像を再登録する
                        updateImage();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(routeSendFileDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == routeSendFileDto.resultCode) {

                    //ルート情報を更新
                    RouteDao routeDao = new RouteDao();
                    //端末に登録されているルート情報
                    Route routeLocal = routeDao.getRoute(routeSendFileDto.routeId, myAccount.accountId);
                    //サーバーより受信したヘッダーのURL
                    routeLocal.thumbnailHeaderImageUrl = routeSendFileDto.headerFileUrl;
                    //サーバーより受信したアイコンのURL
                    routeLocal.thumbnailIconUrl = routeSendFileDto.iconFileUrl;
                    routeDao.updateRoute(routeLocal);

                    setResult(Activity.RESULT_OK);
                    //アクティビティを終了
                    finish();
                } else {
                    //エラーの場合
                    dispToast(getResources().getString(R.string.error_save_image));
                }
            }

            @Override
            public void onFailure(Call<RouteSendFileDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
        return;
    }


    /**
     * 子画面からの戻り時処理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //友達選択画面からの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_FRIEND_SELECT:
                //正常終了した場合
                if (RESULT_OK == resultCode) {
                    Object participantListObj = data.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_PARTICIPANT_LIST);
                    if (participantListObj != null) {
                        participantList = (ArrayList<AccountDto>)participantListObj;
                    } else {
                        participantList.clear();
                    }

                    //自分自身を追加する
                    AccountDto accountDto = new AccountDto();
                    accountDto.setMyAccount(myAccount);
                    participantList.add(accountDto);

                    participantListAdapter.notifyDataSetChanged();
                }
                break;

            //画像ギャラリーアプリからの戻り時（ヘッダー）処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_HEADER:
                if (resultCode == RESULT_OK) {
                    //アイコン画像に設定
                    Picasso.with(getApplicationContext()).load(data.getData()).fit().centerInside().into(imgHeader);
                    //タグに画像のファイルパスを設定
                    imgHeader.setTag(data.getData());
                }
                break;

            //画像ギャラリーアプリからの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY_ICON:
                if (resultCode == RESULT_OK) {
                    //アイコン画像に設定
                    Picasso.with(getApplicationContext()).load(data.getData()).fit().centerInside().into(imgIcon);
                    //タグに画像のファイルパスを設定
                    imgIcon.setTag(data.getData());
                }
                break;
        }
    }

    /**
     * 画面に表示するビューを作成する。
     */
    private void createView() {

        // ヘッダー画像
        imgHeader = (ImageView)findViewById(R.id.imgHeader);
        // アイコン画像
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        //ルート名
        txtRouteTitle = (EditText)findViewById(R.id.txtRouteTitle);
        //ルート名
        txtRouteTitle = (EditText)findViewById(R.id.txtRouteTitle);
        //滞在開始日付
        btnStayStartDate = (Button)findViewById(R.id.btnStayStartDate);
        //滞在終了日付
        btnStayEndDate = (Button)findViewById(R.id.btnStayEndDate);
        // 旅の参加者一覧
        grdParticipantList = (GridView)findViewById(R.id.grdParticipantList);
        // 旅の参加者選択ボタン
        btnSelectParticipant = (Button)findViewById(R.id.btnSelectParticipant);

        //公開範囲
        rdoMyRouteOpenRangeCd = (RadioGroup)findViewById(R.id.rdoMyRouteOpenRangeCd);
        //メモ
        txtDescription = (EditText)findViewById(R.id.txtDescription);
        //削除ボタン
        btnDelete = (Button)findViewById(R.id.btnDelete);
        //キャンセルボタン
        btnCancel = (Button)findViewById(R.id.btnCancel);
        //登録ボタン
        btnResist = (Button)findViewById(R.id.btnResist);
    }

    /**
     * コード値を元に画面にて選択するラジオボタンのIDを設定する。
     *
     * @return
     */
    private void setMyRouteOpenRangeCd(int id) {

        if (BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_PARTICIPANT == id) {
            rdoMyRouteOpenRangeCd.check(R.id.rdoMyRouteOpenRangeCdParticipant);
        } else if (BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_FRIEND == id) {
            rdoMyRouteOpenRangeCd.check(R.id.rdoMyRouteOpenRangeCdFriend);
        }
        rdoMyRouteOpenRangeCd.check(R.id.rdoMyRouteOpenRangeCdAll);
    }

    /**
     * コード値を元に画面にて選択するラジオボタンのIDを取得する。
     *
     * @return
     */
    private int getMyRouteOpenRangeCd() {

        int checkedId = rdoMyRouteOpenRangeCd.getCheckedRadioButtonId();
        if (R.id.rdoMyRouteOpenRangeCdParticipant == checkedId) {
            return BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_PARTICIPANT;
        } else if (R.id.rdoMyRouteOpenRangeCdFriend == checkedId) {
            return BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_FRIEND;
        }

        return BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_ALL;
    }


    /**
     * 日付入力ダイアログを表示する。
     * @param date
     */
    private void dispDatePickerDialog(Date date) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BestTravelConstant.PARAMETER_KEY_DATE, date);
        datePickerDialogFragment.setArguments(args);
        datePickerDialogFragment.show(getSupportFragmentManager(), BestTravelConstant.FRAGMENT_KEY_DATE_PICKER_DIALOG);
    }
    /**
     * 日付のダイアログでの戻り時処理
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        if (selectDatePickerDialog == BestTravelConstant.SELECT_DATE_PICKER_DIALOG_START_DATE) {
            btnStayStartDate.setText(BestTravelUtil.convertDateToString(calendar.getTime()));
        } else {
            btnStayEndDate.setText(BestTravelUtil.convertDateToString(calendar.getTime()));
        }
    }

    /**
     * 旅の参加者リストのアダプタークラス
     */
    public class ParticipantListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return participantList.size();
        }

        @Override
        public Object getItem(int i) {
            return participantList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.view_participant_item, viewGroup, false);
            }

            //友達情報の取得
            AccountDto accountDto = participantList.get(i);

            // アイコン画像
            ImageView imgIcon = (ImageView)view.findViewById(R.id.imgIcon);
            Picasso.with(getApplicationContext()).load(accountDto.thumbnailIconUrl).fit().centerInside().into(imgIcon);

            // 名前
            TextView lblUserName = (TextView)view.findViewById(R.id.lblUserName);
            lblUserName.setText(accountDto.userName);

            //リスト選択
            view.setTag(accountDto);
            //クリック時の処理
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //アカウント詳細を表示
                    Intent intent = new Intent(v.getContext(), AccountDetailActivity.class);
                    //アカウントDTO
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, (AccountDto)v.getTag());
                    startActivity(intent);
                }
            });

            return view;
        }
    }

}
