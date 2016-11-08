package jp.co.accel_road.besttravel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.co.accel_road.besttravel.BaseActivity;
import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.RouteDetailActivity;
import jp.co.accel_road.besttravel.RouteEditActivity;
import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.AlbumDataDao;
import jp.co.accel_road.besttravel.dao.DestinationDao;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.dao.RouteDao;
import jp.co.accel_road.besttravel.dao.RouteParticipantDao;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.model.RouteListDto;
import jp.co.accel_road.besttravel.service.FavoriteService;
import jp.co.accel_road.besttravel.service.RouteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ルートのリストを表示するタブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class RouteListFragment extends Fragment {

    /** ルート区分コード */
    private int routeKbnCd;
    /** リストに表示するルートのリスト */
    private List<RouteDto> routeDtoList = null;
    /** ルートリストのアダプター */
    private MyRouteListAdapter myRouteListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_list, container, false);

        //ルート区分コードを取得
        routeKbnCd = getArguments().getInt(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD);

        if (BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE == routeKbnCd) {
            //マイルートの場合
            onCreateViewMyRoute(rootView);
        } else {
            //お気に入りの場合
            onCreateViewFavorite(rootView);
        }
        return rootView;
    }

    /**
     * マイルートの場合の初期表示処理を行う。
     *
     * @param rootView ルートのビュー
     */
    private void onCreateViewMyRoute(View rootView) {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //ローカルDBのマイルートのリストを全て取得
        RouteDao routeDao = new RouteDao();
        List<Route> routeList = routeDao.getMyRouteList(baseActivity.myAccount.accountId);
        setDispRouteList(routeList);

        //マイルートのリストを取得
        ListView lisMyRouteList = (ListView) rootView.findViewById(R.id.lisMyRouteList);
        //マイルートのアダプターを設定
        myRouteListAdapter = new MyRouteListAdapter(getActivity());
        lisMyRouteList.setAdapter(myRouteListAdapter);

        //追加ボタン
        FloatingActionButton btnRouteAdd = (FloatingActionButton) rootView.findViewById(R.id.btnRouteAdd);
        btnRouteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //目的地変更アクティビティを表示します
                dispRouteEditActivity(null);
            }
        });

        //マイルートのリストを設定する
        getMyRouteListServer();
    }
    /**
     * お気に入りの場合の初期表示処理を行う。
     *
     * @param rootView ルートのビュー
     */
    private void onCreateViewFavorite(View rootView) {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //お気に入りルートのリストを全て取得
        RouteDao routeDao = new RouteDao();
        List<Route> routeList = routeDao.getFavoriteRouteList(baseActivity.myAccount.accountId);
        setDispRouteList(routeList);

        //マイルートのリストを取得
        ListView lisMyRouteList = (ListView)rootView.findViewById(R.id.lisMyRouteList);
        //マイルートのアダプターを設定
        myRouteListAdapter = new MyRouteListAdapter(getActivity());
        lisMyRouteList.setAdapter(myRouteListAdapter);

        //お気に入りルートのリストを設定する
        getFavoriteRouteListServer();

        //追加ボタンを非表示にする
        FloatingActionButton btnRouteAdd = (FloatingActionButton) rootView.findViewById(R.id.btnRouteAdd);
        btnRouteAdd.setVisibility(View.INVISIBLE);
    }

    /**
     * マイルートのリストをサーバーより取得して設定する。
     */
    private void getMyRouteListServer() {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //ルートの取得が一度もない場合
        if (baseActivity.myAccount.myRouteGetLastDate == null) {
            //マイルートの一覧を取得
            RouteService routeService = baseActivity.getRetrofit().create(RouteService.class);
            Call<RouteListDto> reps = routeService.getMyRouteList(baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteListDto>() {
                @Override
                public void onResponse(Call<RouteListDto> call, Response<RouteListDto> response) {
                    //マイルートのリストを取得
                    RouteListDto routeListDto = response.body();

                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //マイルートのリストを再取得する
                            getMyRouteListServer();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeListDto.resultCode, listener)) {
                        return ;
                    }

                    if (routeListDto.resultCode == BestTravelConstant.RESULT_CODE_OK) {

                        //取得したマイルートのリストをDBに保存する
                        saveDbMyRouteList(routeListDto);
                    } else {

                        baseActivity.dispToast(getResources().getString(R.string.error_get, "マイルート"));
                    }
                }

                @Override
                public void onFailure(Call<RouteListDto> call, Throwable t) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        } else {
            //以前の取得からのマイルートの差分の一覧を取得
            RouteService routeService = baseActivity.getRetrofit().create(RouteService.class);
            Call<RouteListDto> reps = routeService.getDifferenceMyRouteList(BestTravelUtil.convertJsonDateToString(baseActivity.myAccount.myRouteGetLastDate),
                    baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteListDto>() {
                @Override
                public void onResponse(Call<RouteListDto> call, Response<RouteListDto> response) {
                    //マイルートのリストを取得
                    RouteListDto routeListDto = response.body();

                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //マイルートのリストを再取得する
                            getMyRouteListServer();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeListDto.resultCode, listener)) {
                        return ;
                    }

                    if (routeListDto.resultCode == BestTravelConstant.RESULT_CODE_OK) {

                        //取得したマイルートのリストをDBに保存する
                        saveDbMyRouteList(routeListDto);
                    } else {

                        baseActivity.dispToast(getResources().getString(R.string.error_get, "マイルート"));
                    }
                }

                @Override
                public void onFailure(Call<RouteListDto> call, Throwable t) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        }
    }

    /**
     * お気に入りルートのリストをサーバーより取得して設定する。
     */
    private void getFavoriteRouteListServer() {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity.myAccount.favoriteGetLastDate == null) {

            //お気に入りルートの一覧を取得
            FavoriteService favoriteService = baseActivity.getRetrofit().create(FavoriteService.class);
            Call<RouteListDto> reps = favoriteService.getFavoriteRouteList(baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteListDto>() {
                @Override
                public void onResponse(Call<RouteListDto> call, Response<RouteListDto> response) {
                    //マイルートのリストを取得
                    RouteListDto routeListDto = response.body();

                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りルートのリストを再取得する
                            getFavoriteRouteListServer();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeListDto.resultCode, listener)) {
                        return ;
                    }

                    if (routeListDto.resultCode == BestTravelConstant.RESULT_CODE_OK) {

                        //取得したお気に入りルートのリストをDBに保存する
                        saveDbFavoriteRouteList(routeListDto);
                    } else {

                        baseActivity.dispToast(getResources().getString(R.string.error_get, "お気に入りルート"));
                    }
                }

                @Override
                public void onFailure(Call<RouteListDto> call, Throwable t) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        } else {

            //お気に入りルートの一覧を取得
            FavoriteService favoriteService = baseActivity.getRetrofit().create(FavoriteService.class);
            Call<RouteListDto> reps = favoriteService.getDifferenceFavoriteRouteList(BestTravelUtil.convertJsonDateToString(baseActivity.myAccount.favoriteGetLastDate),
                    baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteListDto>() {
                @Override
                public void onResponse(Call<RouteListDto> call, Response<RouteListDto> response) {
                    //マイルートのリストを取得
                    RouteListDto routeListDto = response.body();

                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りルートのリストを再取得する
                            getFavoriteRouteListServer();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeListDto.resultCode, listener)) {
                        return ;
                    }

                    if (routeListDto.resultCode == BestTravelConstant.RESULT_CODE_OK) {

                        //取得したお気に入りルートのリストをDBに保存する
                        saveDbFavoriteRouteList(routeListDto);
                    } else {

                        baseActivity.dispToast(getResources().getString(R.string.error_get, "お気に入りルート"));
                    }
                }

                @Override
                public void onFailure(Call<RouteListDto> call, Throwable t) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        }
    }
    /**
     * サーバーより取得したマイルートリストをDBに保存する。
     *
     * @param routeListDto サーバーから取得したルートリスト
     */
    private void saveDbMyRouteList(RouteListDto routeListDto) {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //リストが取得できた場合は、ルームリストを更新する
        if (routeListDto != null && routeListDto.routeList != null) {
            RouteDao routeDao = new RouteDao();

            //ルートリストを置き換える
            for (RouteDto routeDto: routeListDto.routeList) {

                //削除対象の場合
                if (routeDto.deleteFlg) {
                    //DBよりデータを削除
                    routeDao.deleteRoute(routeDto.routeId, baseActivity.myAccount.accountId);
                } else {
                    Route route = routeDao.getRoute(routeDto.routeId, baseActivity.myAccount.accountId);
                    if (route == null) {
                        //新規登録
                        routeDao.insertRoute(routeDto.getMyRoute(route, baseActivity.myAccount.accountId));

                    } else {

                        //更新日時が古い場合は、処理を行わない
                        if (route.updateDate.compareTo(routeDto.updateDate) > 0) {
                            continue;
                        }

                        //更新
                        routeDao.updateRoute(routeDto.getMyRoute(route, baseActivity.myAccount.accountId));
                    }
                }
            }

            //ルートリストを再取得
            List<Route> routeList = routeDao.getMyRouteList(baseActivity.myAccount.accountId);
            setDispRouteList(routeList);

            //ルートリストの更新
            myRouteListAdapter.notifyDataSetChanged();
        }

        //ルート検索時間の更新
        baseActivity.myAccount.myRouteGetLastDate = routeListDto.getLastDate;
        MyAccountDao myAccountDao = new MyAccountDao();
        myAccountDao.updateMyAccount(baseActivity.myAccount);
    }

    /**
     * サーバーより取得したお気に入りルートリストをDBに保存する。
     *
     * @param routeListDto サーバーから取得したルートリスト
     */
    private void saveDbFavoriteRouteList(RouteListDto routeListDto) {

        //アクティビティを取得
        BaseActivity baseActivity = (BaseActivity) getActivity();

        //リストが取得できた場合は、ルームリストを更新する
        if (routeListDto != null && routeListDto.routeList != null) {
            RouteDao routeDao = new RouteDao();

            //ルートリストを置き換える
            for (RouteDto routeDto: routeListDto.routeList) {

                //削除対象の場合
                if (routeDto.deleteFlg) {
                    //DBよりデータを削除
                    routeDao.deleteRoute(routeDto.routeId, baseActivity.myAccount.accountId);
                } else {
                    Route route = routeDao.getRoute(routeDto.routeId, baseActivity.myAccount.accountId);
                    if (route == null) {
                        //新規登録
                        routeDao.insertRoute(routeDto.getFavoriteRoute(null, baseActivity.myAccount.accountId));

                    } else {

                        //更新日時が古い場合は、処理を行わない
                        if (route.updateDate.compareTo(routeDto.updateDate) > 0) {
                            continue;
                        }

                        //更新
                        routeDao.updateRoute(routeDto.getFavoriteRoute(route, baseActivity.myAccount.accountId));
                    }
                }
            }

            //ルートリストを再取得
            List<Route> routeList = routeDao.getFavoriteRouteList(baseActivity.myAccount.accountId);
            setDispRouteList(routeList);

            //ルートリストの更新
            myRouteListAdapter.notifyDataSetChanged();
        }

        //ルート検索時間の更新
        baseActivity.myAccount.favoriteGetLastDate = routeListDto.getLastDate;
        MyAccountDao myAccountDao = new MyAccountDao();
        myAccountDao.updateMyAccount(baseActivity.myAccount);
    }

    /**
     * ルートリストを画面表示用のリストにセットする
     *
     * @param routeList
     */
    private void setDispRouteList(List<Route> routeList) {

        if (routeDtoList == null) {
            routeDtoList = new ArrayList<>();
        } else {
            routeDtoList.clear();
        }

        //画面表示用のリストにローカルDBの値を設定する
        for (Route route : routeList) {
            RouteDto routeDto = new RouteDto();
            routeDto.setRoute(route);
            routeDtoList.add(routeDto);
        }

    }

    /**
     * ルート編集アクティビティを表示する。
     *
     * @param routeDto
     */
    private void dispRouteEditActivity(RouteDto routeDto) {

        //ルート検索画面を表示
        Intent intent = new Intent(getContext(), RouteEditActivity.class);
        //ルート
        intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO, routeDto);
        startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_ROUTE_EDIT);
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
        //ルート編集アクティビティからの戻り時処理
        if (requestCode == BestTravelConstant.ACTIVITY_REQUEST_KEY_ROUTE_EDIT) {
            //正常終了以外の場合は処理を終了する
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            //マイルートのリストを再取得し設定する
            getMyRouteListServer();
        }
    }

    /**
     * マイルートのリストアダプタークラス
     */
    public class MyRouteListAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater = null;

        /** お気に入りボタンを押下したルートID */
        private long favoriteRouteId;
        /** 押下したお気に入りボタン */
        private ToggleButton favoritePushButton;

        public MyRouteListAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return routeDtoList.size();
        }

        @Override
        public RouteDto getItem(int position) {
            //取得する値が範囲外の場合はnullを返す
            if (routeDtoList.size() >= position) {
                return null;
            }
            return routeDtoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            //取得する値が範囲外の場合はnullを返す
            if (routeDtoList.size() >= position) {
                return -1;
            }
            return routeDtoList.get(position).routeId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.view_route_item, parent, false);
            }

            //ルート情報を取得
            RouteDto routeDto = routeDtoList.get(position);

            //アイコン画像
            ImageView imgMyRouteIcon = (ImageView)convertView.findViewById(R.id.imgMyRouteIcon);
            Picasso.with(getActivity()).load(routeDto.iconUrl).fit().centerInside().into(imgMyRouteIcon);

            //期間
            TextView lblMyRoutePeriod = (TextView)convertView.findViewById(R.id.lblMyRoutePeriod);
            //開始日と終了日が等しい場合
            if (routeDto.startDate.compareTo(routeDto.endDate) == 0) {
                lblMyRoutePeriod.setText(BestTravelUtil.convertDateToStringDispDate(routeDto.startDate));
            } else {
                //開始日と終了日が等しくない場合
                lblMyRoutePeriod.setText(BestTravelUtil.convertDateToStringDispDate(routeDto.startDate) + "～" + BestTravelUtil.convertDateToStringDispDate(routeDto.endDate));
            }

            //タイトル
            TextView lblMyRouteTitle = (TextView)convertView.findViewById(R.id.lblMyRouteTitle);
            lblMyRouteTitle.setText(routeDto.routeTitle);

            //お気に入りボタン
            ToggleButton imbFavorite = (ToggleButton)convertView.findViewById(R.id.imbFavorite);
            imbFavorite.setChecked(true);
            imbFavorite.setTag(routeDto.routeId);
            imbFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToggleButton imbFavorite = (ToggleButton)view;
                    favoriteRouteId = (long) imbFavorite.getTag();
                    favoritePushButton = imbFavorite;
                    //ボタンがチェックされたのか確認する
                    if (imbFavorite.isChecked()) {
                        //チェックされた場合
                        insertFavorite();
                    } else {
                        //チェックが解除された場合
                        deleteFavorite();
                    }
                }
            });

            //マイルートを表示している場合は、お気に入りボタンを非表示にする
            if (BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE == routeKbnCd) {
                imbFavorite.setVisibility(View.GONE);
            }

            //リスト選択
            convertView.setTag(routeDto.routeId);
            //クリック時の処理
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ルート詳細を表示
                    Intent intent = new Intent(context, RouteDetailActivity.class);

                    long routeId = (long) v.getTag();
                    for(int i = 0; i < routeDtoList.size(); i++) {
                        RouteDto routeDto = routeDtoList.get(i);

                        //対象のルートIDが等しい場合
                        if (routeDto.routeId == routeId) {
                            //目的地変更アクティビティを表示します
                            intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO, routeDto);
                            intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD, routeKbnCd);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            });
            //長押し時の処理
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long routeId = (long) v.getTag();
                    for(int i = 0; i < routeDtoList.size(); i++) {
                        RouteDto routeDto = routeDtoList.get(i);

                        //対象のルートIDが等しい場合
                        if (routeDto.routeId == routeId) {
                            //目的地変更アクティビティを表示します
                            dispRouteEditActivity(routeDto);
                            break;
                        }
                    }
                    return true;
                }
            });
            return convertView;
        }

        /**
         * お気に入りの登録処理を行う
         */
        private void insertFavorite() {

            //アクティビティを取得
            BaseActivity baseActivity = (BaseActivity) getActivity();

            //お気に入りを登録する
            FavoriteService favoriteService = baseActivity.getRetrofit().create(FavoriteService.class);
            Call<RouteDto> reps = favoriteService.insertFavoriteRoute(favoriteRouteId, baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteDto>() {
                @Override
                public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                    RouteDto routeDto = response.body();

                    //アクティビティを取得
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りの登録処理を行う
                            insertFavorite();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeDto.resultCode, listener)) {
                        return ;
                    }

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == routeDto.resultCode) {

                        //現在登録されているルートの情報を登録する
                        RouteDao routeDao = new RouteDao();
                        Route route = routeDao.getRoute(routeDto.routeId, baseActivity.myAccount.accountId);

                        if (route == null) {
                            routeDao.insertRoute(routeDto.getFavoriteRoute(null, baseActivity.myAccount.accountId));
                        } else if (route.routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                            //メッセージを表示
                            baseActivity.dispToast(getResources().getString(R.string.error_insert_favorite_my_route));
                        } else {
                            routeDao.updateRoute(routeDto.getFavoriteRoute(route, baseActivity.myAccount.accountId));
                        }
                        return;
                    } else {
                        //メッセージを表示
                        baseActivity.dispToast(getResources().getString(R.string.error_insert, "お気に入り"));
                        //トグルボタンの表示を戻す
                        if (favoritePushButton != null) {
                            favoritePushButton.setClickable(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteDto> call, Throwable t) {

                    //アクティビティを取得
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));

                    //トグルボタンの表示を戻す
                    if (favoritePushButton != null) {
                        favoritePushButton.setClickable(false);
                    }
                }
            });
        }
        /**
         * お気に入りの削除処理を行う
         */
        private void deleteFavorite() {

            //アクティビティを取得
            BaseActivity baseActivity = (BaseActivity) getActivity();

            //お気に入りを削除する
            FavoriteService favoriteService = baseActivity.getRetrofit().create(FavoriteService.class);
            Call<RouteDto> reps = favoriteService.deleteFavoriteRoute(favoriteRouteId, baseActivity.myAccount.accessToken);
            reps.enqueue(new Callback<RouteDto>() {
                @Override
                public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                    RouteDto routeDto = response.body();

                    //アクティビティを取得
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りの登録処理を行う
                            deleteFavorite();
                        }
                    };
                    //アクセストークンのチェック
                    if(!baseActivity.checkAccessToken(routeDto.resultCode, listener)) {
                        return ;
                    }

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == routeDto.resultCode) {

                        //現在登録されているルートの情報を登録する
                        RouteDao routeDao = new RouteDao();
                        Route route = routeDao.getRoute(routeDto.routeId, baseActivity.myAccount.accountId);

                        if (route != null && route.routeKbnCd != BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                            //ルート情報の削除
                            //参加者情報の削除
                            RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
                            routeParticipantDao.deleteRouteParticipant(routeDto.routeId, baseActivity.myAccount.accountId);

                            //目的地情報の削除
                            DestinationDao destinationDao = new DestinationDao();
                            destinationDao.deleteDestinationByRouteId(routeDto.routeId, baseActivity.myAccount.accountId);

                            //アルバム情報の削除
                            AlbumDataDao albumDataDao = new AlbumDataDao();
                            albumDataDao.deleteAlbumDataByRouteId(routeDto.routeId, baseActivity.myAccount.accountId);

                            //ルート情報の削除
                            routeDao.deleteRoute(routeDto.routeId, baseActivity.myAccount.accountId);
                        }

                        return;
                    } else {
                        //メッセージを表示
                        baseActivity.dispToast(getResources().getString(R.string.error_insert, "お気に入り"));
                        //トグルボタンの表示を戻す
                        if (favoritePushButton != null) {
                            favoritePushButton.setClickable(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteDto> call, Throwable t) {

                    //アクティビティを取得
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    baseActivity.dispToast(getResources().getString(R.string.error_transceiver));

                    //トグルボタンの表示を戻す
                    if (favoritePushButton != null) {
                        favoritePushButton.setClickable(false);
                    }
                }
            });
        }
    }
}
