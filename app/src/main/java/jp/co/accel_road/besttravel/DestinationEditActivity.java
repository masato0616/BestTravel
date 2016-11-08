package jp.co.accel_road.besttravel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.fragment.DatePickerDialogFragment;
import jp.co.accel_road.besttravel.fragment.TimePickerDialogFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.OnSelectMapListener;
import jp.co.accel_road.besttravel.model.AlbumDataDto;
import jp.co.accel_road.besttravel.model.AlbumDto;
import jp.co.accel_road.besttravel.model.DestinationDto;
import jp.co.accel_road.besttravel.model.DestinationSendFileDto;
import jp.co.accel_road.besttravel.service.DestinationService;
import jp.co.accel_road.besttravel.view.AllDispGridView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 目的地を編集するアクティビティ
 */
public class DestinationEditActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, OnSelectMapListener {

    /** 目的地名 */
    private EditText txtDestinationName;
    /** 滞在開始時間 */
    private Button btnStayStartDate;
    private Button btnStayStartTime;
    /** 滞在終了時間 */
    private Button btnStayEndDate;
    private Button btnStayEndTime;
    /** 住所 */
    private EditText txtAddress;
    /** メモ */
    private EditText txtMemo;
    /** 非公開フラグ */
    private CheckBox chkPrivateDestination;
    /** アルバム追加ボタン */
    private Button btnAddAlbum;
    /** アルバム一覧 */
    private AllDispGridView grdAlbumList;
    private AlbumListAdapter albumListAdapter;
    /** 削除ボタン */
    private Button btnDelete;
    /** キャンセルボタン */
    private Button btnCancel;
    /** 登録ボタン */
    private Button btnResist;

    /** 対象となる目的地 */
    private DestinationDto destinationDto;
    /** 対象となるアルバムデータのリスト */
    private List<AlbumDataDto> albumDataDtoList;

    /** 日付入力ダイアログを表示した日付がどっちなのか判断する区分 */
    private int selectDatePickerDialog;
    /** 時間入力ダイアログを表示した日付がどっちなのか判断する区分 */
    private int selectTimePickerDialog;

    /** 緯度・経度 */
    private LatLng latLng;

    /** 現在表示している目的地のルートID */
    private Long routeId;

    /** 登録時に追加対象となったアルバムデータ */
    private List<AlbumDataDto> addAlbumDataDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_edit);
        //ツールバーの設定
        setToolbar();

        //ビューの作成を行う
        createView();

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        //目的地を取得
        destinationDto = (DestinationDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_DESTINATION_DTO);
        //ルートIDを取得
        routeId = (Long)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_ID);
        //表示日付を取得
        Date dispDate = (Date)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_DISP_DATE);

        if (destinationDto == null) {
            destinationDto = new DestinationDto();
        }

        //目的地名
        txtDestinationName.setText(destinationDto.destinationName);
        //滞在開始時間
        if (destinationDto.startDate != null) {
            btnStayStartDate.setText(BestTravelUtil.convertDateToString(destinationDto.startDate));
            btnStayStartTime.setText(destinationDto.startTime);
        } else {
            btnStayStartDate.setText(BestTravelUtil.convertDateToString(dispDate));
        }

        //滞在終了時間
        if (destinationDto.endDate != null) {
            btnStayEndDate.setText(BestTravelUtil.convertDateToString(destinationDto.endDate));
            btnStayEndTime.setText(destinationDto.endTime);
        } else {
            btnStayEndDate.setText(BestTravelUtil.convertDateToString(dispDate));
        }
        //住所
        txtAddress.setText(destinationDto.address);
        //緯度
        //経度
        if (destinationDto.latitude != null && destinationDto.longitude != null) {
            latLng = new LatLng(destinationDto.latitude, destinationDto.longitude);
        }
        //メモ
        txtMemo.setText(destinationDto.memo);
        //非公開フラグ
        chkPrivateDestination.setClickable(destinationDto.privateDestinationFlg);

        btnStayStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDatePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_START_DATE;
                //日付入力ダイアログを表示
                dispDatePickerDialog(BestTravelUtil.convertStringToDate(((Button) v).getText().toString()));
            }
        });

        btnStayStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_START_DATE;
                //日付入力ダイアログを表示
                dispTimePickerDialog(((Button) v).getText().toString());
            }
        });

        btnStayEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDatePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_END_DATE;
                //日付入力ダイアログを表示
                dispDatePickerDialog(BestTravelUtil.convertStringToDate(((Button) v).getText().toString()));
            }
        });

        btnStayEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimePickerDialog = BestTravelConstant.SELECT_DATE_PICKER_DIALOG_END_DATE;
                //日付入力ダイアログを表示
                dispTimePickerDialog(((Button) v).getText().toString());
            }
        });

        //削除ボタン押下
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //削除時の処理を行う
                deleteDestinationDtoListItem();
            }
        });

        //キャンセルボタン押下
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //アクティビティを終了
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        //登録ボタン押下
        btnResist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //目的地情報を登録する
                //目的地情報を登録する
                if (destinationDto == null || destinationDto.destinationId == null) {
                    destinationDto = new DestinationDto();
                    destinationDto.routeId = routeId;
                }
                //目的地名
                destinationDto.destinationName = txtDestinationName.getText().toString();
                //滞在開始時間
                destinationDto.startDate = BestTravelUtil.convertStringToDate(btnStayStartDate.getText().toString());
                destinationDto.startTime = btnStayStartTime.getText().toString();
                //滞在終了時間
                destinationDto.endDate = BestTravelUtil.convertStringToDate(btnStayEndDate.getText().toString());
                destinationDto.endTime = btnStayEndTime.getText().toString();
                //住所
                destinationDto.address = txtAddress.getText().toString();
                if (latLng != null) {
                    //緯度
                    destinationDto.latitude = latLng.latitude;
                    //経度
                    destinationDto.longitude = latLng.longitude;
                }
                //メモ
                destinationDto.memo = txtMemo.getText().toString();
                //非公開フラグ
                destinationDto.privateDestinationFlg = chkPrivateDestination.isChecked();

                // アルバムデータのリストのうち、登録するもの、削除するものを別のリストに移動
                List<AlbumDataDto> removeAlbumDataDtoList = new ArrayList<>();

                addAlbumDataDtoList = new ArrayList<>();

                for (AlbumDataDto albumDataDto : albumDataDtoList) {

                    //新規追加の場合
                    if (albumDataDto.newFlg && !albumDataDto.deleteFlg) {
                        addAlbumDataDtoList.add(albumDataDto);

                    } else if (!albumDataDto.newFlg && albumDataDto.deleteFlg){
                        //削除対象の場合
                        removeAlbumDataDtoList.add(albumDataDto);
                    }
                }

                //削除対象のリストで置き換える
                destinationDto.albumDataDtoList = removeAlbumDataDtoList;

                //編集コードの設定
                if (destinationDto.destinationId != null) {
                    updateDestinationDtoListItem();
                } else {
                    addDestinationDtoListItem();
                }
            }
        });

        //アルバム追加ボタン押下
        btnAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                startActivityForResult(Intent.createChooser(intent,"select"), BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY);
            }
        });

        //アルバムデータのリストを取得
        albumDataDtoList = destinationDto.albumDataDtoList;
        //アルバム一覧
        albumListAdapter = new AlbumListAdapter();
        grdAlbumList.setAdapter(albumListAdapter);
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

            //画像ギャラリーアプリからの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_IMAGE_GALLERY:
                if (resultCode == RESULT_OK) {

                    AlbumDataDto albumDataDto = new AlbumDataDto();
                    albumDataDto.localFileUrl = data.getData();

                    //新規フラグを追加
                    albumDataDto.newFlg = true;

                    albumDataDtoList.add(albumDataDto);

                    //アルバムの更新
                    albumListAdapter.notifyDataSetChanged();
                }
                break;

            //アルバムデータ詳細画面からの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_ALBUM_DATA_DETAIL:

                if (resultCode == RESULT_OK) {
                    //呼び出し時のパラメーターを取得
                    //アルバムデータDTOのリストを取得
                    AlbumDto albumDto = (AlbumDto)data.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DTO);
                    albumDataDtoList = albumDto.albumDataDtoList;

                    //アルバムの更新
                    albumListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }



    /**
     * 目的地を追加する。
     */
    public void addDestinationDtoListItem() {

        //目的地情報をサーバーに登録する
        DestinationService destinationService = getRetrofit().create(DestinationService.class);
        Call<DestinationDto> reps = destinationService.insertDestination(destinationDto, myAccount.accessToken);
        reps.enqueue(new Callback<DestinationDto>() {
            @Override
            public void onResponse(Call<DestinationDto> call, Response<DestinationDto> response) {

                DestinationDto resultDestinationDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //目的地を追加する
                        addDestinationDtoListItem();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultDestinationDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == resultDestinationDto.resultCode) {

                    //追加するアルバムデータが存在しない場合
                    if (addAlbumDataDtoList.isEmpty()) {
                        //正常に修了した場合は、アクティビティを終了する
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {

                        //アルバムデータの登録
                        addAlbumDataDtoListItem();
                    }

                    return;
                }
            }

            @Override
            public void onFailure(Call<DestinationDto> call, Throwable t) {

            }
        });

        return;
    }

    /**
     * 目的地を更新する。
     *
     * @return true：正常終了／false：エラー
     */
    public void updateDestinationDtoListItem() {

        //目的地情報をサーバーに登録する
        DestinationService destinationService = getRetrofit().create(DestinationService.class);
        Call<DestinationDto> reps = destinationService.updateDestination(destinationDto, myAccount.accessToken);
        reps.enqueue(new Callback<DestinationDto>() {
            @Override
            public void onResponse(Call<DestinationDto> call, Response<DestinationDto> response) {

                DestinationDto resultDestinationDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //目的地を更新する
                        updateDestinationDtoListItem();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultDestinationDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == resultDestinationDto.resultCode) {

                    //追加するアルバムデータが存在しない場合
                    if (addAlbumDataDtoList.isEmpty()) {
                        //正常に終了した場合は、アクティビティを終了する
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {

                        //アルバムデータの登録
                        addAlbumDataDtoListItem();
                    }
                    return;
                }
            }

            @Override
            public void onFailure(Call<DestinationDto> call, Throwable t) {

            }
        });
        return;
    }
    /**
     * 目的地を削除する。
     *
     * @return true：正常終了／false：エラー
     */
    public void deleteDestinationDtoListItem() {

        if (destinationDto == null || destinationDto.destinationId == null) {
            finish();
            return;
        }
        //目的地情報をサーバーに登録する
        DestinationService destinationService = getRetrofit().create(DestinationService.class);
        Call<DestinationDto> reps = destinationService.deleteDestination(destinationDto.destinationId, myAccount.accessToken);
        reps.enqueue(new Callback<DestinationDto>() {
            @Override
            public void onResponse(Call<DestinationDto> call, Response<DestinationDto> response) {

                DestinationDto resultDestinationDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //目的地を削除する
                        deleteDestinationDtoListItem();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultDestinationDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == resultDestinationDto.resultCode) {
                    //正常に修了した場合は、アクティビティを終了する
                    setResult(Activity.RESULT_OK);
                    finish();

                    return;
                }
            }

            @Override
            public void onFailure(Call<DestinationDto> call, Throwable t) {

            }
        });
        return;
    }

    /**
     * 追加するアルバムのデータをサーバーに送信する
     */
    private void addAlbumDataDtoListItem() {

        if (addAlbumDataDtoList.isEmpty()) {
            return;
        }

        List<MultipartBody.Part> imageFileList = new ArrayList<>();

        for (AlbumDataDto addAlbumDataDto: addAlbumDataDtoList) {
            //ファイルパスを取得
            String filePass = BestTravelUtil.getFilePath(addAlbumDataDto.localFileUrl, getContentResolver());

            //パスが取得できなかった場合は、次のレコードを処理する
            if (filePass == null || "".equals(filePass)) {
                continue;
            }
            RequestBody updateHeaderImageRequestBody = BestTravelUtil.getImageRequestBody(addAlbumDataDto.localFileUrl, getContentResolver());

            MultipartBody.Part updateImagePart =
                    MultipartBody.Part.createFormData("imageFile", filePass, updateHeaderImageRequestBody);

            imageFileList.add(updateImagePart);
        }

        //送信ファイルの説明
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        //アイコン画像の更新処理を行う
        DestinationService destinationService = getRetrofit().create(DestinationService.class);
        Call<DestinationSendFileDto> reps = destinationService.insertDestinationAlbumImage(destinationDto.destinationId, description, imageFileList, myAccount.accessToken);
        reps.enqueue(new Callback<DestinationSendFileDto>() {
            @Override
            public void onResponse(Call<DestinationSendFileDto> call, Response<DestinationSendFileDto> response) {

                //サーバーより取得したアカウント
                DestinationSendFileDto destinationSendFileDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //アルバムデータを再送信する
                        addAlbumDataDtoListItem();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(destinationSendFileDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == destinationSendFileDto.resultCode) {

                    //正常に修了した場合は、アクティビティを終了する
                    setResult(Activity.RESULT_OK);
                    finish();

                } else {
                    //エラーの場合
                    dispToast(getResources().getString(R.string.error_save_image));
                }
            }

            @Override
            public void onFailure(Call<DestinationSendFileDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
        return;
    }

    /**
     * 画面に表示するビューを作成する。
     */
    private void createView() {
        //目的地名
        if (txtDestinationName == null) {
            txtDestinationName = (EditText)findViewById(R.id.txtDestinationName);
        }
        //滞在開始時間
        if (btnStayStartDate == null) {
            btnStayStartDate = (Button)findViewById(R.id.btnStayStartDate);
        }
        if (btnStayStartTime == null) {
            btnStayStartTime = (Button)findViewById(R.id.btnStayStartTime);
        }
        //滞在終了時間
        if (btnStayEndDate == null) {
            btnStayEndDate = (Button)findViewById(R.id.btnStayEndDate);
        }
        if (btnStayEndTime == null) {
            btnStayEndTime = (Button)findViewById(R.id.btnStayEndTime);
        }
        //住所
        if (txtAddress == null) {
            txtAddress = (EditText)findViewById(R.id.txtAddress);
        }
        //メモ
        if (txtMemo == null) {
            txtMemo = (EditText)findViewById(R.id.txtMemo);
        }
        //メモ
        if (txtMemo == null) {
            txtMemo = (EditText)findViewById(R.id.txtMemo);
        }
        //非公開フラグ
        if (chkPrivateDestination == null) {
            chkPrivateDestination = (CheckBox)findViewById(R.id.chkPrivateDestination);
        }
        //アルバム追加ボタン
        if (btnAddAlbum == null) {
            btnAddAlbum = (Button)findViewById(R.id.btnAddAlbum);
        }
        //アルバム一覧
        if (grdAlbumList == null) {
            grdAlbumList = (AllDispGridView)findViewById(R.id.grdAlbumList);
        }
        //削除ボタン
        if (btnDelete == null) {
            btnDelete = (Button)findViewById(R.id.btnDelete);
        }
        //キャンセルボタン
        if (btnCancel == null) {
            btnCancel = (Button)findViewById(R.id.btnCancel);
        }
        //登録ボタン
        if (btnResist == null) {
            btnResist = (Button)findViewById(R.id.btnResist);
        }
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
     * 時間入力ダイアログを表示する。
     * @param time
     */
    private void dispTimePickerDialog(String time) {
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BestTravelConstant.PARAMETER_KEY_TIME, time);
        timePickerDialogFragment.setArguments(args);
        timePickerDialogFragment.show(getSupportFragmentManager(), BestTravelConstant.FRAGMENT_KEY_TIME_PICKER_DIALOG);
    }

    /**
     * 時間入力ダイアログでの戻り値処理
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        if (selectTimePickerDialog == BestTravelConstant.SELECT_DATE_PICKER_DIALOG_START_DATE) {
            btnStayStartTime.setText(time);
        } else {
            btnStayEndTime.setText(time);
        }
    }

    /**
     * マップ選択ダイアログ選択時の処理
     * @param latLng
     */
    @Override
    public void onSelectMap(LatLng latLng) {
        this.latLng = latLng;

        //住所の取得
        txtAddress.setText(getAddressFromLetLng(latLng));
    }

    /**
     * 座標から住所を取得する。
     *
     * @param latLng 座標
     * @return 住所
     */
    private String getAddressFromLetLng(LatLng latLng) {

        //住所の取得
        StringBuffer strAddr = new StringBuffer();
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> lstAddrs = gcoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            for (Address addr : lstAddrs) {
                int idx = addr.getMaxAddressLineIndex();
                for (int i = 1; i <= idx; i++) {
                    strAddr.append(addr.getAddressLine(i));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strAddr.toString();
    }

    /**
     * 住所から座標を取得する。
     *
     * @param address 住所
     * @return 座標
     */
    private LatLng getAddressFromLetLng(String address) {
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        List<Address> lstAddr = null;
        LatLng latLng = null;

        // 緯度・経度取得
        try {
            lstAddr = gcoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lstAddr != null && lstAddr.size() > 0) {
            Address addr = lstAddr.get(0);
            latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
        }

        return latLng;
    }

    /**
     * アルバムのアダプタークラス
     */
    public class AlbumListAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return albumDataDtoList.size();
        }

        @Override
        public Object getItem(int i) {

            return albumDataDtoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.view_album_data_item, viewGroup, false);
            }

            //アルバム情報の取得
            AlbumDataDto albumDataDto = albumDataDtoList.get(i);

            // アイコン画像
            ImageView imgIcon = (ImageView)view.findViewById(R.id.imgIcon);
            imgIcon.setTag(i);
            if (albumDataDto.newFlg) {
                Picasso.with(getApplicationContext()).load(albumDataDto.localFileUrl).fit().centerInside().into(imgIcon);
            } else {
                Picasso.with(getApplicationContext()).load(albumDataDto.thumbnailFileUrl).fit().centerInside().into(imgIcon);
            }

            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //アルバムデータ詳細画面を表示
                    Intent intent = new Intent(getApplicationContext(), AlbumDataDetailActivity.class);
                    //アルバムデータDTOのリスト
                    AlbumDto albumDto = new AlbumDto();
                    albumDto.albumDataDtoList = albumDataDtoList;
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DTO, albumDto);
                    //アルバムデータのインデックスを設定
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DATA_INDEX, (int)view.getTag());

                    startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_ALBUM_DATA_DETAIL);

                }
            });

            return view;
        }
    }
}
