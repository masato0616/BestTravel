package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.AlbumDataDao;
import jp.co.accel_road.besttravel.dao.ChatMessageDao;
import jp.co.accel_road.besttravel.dao.DestinationDao;
import jp.co.accel_road.besttravel.dao.RouteDao;
import jp.co.accel_road.besttravel.dao.RouteParticipantDao;
import jp.co.accel_road.besttravel.entity.AlbumData;
import jp.co.accel_road.besttravel.entity.ChatMessage;
import jp.co.accel_road.besttravel.entity.Destination;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.entity.RouteParticipant;
import jp.co.accel_road.besttravel.fragment.DestinationAlbumFragment;
import jp.co.accel_road.besttravel.fragment.DestinationListFragment;
import jp.co.accel_road.besttravel.fragment.DestinationMapFragment;
import jp.co.accel_road.besttravel.fragment.GroupChatFragment;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.RouteListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.AlbumDataDto;
import jp.co.accel_road.besttravel.model.ChatMessageDto;
import jp.co.accel_road.besttravel.model.DestinationDto;
import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.service.RouteService;
import jp.co.accel_road.besttravel.view.SlidingTabLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 目的地を表示するアクティビティ
 */
public class RouteDetailActivity extends BaseActivity implements RouteListener {

    /** タブ表示のためのアダプター */
    private SectionsPagerAdapter sectionsPagerAdapter;

    /** ルート区分コード */
    private int routeKbnCd;
    /** ルート情報 */
    private RouteDto routeDto;
    /** 現在表示している日付 */
    private Date dispDate;

    /** 一覧表示タブ */
    private DestinationListFragment destinationListFragment;
    /** マップ表示タブ */
    private DestinationMapFragment desinationMapFragment;
    /** アルバム一覧タブ */
    private DestinationAlbumFragment destinationAlbumFragment;
    /** グループチャットタブ */
    private GroupChatFragment groupChatFragment;
    /** ナビゲーション */
    private DrawerLayout drwRouteDetail;

    /** タブを表示するページャー */
    private ViewPager mViewPager;

    /** ヘッダー画像 */
    private ImageView imgHeader;
    /** アイコン画像 */
    private ImageView imgIcon;
    /** ルート名 */
    private TextView lblRouteTitle;
    /** 滞在開始日付 */
    private TextView lblStayStartDate;
    /** 滞在終了日付 */
    private TextView lblStayEndDate;
    /** 旅の参加者一覧*/
    ParticipantListAdapter participantListAdapter;
    /** 公開範囲 */
    private TextView lblMyRouteOpenRangeCd;
    /** 旅の説明 */
    private TextView lblDescription;

    /**
     * 初期表示時処理
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        //ツールバーの設定
        setToolbar();

        //ナビゲーションアイコンの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drwRouteDetail = (DrawerLayout) findViewById(R.id.drwRouteDetail);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drwRouteDetail, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        routeDto = (RouteDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO);
        //ルート区分コードを取得
        routeKbnCd = intent.getIntExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD, -1);

        //初期表示時の日付を設定
        dispDate = routeDto.startDate;

        //ルート区分コードがその他以外（マイルート、お気に入りルート）の場合にローカルDBより値を取得する
        if (routeKbnCd != BestTravelConstant.ROUTE_KBN_CD_OTHER_ROUTE) {
            //ルート参加者のリストをローカルDBより取得する
            RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
            List<RouteParticipant> routeParticipantList = routeParticipantDao.getRouteParticipantList(routeDto.routeId, myAccount.accountId);
            routeDto.setParticipantList(routeParticipantList);

            //目的地のリストをローカルDBより取得する
            DestinationDao destinationDao = new DestinationDao();
            List<Destination> destinationDbList = destinationDao.getDispDestinationList(routeDto.routeId, dispDate, myAccount.accountId);
            routeDto.setDestinationDtoList(destinationDbList);

            //アルバムのリストをローカルDBより取得する
            AlbumDataDao albumDataDao = new AlbumDataDao();
            List<AlbumData> albumDataList = albumDataDao.getDispAlbumDataListByRouteId(routeDto.routeId, dispDate, myAccount.accountId);
            routeDto.setAlbumDataDtoList(albumDataList);

            //チャットメッセージのリストをローカルDBより取得する
            ChatMessageDao chatMessageDao = new ChatMessageDao();
            List<ChatMessage> chatMessageList = chatMessageDao.getChatMessageList(routeDto.routeId, myAccount.accountId);
            routeDto.setChatMessageDtoList(chatMessageList);
        }

        //ナビゲーションビュー内の設定
        NavigationView navRouteSide = (NavigationView) findViewById(R.id.navRouteSide);
        View viewRouteDetailSideHeader = navRouteSide.getHeaderView(0);

        //ヘッダー画像
        imgHeader = (ImageView)viewRouteDetailSideHeader.findViewById(R.id.imgHeader);
        // アイコン画像
        imgIcon = (ImageView)viewRouteDetailSideHeader.findViewById(R.id.imgIcon);
        // ルート名
        lblRouteTitle = (TextView)viewRouteDetailSideHeader.findViewById(R.id.lblRouteTitle);
        // 滞在開始日付
        lblStayStartDate = (TextView)viewRouteDetailSideHeader.findViewById(R.id.lblStayStartDate);
        // 滞在終了日付
        lblStayEndDate = (TextView)viewRouteDetailSideHeader.findViewById(R.id.lblStayEndDate);
        // 旅の参加者一覧
        GridView grdParticipantList = (GridView)viewRouteDetailSideHeader.findViewById(R.id.grdParticipantList);
        //旅の参加者一覧のアダプター設定
        participantListAdapter = new ParticipantListAdapter();
        grdParticipantList.setAdapter(participantListAdapter);

        // 公開範囲
        lblMyRouteOpenRangeCd = (TextView)viewRouteDetailSideHeader.findViewById(R.id.lblMyRouteOpenRangeCd);
        // 旅の説明
        lblDescription = (TextView)viewRouteDetailSideHeader.findViewById(R.id.lblDescription);
        // ルート編集ボタン
        Button btnRouteEdit = (Button)viewRouteDetailSideHeader.findViewById(R.id.btnRouteEdit);
        btnRouteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ルート検索画面を表示
                Intent intent = new Intent(getApplicationContext(), RouteEditActivity.class);
                //ルート
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO, routeDto);
                startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_ROUTE_EDIT);
            }
        });
        //ルート区分コードがマイルート以外の場合
        if (routeKbnCd != BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
            //ルート編集ボタン非表示
            btnRouteEdit.setVisibility(View.GONE);
        }

        //ナビゲーションウィンドウの描画
        setNavigationView();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sdtTitleTab);
        slidingTabLayout.setViewPager(mViewPager);

        //マイルートの詳細情報を取得
        refreshLatestRoute();
    }

    @Override
    public void onStart(){
        super.onStart();

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
        super.onActivityResult(requestCode, resultCode, intent);
        //GPSの設定の戻り時処理
        if (requestCode == BestTravelConstant.RESOLUTION_REQUEST_KEY_GPS) {
            //目的地マップフラグメントを表示する
            desinationMapFragment.onActivityResult(requestCode, resultCode, intent);

        } else if (requestCode == BestTravelConstant.ACTIVITY_REQUEST_KEY_ROUTE_EDIT) {
            //ルート編集画面からの戻り時処理

            //マイルートの詳細情報を取得
            refreshLatestRoute();
        }
    }

    /**
     * タブを表示するアダプター
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    //地図表示
                    if (desinationMapFragment == null) {
                        desinationMapFragment = new DestinationMapFragment();
                    }
                    return desinationMapFragment;
                case 1:
                    //目的地一覧表示
                    if (destinationListFragment == null) {
                        destinationListFragment = new DestinationListFragment();
                    }
                    return destinationListFragment;
                case 2:
                    //アルバム一覧表示
                    if (destinationAlbumFragment == null) {
                        destinationAlbumFragment = new DestinationAlbumFragment();
                    }
                    return destinationAlbumFragment;
                case 3:
                    //グループチャット表示
                    if (groupChatFragment == null) {
                        groupChatFragment = new GroupChatFragment();
                    }
                    return groupChatFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            //ルート区分コードがマイルートの場合4、マイルート以外（その他、お気に入りルート）の場合3（グループチャットは表示しない）
            if (routeKbnCd != BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                return 3;
            }
           return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "地図";
                case 1:
                    return "一覧";
                case 2:
                    return "アルバム";
                case 3:
                    return "チャット";
            }
            return null;
        }

        /**
         * 再描画処理を行う。
         */
        public void redraw() {

            //一覧表示の再描画
            if (destinationListFragment != null) {
                destinationListFragment.redrow();
            }
            //地図表示の再描画
            if (desinationMapFragment != null) {
                desinationMapFragment.redrow();
            }
            //アルバム一覧の再描画
            if (destinationAlbumFragment != null) {
                destinationAlbumFragment.redrow();
            }
            //グループチャットの再描画
            if (groupChatFragment != null) {
                groupChatFragment.redrow();
            }

            //ナビゲーションウィンドウの再描画
            setNavigationView();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    /**
     * ルート情報を最新化する
     */
    @Override
    public void refreshLatestRoute() {

        //ルート区分コードがマイルートの場合
        if (routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
            //マイルートの詳細情報を取得
            if (routeDto.destinationGetLastDate == null) {
                getEditRouteDetail();
            } else {
                //マイルートの詳細情報を差分のみ取得
                getEditRouteDifferenceDetail();
            }
        } else if (routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_FAVORITE_ROUTE) {
            //ルートの詳細情報を取得
            if (routeDto.destinationGetLastDate == null) {
                getOtherRouteDetail();
            } else {
                //ルートの詳細情報を差分のみ取得
                getOtherRouteDifferenceDetail();
            }
        } else {
            getOtherRouteDetail();
        }


    }

    /**
     * マイルートの詳細情報を全て取得する
     */
    private void getEditRouteDetail() {

        //マイルートの詳細情報を取得
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.getEditRouteDetail(routeDto.routeId, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //ルート情報を置き換える
                RouteDto resultRouteDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイルートの詳細情報を再取得する
                        getEditRouteDetail();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultRouteDto.resultCode, listener)) {
                    return ;
                }

                //エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_ERROR == resultRouteDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_get, "ルート情報"));

                    return;
                }
                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == resultRouteDto.resultCode) {
                    //ルートの詳細情報をローカルDBに保存する。
                    saveRouteDetail(resultRouteDto);
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }
    /**
     * マイルートの詳細情報を差分のみ取得する
     */
    private void getEditRouteDifferenceDetail() {

        //マイルートの詳細情報を取得
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.getEditRouteDifferenceDetail(routeDto.routeId, BestTravelUtil.convertJsonDateToString(routeDto.destinationGetLastDate), myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //ルート情報を置き換える
                RouteDto resultRouteDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイルートの詳細情報を差分のみ再取得する
                        getEditRouteDifferenceDetail();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultRouteDto.resultCode, listener)) {
                    return ;
                }
                //エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_ERROR == resultRouteDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_get, "ルート情報"));

                    return;
                }
                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == resultRouteDto.resultCode) {
                    //ルートの詳細情報をローカルDBに保存する。
                    saveRouteDetail(resultRouteDto);
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * お気に入りルートの詳細情報を全て取得する
     */
    private void getOtherRouteDetail() {

        //ルートの詳細情報を取得
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.getRouteDetail(routeDto.routeId, myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //ルート情報を置き換える
                RouteDto resultRouteDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイルートの詳細情報を再取得する
                        getOtherRouteDetail();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultRouteDto.resultCode, listener)) {
                    return ;
                }

                //エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_ERROR == resultRouteDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_get, "ルート情報"));

                    return;
                }
                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == resultRouteDto.resultCode) {
                    //ルート区分コードがその他ルートの場合
                    if (routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_OTHER_ROUTE) {
                        //以前のその他ルートの情報を全て削除する
                        deleteAllOtherRoute();
                    }
                    //ルートの詳細情報をローカルDBに保存する。
                    saveRouteDetail(resultRouteDto);
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * その他ルートの情報を全て削除する
     */
    private void deleteAllOtherRoute() {

        DestinationDao destinationDao = new DestinationDao();
        AlbumDataDao albumDataDao = new AlbumDataDao();

        //ルート情報
        RouteDao routeDao = new RouteDao();
        List<Route> routeList = routeDao.getOtherRouteList();
        for (Route route: routeList) {

            //目的地情報を削除
            destinationDao.deleteDestinationByRouteId(route.routeId, route.myAccountId);
            //アルバム情報を削除
            albumDataDao.deleteAlbumDataByRouteId(route.routeId, route.myAccountId);
            //ルート情報を削除
            routeDao.deleteRoute(route.routeId, route.myAccountId);
        }
    }


    /**
     * お気に入りルートの詳細情報を差分のみ取得する
     */
    private void getOtherRouteDifferenceDetail() {

        //マイルートの詳細情報を取得
        RouteService routeService = getRetrofit().create(RouteService.class);
        Call<RouteDto> reps = routeService.getRouteDifferenceDetail(routeDto.routeId, BestTravelUtil.convertJsonDateToString(routeDto.destinationGetLastDate), myAccount.accessToken);
        reps.enqueue(new Callback<RouteDto>() {
            @Override
            public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                //ルート情報を置き換える
                RouteDto resultRouteDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイルートの詳細情報を差分のみ再取得する
                        getOtherRouteDifferenceDetail();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(resultRouteDto.resultCode, listener)) {
                    return ;
                }
                //エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_ERROR == resultRouteDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_get, "ルート情報"));

                    return;
                }
                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == resultRouteDto.resultCode) {
                    //ルートの詳細情報をローカルDBに保存する。
                    saveRouteDetail(resultRouteDto);
                }
            }

            @Override
            public void onFailure(Call<RouteDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * ルートの詳細情報をローカルDBに保存する。
     * @param resultRouteDto
     */
    private void saveRouteDetail(RouteDto resultRouteDto) {

        //ルート情報を更新
        routeDto.copyRouteDto(resultRouteDto);

        // 参加者リストを更新
        routeDto.participantList = resultRouteDto.participantList;

        //ローカルDBの参加者情報を削除
        RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
        routeParticipantDao.deleteRouteParticipant(routeDto.routeId, myAccount.accountId);
        //ローカルDBの参加者情報を追加
        for (AccountDto participant: routeDto.participantList) {
            routeParticipantDao.insertRouteParticipant(participant.getRouteParticipant(myAccount));
        }

        //各DAOを作成
        DestinationDao destinationDao = new DestinationDao();
        AlbumDataDao albumDataDao = new AlbumDataDao();
        ChatMessageDao chatMessageDao = new ChatMessageDao();

        // 目的地のリストを更新
        for (DestinationDto destinationDto: resultRouteDto.destinationDtoList) {
            //削除対象かどうか判断する
            if (destinationDto.deleteFlg) {
                //目的地をローカルDBより取得する
                Destination destination = destinationDao.getDestination(destinationDto.destinationId, myAccount.accountId);

                //ローカルDBに登録されている場合は、目的地、アルバムデータを削除する
                if (destination != null) {
                    destinationDao.deleteDestinationByDestinationId(destinationDto.destinationId, myAccount.accountId);
                    albumDataDao.deleteAlbumDataByDestinationId(destinationDto.destinationId, myAccount.accountId);
                }

            } else {
                //目的地をローカルDBより取得する
                Destination destination = destinationDao.getDestination(destinationDto.destinationId, myAccount.accountId);

                //ローカルDBに登録されてない場合は、目的地を登録する
                if (destination == null) {
                    destinationDao.insertDestination(destinationDto.getDestination(myAccount));
                } else {
                    //ローカルDBに登録されている場合は、目的地を更新する
                    destinationDao.updateDestination(destinationDto.getDestination(myAccount));
                }

                // アルバムデータのリストを更新
                for (AlbumDataDto albumDataDto: destinationDto.albumDataDtoList) {
                    //削除対象かどうか判断する
                    if (albumDataDto.deleteFlg) {
                        //アルバムデータをローカルDBより取得する
                        AlbumData albumData = albumDataDao.getAlbumData(albumDataDto.albumDataId, myAccount.accountId);

                        //ローカルDBに登録されている場合は、アルバムデータを削除する
                        if (albumData != null) {
                            albumDataDao.deleteAlbumData(albumDataDto.albumDataId, myAccount.accountId);
                        }

                    } else {
                        //アルバムデータをローカルDBより取得する
                        AlbumData albumData = albumDataDao.getAlbumData(albumDataDto.albumDataId, myAccount.accountId);

                        //ローカルDBに登録されていない場合は、アルバムデータを登録する
                        if (albumData == null) {
                            albumDataDao.insertAlbumData(albumDataDto.getAlbumData(myAccount));
                        } else {
                            //ローカルDBに登録されている場合は、アルバムデータを登録する
                            albumDataDao.updateAlbumData(albumDataDto.getAlbumData(myAccount));
                        }
                    }
                }
            }
        }

        //ローカルDBのチャットメッセージ情報を追加
        if (resultRouteDto.chatMessageDtoList != null) {
            for (ChatMessageDto chatMessageDto: resultRouteDto.chatMessageDtoList) {
                ChatMessage chatMessage = chatMessageDao.getChatMessage(chatMessageDto.chatMessageId, myAccount.accountId);

                //データが存在しない場合は、登録する
                if (chatMessage == null) {
                    chatMessageDao.insertChatMessage(chatMessageDto.getChatMessage(myAccount));
                }
            }
        }

        //ルート情報を更新する
        RouteDao routeDao = new RouteDao();
        Route routeDb = routeDao.getRoute(resultRouteDto.routeId, myAccount.accountId);

        Route route = null;
        if (routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
            route = resultRouteDto.getMyRoute(routeDb, myAccount.accountId);
        } else if (routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_FAVORITE_ROUTE) {
            route = resultRouteDto.getFavoriteRoute(routeDb, myAccount.accountId);
        } else {
            route = resultRouteDto.getOtherRoute(myAccount.accountId);
        }

        if (routeDb == null) {
            routeDao.insertRoute(route);
        } else {
            routeDao.updateRoute(route);
        }

        routeDto.destinationGetLastDate = route.destinationLastUpdateDate;

        //目的地のリストをローカルDBより取得する
        List<Destination> destinationDbList = destinationDao.getDispDestinationList(routeDto.routeId, dispDate, myAccount.accountId);
        routeDto.setDestinationDtoList(destinationDbList);

        //アルバムのリストをローカルDBより取得する
        List<AlbumData> albumDataList = albumDataDao.getDispAlbumDataListByRouteId(routeDto.routeId, dispDate, myAccount.accountId);
        routeDto.setAlbumDataDtoList(albumDataList);

        //チャットメッセージのリストをローカルDBより取得する
        List<ChatMessage> chatMessageList = chatMessageDao.getChatMessageList(routeDto.routeId, myAccount.accountId);
        routeDto.setChatMessageDtoList(chatMessageList);

        //画面に表示しているものを更新する。
        sectionsPagerAdapter.redraw();
    }

    /**
     * 現在表示している日付の目的地のリストの中から、指定した目的地IDの目的地を取得する
     *
     * @param destinationId 目的地ID
     * @return 目的地
     */
    public DestinationDto getDispDestinationDto(Long destinationId) {

        List<DestinationDto> destinationDtoList = routeDto.destinationDtoList;

        if (destinationDtoList == null) {
            return null;
        }
        for (DestinationDto destinationDto: destinationDtoList) {
            if (destinationId.longValue() == destinationDto.destinationId.longValue()) {
                return destinationDto;
            }
        }
        return null;
    }

    /**
     * 表示日付の目的地のリストを取得する
     *
     * @return 目的地のリスト
     */
    @Override
    public List<DestinationDto> getDispDestinationDtoList() {

        if (routeDto.destinationDtoList == null) {
            return new ArrayList<>();
        }
        return routeDto.destinationDtoList;
    }

    /**
     * 現在表示している表示日付を取得する
     *
     * @return
     */
    @Override
    public Date getDispDate() {
        return dispDate;
    }

    /**
     * 現在表示している表示日付を取得する
     *
     * @return
     */
    @Override
    public void changeDispDate(Date newDispDate) {

        //表示日付を変更
        dispDate = newDispDate;

        //目的地のリストをローカルDBより取得する
        DestinationDao destinationDao = new DestinationDao();
        List<Destination> destinationDbList = destinationDao.getDispDestinationList(routeDto.routeId, dispDate, myAccount.accountId);
        routeDto.setDestinationDtoList(destinationDbList);

        //アルバムのリストをローカルDBより取得する
        AlbumDataDao albumDataDao = new AlbumDataDao();
        List<AlbumData> albumDataList = albumDataDao.getDispAlbumDataListByRouteId(routeDto.routeId, dispDate, myAccount.accountId);
        routeDto.setAlbumDataDtoList(albumDataList);

        //画面に表示しているものを更新する。
        sectionsPagerAdapter.redraw();

        return;
    }

    /**
     * チャットメッセージのリストを取得する
     *
     * @return チャットメッセージのリスト
     */
    public List<ChatMessageDto> getChatMessageDtoList() {
        return routeDto.chatMessageDtoList;
    }


    /**
     * ルートIDを取得する
     *
     * @return ルートID
     */
    @Override
    public Long getRouteId() {
        return routeDto.routeId;
    }

    /**
     * 開始日を取得する
     *
     * @return 開始日
     */
    @Override
    public Date getStartDate() {
        return routeDto.startDate;
    }

    /**
     * 終了日を取得する
     *
     * @return 終了日
     */
    @Override
    public Date getEndDate() {
        return routeDto.endDate;
    }

    /**
     * ルート区分を取得する
     *
     * @return
     */
    @Override
    public int getRouteKbnCd() {
        return routeKbnCd;
    }

    /**
     * ナビゲーションビューの表示を設定する
     */
    private void setNavigationView() {

        //ヘッダー画像
        Picasso.with(this).load(routeDto.headerImageUrl).fit().centerInside().into(imgHeader);
        //アイコン画像
        Picasso.with(this).load(routeDto.iconUrl).fit().centerInside().into(imgIcon);
        //ルートタイトル
        lblRouteTitle.setText(routeDto.routeTitle);
        //滞在開始日付
        lblStayStartDate.setText(BestTravelUtil.convertDateToStringDispDate(routeDto.startDate));
        //滞在終了日付
        lblStayEndDate.setText(BestTravelUtil.convertDateToStringDispDate(routeDto.endDate));
        //公開範囲
        setMyRouteOpenRangeCd(routeDto.myRouteOpenRangeCd);
        //旅の説明
        lblDescription.setText(routeDto.routeDescription);

        //参加者の一覧を更新する
        participantListAdapter.notifyDataSetChanged();
    }

    /**
     * コード値を元に画面にて選択するラジオボタンのIDを設定する。
     *
     * @return
     */
    private void setMyRouteOpenRangeCd(int id) {

        if (BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_PARTICIPANT == id) {
            lblMyRouteOpenRangeCd.setText("参加者のみに公開");
        } else if (BestTravelConstant.MY_ROUTE_OPEN_RANGE_CD_FRIEND == id) {
            lblMyRouteOpenRangeCd.setText("友達のみに公開");
        }
        lblMyRouteOpenRangeCd.setText("全員に公開");
    }


    /**
     * 旅の参加者リストのアダプタークラス
     */
    public class ParticipantListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (routeDto == null || routeDto.participantList == null) {
                return 0;
            }
            return routeDto.participantList.size();
        }

        @Override
        public Object getItem(int i) {
            if (routeDto == null || routeDto.participantList == null) {
                return null;
            }
            return routeDto.participantList.get(i);
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
            AccountDto accountDto = routeDto.participantList.get(i);

            // アイコン画像
            ImageView imgIcon = (ImageView)view.findViewById(R.id.imgIcon);
            Picasso.with(getApplicationContext()).load(accountDto.thumbnailIconUrl).fit().centerInside().into(imgIcon);

            // 名前
            TextView lblUserName = (TextView)view.findViewById(R.id.lblUserName);
            lblUserName.setText(accountDto.userName);
            return view;
        }
    }
}
