package jp.co.accel_road.besttravel.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.accel_road.besttravel.BaseActivity;
import jp.co.accel_road.besttravel.DestinationEditActivity;
import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.RouteListener;
import jp.co.accel_road.besttravel.model.DestinationDto;
import jp.co.accel_road.besttravel.model.GooglePlacesDto;
import jp.co.accel_road.besttravel.model.GooglePlacesLocationDto;
import jp.co.accel_road.besttravel.model.GooglePlacesResponseDto;
import jp.co.accel_road.besttravel.model.GooglePlacesResultDto;
import jp.co.accel_road.besttravel.service.DestinationService;
import jp.co.accel_road.besttravel.service.GooglePlacesService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 目的地のマップを表示するタブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class DestinationMapFragment extends Fragment {

    /** 目的地のリストが変更された際に呼び出されるリスナー */
    private RouteListener routeListener;

    /** マップオブジェクト */
    private GoogleMap mMap;

    /** 位置情報を取得するためのGoogle提供サービス接続オブジェクト */
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    /** 画面に表示する表示日付 */
    private TextView lblDispDate;
    /** 日付減少ボタン */
    private Button btnBeforeDispDate;
    /** 日付増加ボタン */
    private Button btnAfterDispDate;
    /** 検索入力欄 */
    private LinearLayout linSearchInput;
    /** 検索画面の入力欄 */
    private EditText txtSearch;

    /** 地図のフラグメント */
    private SupportMapFragment mapFragment;
    /** フラグメント全体のビュー */
    private View rootView;


    /** マーカーと目的地を関連付けるマップ */
    private Map<String, Long> markerDestinationIdMap;
    private List<Marker> destinationMarkerList;

    /** マーカーと検索結果を関連付けるマップ */
    private Map<String, DestinationDto> markerSearchResultMap;
    private List<Marker> searchResultMarkerList;

    /** 次の目的地を示す矢印を格納したリスト */
    private List<Polyline> nextDestinationPolylineList;

    /** マーカーをドラッグした目的地DTO */
    private DestinationDto dragDestinationDto;
    /**
     * 目的地一覧表示の初期表示時の処理を行う。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_destination_map, container, false);

        //日付表示ラベル
        lblDispDate = (TextView)rootView.findViewById(R.id.lblDispDate);
        //表示日付の値を設定
        lblDispDate.setText(BestTravelUtil.convertDateToString(routeListener.getDispDate()));

        //日付減少ボタン
        btnBeforeDispDate = (Button)rootView.findViewById(R.id.btnBeforeDispDate);
        btnBeforeDispDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date dispDate = routeListener.getDispDate();
                //開始日を取得
                Date startDate = routeListener.getStartDate();

                if (dispDate.compareTo(startDate) > 0) {
                    //一日前の日付を表示する
                    routeListener.changeDispDate(BestTravelUtil.addDate(dispDate, -1));
                } else {
                    //エラーメッセージを表示
                    BaseActivity baseActivity = (BaseActivity)getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_less_start_date));
                }

                //ボタンの表示制御を行う
                dispDateButton();
            }
        });

        //日付増加ボタン
        btnAfterDispDate = (Button)rootView.findViewById(R.id.btnAfterDispDate);
        btnAfterDispDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //現在の表示日付を取得
                Date dispDate = routeListener.getDispDate();
                //終了日を取得
                Date endDate = routeListener.getEndDate();

                if (dispDate.compareTo(endDate) < 0) {
                    //一日後の日付を表示する
                    routeListener.changeDispDate(BestTravelUtil.addDate(dispDate, 1));
                } else {
                    //エラーメッセージを表示
                    BaseActivity baseActivity = (BaseActivity)getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_over_end_date));
                }

                //ボタンの表示制御を行う
                dispDateButton();
            }
        });

        //ボタンの表示制御を行う
        dispDateButton();

        // 目的地アイコン
        TextView lblDestination = (TextView)rootView.findViewById(R.id.lblDestination);

        //ルート区分コードがマイルートの場合は、目的地の追加を有効にする
        if (routeListener.getRouteKbnCd() == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
            //ドラッグ開始時処理
            lblDestination.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                    rootView.startDrag(null, shadow, view, 0);
                    return true;
                }
            });
            //ドラッグ終了時処理
            rootView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    switch (dragEvent.getAction()){
                        case DragEvent.ACTION_DRAG_STARTED :
                            break;
                        case DragEvent.ACTION_DROP :

                            //終了位置を取得
                            float x = dragEvent.getX();
                            float y = dragEvent.getY();
                            //画面の座標から地図上の座標を取得
                            Projection proj = mMap.getProjection();
                            Point point = new Point();
                            point.set((int)dragEvent.getX(), (int)dragEvent.getY());
                            LatLng latLng = proj.fromScreenLocation(point);

                            //追加のダイアログ
                            DestinationDto destinationDto = new DestinationDto();
                            destinationDto.latitude = latLng.latitude;
                            destinationDto.longitude = latLng.longitude;
                            dispDestinationEdit(destinationDto);

                            break;
                        case DragEvent.ACTION_DRAG_EXITED :
                            break;
                    }
                    return true;
                }
            });
        }

        //検索入力欄表示ボタン
        Button btnDispSearch = (Button)rootView.findViewById(R.id.btnDispSearch);
        btnDispSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (linSearchInput.getVisibility() == View.VISIBLE) {
                    linSearchInput.setVisibility(View.GONE);
                } else {
                    linSearchInput.setVisibility(View.VISIBLE);
                }
            }
        });
        //ルート区分コードがマイルート以外の場合は、検索ボタンを非表示にする
        if (routeListener.getRouteKbnCd() != BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
            btnDispSearch.setVisibility(View.GONE);
        }

        //検索入力欄
        linSearchInput = (LinearLayout)rootView.findViewById(R.id.linSearchInput);

        //検索条件入力欄
        txtSearch = (EditText) rootView.findViewById(R.id.txtSearch);
        //検索ボタン
        Button btnSearch = (Button)rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchValue = txtSearch.getText().toString();

                //検索条件が入力されていない場合は、処理を終了する。
                if (searchValue == null || "".equals(searchValue)) {
                    return;
                }

                //パラメーターを設定
                GooglePlacesDto googlePlacesDto = new GooglePlacesDto();
                googlePlacesDto.searchKeyword = searchValue;
                googlePlacesDto.latitude = mMap.getCameraPosition().target.latitude;
                googlePlacesDto.longitude = mMap.getCameraPosition().target.longitude;
                googlePlacesDto.radius = 256 * 2 ^(int)mMap.getCameraPosition().zoom;

                //アクティビティを取得
                BaseActivity baseActivity = (BaseActivity) getActivity();
                //ユーザーIDに紐づくユーザー情報を取得する
                GooglePlacesService googlePlacesService = baseActivity.getRetrofit().create(GooglePlacesService.class);
                Call<GooglePlacesResponseDto> reps = googlePlacesService.getSearchPlace(googlePlacesDto);
                reps.enqueue(new Callback<GooglePlacesResponseDto>() {
                    @Override
                    public void onResponse(Call<GooglePlacesResponseDto> call, Response<GooglePlacesResponseDto> response) {

                        GooglePlacesResponseDto googlePlacesResponseDto = response.body();

                        //検索結果が存在する場合は、マップに結果を表示する
                        if (!googlePlacesResponseDto.results.isEmpty()) {
                            if (searchResultMarkerList == null) {
                                searchResultMarkerList = new ArrayList<>();
                            }

                            if (markerSearchResultMap == null) {
                                markerSearchResultMap = new HashMap<>();
                            } else {
                                markerSearchResultMap.clear();
                            }

                            //検索結果のマーカーのクリア
                            for (Marker marker: searchResultMarkerList) {
                                marker.remove();
                            }
                            searchResultMarkerList.clear();

                            for (GooglePlacesResultDto googlePlacesResultDto: googlePlacesResponseDto.results) {
                                GooglePlacesLocationDto googlePlacesLocationDto = googlePlacesResultDto.geometry.location;

                                LatLng sydney = new LatLng(googlePlacesLocationDto.lat, googlePlacesLocationDto.lng);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(sydney);
                                markerOptions.title(googlePlacesResultDto.name);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                Marker marker = mMap.addMarker(markerOptions);

                                searchResultMarkerList.add(marker);

                                DestinationDto destinationDto = new DestinationDto();
                                destinationDto.destinationName = googlePlacesResultDto.name;
                                destinationDto.latitude = googlePlacesLocationDto.lat;
                                destinationDto.longitude = googlePlacesLocationDto.lng;

                                markerSearchResultMap.put(marker.getId(), destinationDto);
                            }
                        } else {

                            String searchValue = txtSearch.getText().toString();
                            //結果が存在しない場合は、ジオコードで検索する
                            List<Address> addressList = getAddressFromLetLng(searchValue);

                            if (addressList == null) {
                                return;
                            }

                            if (searchResultMarkerList == null) {
                                searchResultMarkerList = new ArrayList<>();
                            }

                            if (markerSearchResultMap == null) {
                                markerSearchResultMap = new HashMap<>();
                            } else {
                                markerSearchResultMap.clear();
                            }

                            //検索結果のマーカーのクリア
                            for (Marker marker: searchResultMarkerList) {
                                marker.remove();
                            }
                            searchResultMarkerList.clear();

                            //検索結果を画面に表示する
                            for (Address address: addressList) {
                                LatLng sydney = new LatLng(address.getLatitude(), address.getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(sydney);
                                markerOptions.title(address.getFeatureName());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                Marker marker = mMap.addMarker(markerOptions);
                                searchResultMarkerList.add(marker);

                                DestinationDto destinationDto = new DestinationDto();
                                destinationDto.destinationName = address.getFeatureName();
                                //住所取得
                                StringBuffer strAddr  = new StringBuffer();
                                int idx = address.getMaxAddressLineIndex();
                                for (int i = 1; i <= idx; i++) {
                                    strAddr.append(address.getAddressLine(i));
                                }
                                destinationDto.address = strAddr.toString();
                                destinationDto.latitude = address.getLatitude();
                                destinationDto.longitude = address.getLongitude();

                                markerSearchResultMap.put(marker.getId(), destinationDto);
                            }

                            //位置情報を取得できたので位置を移動する
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude()), 12));
                        }
                    }

                    @Override
                    public void onFailure(Call<GooglePlacesResponseDto> call, Throwable t) {

                    }
                });
            }
        });

        //目的地のリストマップを取得
        List<DestinationDto> destinationDtoList = routeListener.getDispDestinationDtoList();
        if (destinationDtoList.isEmpty()) {
            //目的地が存在しない場合は、現在地を初期表示する
            moveCurrentPlace();
        }

        //地図情報の設定
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

                //画面の描画処理を実行
                redrow();

                //現在の位置情報を表示
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        //目的地IDを取得
                        Long destinationId = markerDestinationIdMap.get(marker.getId());

                        //目的地のマーカーの場合
                        if (destinationId != null) {
                            View view = getActivity().getLayoutInflater().inflate(R.layout.view_destination_map_info_window, null);
                            //目的地IDに紐づく目的地を取得
                            DestinationDto destinationDto = routeListener.getDispDestinationDto(destinationId);

                            //タイトル
                            TextView lblDestinationTitle = (TextView)view.findViewById(R.id.lblDestinationTitle);
                            lblDestinationTitle.setText(destinationDto.destinationName);
                            //開始時間
                            Date dispDate = routeListener.getDispDate();
                            TextView lblStartTime = (TextView)view.findViewById(R.id.lblStartTime);
                            //開始日付と等しいなら時間のみを表示する
                            if (dispDate.compareTo(destinationDto.startDate) == 0) {
                                lblStartTime.setText(destinationDto.startTime);
                            } else {
                                lblStartTime.setText(BestTravelUtil.convertDateToStringMonthDate(destinationDto.startDate) + destinationDto.startTime);
                            }
                            //終了時間
                            TextView lblEndTime = (TextView)view.findViewById(R.id.lblEndTime);
                            //終了日付と等しいなら時間のみを表示する
                            if (dispDate.compareTo(destinationDto.endDate) == 0) {
                                lblEndTime.setText(destinationDto.endTime);
                            } else {
                                lblEndTime.setText(BestTravelUtil.convertDateToStringMonthDate(destinationDto.endDate) + destinationDto.endTime);
                            }
                            //メモ
                            TextView lblMemo = (TextView)view.findViewById(R.id.lblMemo);
                            lblMemo.setText(destinationDto.memo);

                            return view;
                        }
                        return null;
                    }
                });

                //表示座標の初期値（東京駅）
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.681298, 139.766247), 12));

                //目的地のリストマップを取得
                List<DestinationDto> destinationDtoList = routeListener.getDispDestinationDtoList();

                //目的地が存在する場合は、最初の目的地を地図の中心に設定
                if (!destinationDtoList.isEmpty()) {
                    for (DestinationDto destinationDto: destinationDtoList) {
                        if (destinationDto.latitude != null && destinationDto.longitude != null) {
                            //初期表示の座標を取得
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destinationDto.latitude, destinationDto.longitude), 12));
                            break;
                        }
                    }
                }

                //マーカークリック時動作
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //情報ウィンドウの表示
                        marker.showInfoWindow();
                        return true;
                    }
                });

                //情報ウィンドウクリック時動作
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        //ルート区分コードがマイルートの場合は、目的地の編集を有効にする
                        if (routeListener.getRouteKbnCd() == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                            //目的地IDを取得
                            Long destinationId = markerDestinationIdMap.get(marker.getId());
                            DestinationDto destinationDto = null;
                            if (destinationId != null) {
                                //目的地IDに紐づく目的地を取得
                                destinationDto = routeListener.getDispDestinationDto(destinationId);
                            } else {
                                //検索結果のマーカーの場合
                                destinationDto = markerSearchResultMap.get(marker.getId());
                            }

                            //データが取得でき、住所が未設定の場合は、住所を取得して設定する
                            if (destinationDto != null && (destinationDto.address == null || "".equals(destinationDto.address))) {
                                destinationDto.address = getAddressFromLetLng(new LatLng(destinationDto.latitude, destinationDto.longitude));
                            }

                            dispDestinationEdit(destinationDto);
                        }
                    }
                });

                //ルート区分コードがマイルートの場合は、目的地の移動を有効にする
                if (routeListener.getRouteKbnCd() == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                    //マーカードラッグ時動作
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            //移動後の座標で目的地を更新する

                            //目的地IDを取得
                            Long destinationId = markerDestinationIdMap.get(marker.getId());

                            if (destinationId == null) {
                                return;
                            }
                            //目的地IDに紐づく目的地を取得
                            dragDestinationDto = routeListener.getDispDestinationDto(destinationId);

                            if (dragDestinationDto == null) {
                                return;
                            }
                            //座標を取得
                            LatLng latLng = marker.getPosition();

                            //座標の更新
                            dragDestinationDto.latitude = latLng.latitude;
                            dragDestinationDto.longitude = latLng.longitude;

                            //目的地を更新する
                            updateDestinationDtoListItem();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    /**
     * Android 6.0以上の場合に呼び出される、アタッチ時の処理
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //リスナーを設定
        routeListener = (RouteListener)context;
    }

    /**
     * Android 6.0未満の場合に呼び出される、アタッチ時の処理
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return;
        }
        //リスナーを設定
        routeListener = (RouteListener)activity;
    }

    /**
     * フラグメント内の再描画を行う。
     */
    public void redrow() {

        if (destinationMarkerList == null) {
            destinationMarkerList = new ArrayList<>();
        }
        //マーカーと目的地を結びつけるマップをクリア
        if (markerDestinationIdMap == null) {
            markerDestinationIdMap = new HashMap<>();
        } else {
            markerDestinationIdMap.clear();
        }

        //次の目的地を示す矢印をクリア
        if (nextDestinationPolylineList == null) {
            nextDestinationPolylineList = new ArrayList<>();
        }

        if (mMap != null) {

            //マーカーのクリア
            for (Marker marker: destinationMarkerList) {
                marker.remove();
            }
            destinationMarkerList.clear();

            //矢印のクリア
            for (Polyline polyline: nextDestinationPolylineList) {
                polyline.remove();
            }
            nextDestinationPolylineList.clear();

            //目的地のリストマップを取得
            List<DestinationDto> destinationDtoList = routeListener.getDispDestinationDtoList();

            //ルート情報を画面に表示
            for (DestinationDto destinationDto : destinationDtoList) {
                //座標が登録されている場合画面に表示する
                if (destinationDto.latitude != null && destinationDto.longitude != null) {
                    LatLng sydney = new LatLng(destinationDto.latitude, destinationDto.longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(sydney);
                    markerOptions.title(destinationDto.destinationName);
                    markerOptions.draggable(true);
                    Marker marker = mMap.addMarker(markerOptions);

                    destinationMarkerList.add(marker);

                    //マーカーと目的地を結びつけるマップに値を設定
                    markerDestinationIdMap.put(marker.getId(), destinationDto.destinationId);
                }
            }

            //目的地を結びつける矢印を描画
            for (int i = 0 ; i < destinationDtoList.size(); i++) {

                //矢印の先となる目的地を取得
                DestinationDto toDestinationDto = destinationDtoList.get(i);

                //時間が設定されていない場合は、次のレコードを処理する
                if (toDestinationDto.startTime == null) {
                    continue;
                }

                for (int j = i - 1; j >= 0; j--) {
                    //矢印の元となる目的地を取得
                    DestinationDto fromDestinationDto = destinationDtoList.get(j);
                    if (fromDestinationDto.endTime == null) {
                        continue;
                    }

                    //直前の目的地があった場合は、矢印を描画
                    if (toDestinationDto.startDate.compareTo(fromDestinationDto.endDate) >= 0 &&
                            toDestinationDto.startTime.compareTo(fromDestinationDto.endTime) >= 0) {
                        dispNextDestinationArrow(fromDestinationDto.latitude, fromDestinationDto.longitude, toDestinationDto.latitude, toDestinationDto.longitude);
                        break;
                    }
                }
            }
        }

        //表示日付の値を設定
        lblDispDate.setText(BestTravelUtil.convertDateToString(routeListener.getDispDate()));

        //ボタンの表示制御を行う
        dispDateButton();
    }

    /**
     * 現在地を取得し、カメラを移動する
     */
    private void moveCurrentPlace() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(1000);
//        locationRequest.setFastestInterval(16);

        //GoogleApiの使用準備
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //GoogleApiの使用準備成功時の処理
                        //現在地の取得

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    //位置変更時に呼び出されるメソッド
                                }
                            }).setResultCallback(new ResultCallback<Status>() {

                                @Override
                                public void onResult(Status status) {
                                    //位置取得完了時に呼び出されるメソッド

                                    //位置情報の取得
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        //位置情報を取得できた場合
                                        if (location != null) {
                                            //位置情報を取得できたので位置を移動する
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));

                                            //GPSを終了する
                                            googleApiClient.disconnect();
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        //位置情報接続中断時の処理
                    }
                })
                .addOnConnectionFailedListener( new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        //接続失敗時の処理
                    }
                })
                .build();

        googleApiClient.connect();

        //GPSが起動されていない場合には、起動を促すダイアログを表示する
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // 位置情報が利用できる
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //アクティビティを取得
                        BaseActivity baseActivity = (BaseActivity) getActivity();
                        //エラーメッセージを表示
                        baseActivity.dispToast(getResources().getString(R.string.error_not_gps));
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // 位置情報が取得できず、なおかつその状態からの復帰も難しい時呼ばれるらしい
                        break;
                }
            }
        });
    }

    /**
     * 目的地編集アクティビティを表示する。
     *
     * @param destinationDto
     */
    private void dispDestinationEdit(DestinationDto destinationDto) {
        //目的地編集画面を表示
        Intent intent = new Intent(getContext(), DestinationEditActivity.class);
        //目的地DTO
        intent.putExtra(BestTravelConstant.PARAMETER_KEY_DESTINATION_DTO, destinationDto);
        //ルートIDを設定
        intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_ID, routeListener.getRouteId());
        //表示日付を設定
        intent.putExtra(BestTravelConstant.PARAMETER_KEY_DISP_DATE, routeListener.getDispDate());

        startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_DESTINATION_EDIT);
    }

    /**
     * 呼び出し先アクティビティからの戻り時処理を行う。
     *
     * @param requestCode 呼び出し時のコード
     * @param resultCode 処理結果コード
     * @param intent パラメータ
     */
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        //目的地編集アクティビティからの戻り時処理
        if (requestCode == BestTravelConstant.ACTIVITY_REQUEST_KEY_DESTINATION_EDIT) {
            //正常終了以外の場合は処理を終了する
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            //ルート情報の最新化を行う
            routeListener.refreshLatestRoute();
        }
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
        Geocoder gcoder = new Geocoder(getActivity(), Locale.getDefault());
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
     * 住所から座標のリストを取得する。
     *
     * @param address 住所
     * @return 座標
     */
    private List<Address> getAddressFromLetLng(String address) {
        Geocoder gcoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> lstAddr = null;

        // 緯度・経度取得
        try {
            lstAddr = gcoder.getFromLocationName(address, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstAddr;
    }


    /**
     * 指定座標に矢印を描画する
     *
     * @param fromLatitude 元緯度
     * @param fromLongitude 元経度
     * @param toLatitude 先緯度
     * @param toLongitude 先経度
     */
    private void dispNextDestinationArrow(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        //2転換の距離を取得する。各項目に以下の値が入る
        //0： 距離（メートル）
        //1：始点から終点までの方位角
        //2：終点から始点までの方位角
        float[] results = new float[3];
        Location.distanceBetween(fromLatitude, fromLongitude, toLatitude, toLongitude, results);

        //距離を取得
        float distance = results[0];

        //距離によって太さを設定
        float lineSize = 12;
        float arrowSize = 10000;
        if (distance < 1000) {
            lineSize = 4;
            arrowSize = 100;
        } else if (1000 <= distance && distance < 10000) {
            lineSize = 6;
            arrowSize = 300;
        } else if (10000 <= distance && distance < 50000) {
            lineSize = 8;
            arrowSize = 1000;
        } else if (50000 <= distance && distance < 100000) {
            lineSize = 10;
            arrowSize = 5000;
        }

        //矢印の色を指定
        int lineColor = Color.BLUE;

        //2点間の角度
        double radian = Math.atan2(toLatitude - fromLatitude,toLongitude - fromLongitude);

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(new LatLng(fromLatitude, fromLongitude), new LatLng(toLatitude, toLongitude));
        polylineOptions.width(lineSize);
        polylineOptions.color(lineColor);
        Polyline polyline = mMap.addPolyline(polylineOptions);
        nextDestinationPolylineList.add(polyline);

        PolylineOptions polylineOptionsArrowLeft = new PolylineOptions();
        double arrowLeftLatitude = toLatitude + arrowSize * 0.0000091 * Math.sin(radian - 150 *  Math.PI / 180);
        double arrowLeftLongitude = toLongitude + arrowSize * 0.0000091 * Math.cos(radian - 150 * Math.PI / 180);
        polylineOptionsArrowLeft.add(new LatLng(arrowLeftLatitude, arrowLeftLongitude), new LatLng(toLatitude, toLongitude));
        polylineOptionsArrowLeft.width(lineSize);
        polylineOptionsArrowLeft.color(lineColor);
        Polyline polylineArrowLeft = mMap.addPolyline(polylineOptionsArrowLeft);
        nextDestinationPolylineList.add(polylineArrowLeft);

        PolylineOptions polylineOptionsArrowRight = new PolylineOptions();
        double arrowRightLatitude = toLatitude + arrowSize * 0.0000091 * Math.sin(radian + 150 * Math.PI / 180);
        double arrowRightLongitude = toLongitude + arrowSize * 0.0000091 * Math.cos(radian + 150 * Math.PI / 180);
        polylineOptionsArrowRight.add(new LatLng(arrowRightLatitude, arrowRightLongitude), new LatLng(toLatitude, toLongitude));
        polylineOptionsArrowRight.width(lineSize);
        polylineOptionsArrowRight.color(lineColor);
        Polyline polylineArrowRight = mMap.addPolyline(polylineOptionsArrowRight);
        nextDestinationPolylineList.add(polylineArrowRight);
    }


    /**
     * 目的地を更新する。
     *
     * @return true：正常終了／false：エラー
     */
    public void updateDestinationDtoListItem() {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //目的地情報をサーバーに登録する
        DestinationService destinationService = baseActivity.getRetrofit().create(DestinationService.class);
        Call<DestinationDto> reps = destinationService.updateDestination(dragDestinationDto, baseActivity.myAccount.accessToken);
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
                //アクティビティを取得
                BaseActivity baseActivity = (BaseActivity) getActivity();
                //アクセストークンのチェック
                if(!baseActivity.checkAccessToken(resultDestinationDto.resultCode, listener)) {
                    return ;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == resultDestinationDto.resultCode) {

                    //ルート情報の最新化を行う
                    routeListener.refreshLatestRoute();

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
     * 表示日付変更ボタンの表示、非表示制御を行う。
     */
    private void dispDateButton() {

        //現在の表示日付を取得
        Date dispDate = routeListener.getDispDate();
        //開始日を取得
        Date startDate = routeListener.getStartDate();
        //終了日を取得
        Date endDate = routeListener.getEndDate();

        //日付増加ボタンの表示制御
        if (dispDate.compareTo(endDate) >= 0) {
            btnAfterDispDate.setVisibility(View.INVISIBLE);
        } else {
            btnAfterDispDate.setVisibility(View.VISIBLE);
        }

        //日付減少ボタンの表示制御
        if (dispDate.compareTo(startDate) <= 0) {
            btnBeforeDispDate.setVisibility(View.INVISIBLE);
        } else {
            btnBeforeDispDate.setVisibility(View.VISIBLE);
        }
    }
}
