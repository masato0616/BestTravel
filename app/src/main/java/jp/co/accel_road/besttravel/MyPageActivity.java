package jp.co.accel_road.besttravel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.BlockDao;
import jp.co.accel_road.besttravel.dao.FriendDao;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.dao.NoticeDao;
import jp.co.accel_road.besttravel.dao.RouteDao;
import jp.co.accel_road.besttravel.entity.Block;
import jp.co.accel_road.besttravel.entity.Friend;
import jp.co.accel_road.besttravel.entity.Notice;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.MyAccountAllDataDto;
import jp.co.accel_road.besttravel.model.NoticeDto;
import jp.co.accel_road.besttravel.model.NoticeListDto;
import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.service.AccountService;
import jp.co.accel_road.besttravel.service.NoticeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * マイページ画面のアクティビティ
 */
public class MyPageActivity extends BaseActivity {

    /** マイルートボタン */
    private TextView lblMyRoute;
    /** ルート検索ボタン */
    private TextView lblSearchRoute;
    /** 友達ボタン */
    private TextView lblFriend;
    /** メールボタン */
    private TextView lblMail;
    /** 設定ボタン */
    private TextView lblSettings;

    /** お知らせのリスト */
    private List<NoticeDto> noticeDtoList;
    /** お知らせ一覧のアダプター */
    private NoticeDtoListAdapter noticeDtoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        //ステータス情報が存在しない場合に、ログイン画面を表示する。
        if (myAccount == null) {

            //ログイン画面を表示
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_LOGIN);
            return;
        }

        //ツールバーの設定
        setToolbar();

        //マイルートボタン
        lblMyRoute = (TextView)findViewById(R.id.lblMyRoute);
        lblMyRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //未ログインの場合は、ダイアログで使用できない事を通知する
                if (myAccount.accountId == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.message_not_login_not_use));
                    return;
                }

                //アカウントが凍結している場合は、ダイアログで使用できない事を通知する
                if (myAccount.freezeFlg) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.error_freeze));
                    return;
                }

                //マイルート一覧画面を表示
                Intent intent = new Intent(getApplicationContext(), RouteListActivity.class);
                startActivity(intent);
            }
        });

        //ルート検索ボタン
        lblSearchRoute = (TextView)findViewById(R.id.lblSearchRoute);
        lblSearchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ダイアログを表示する
                dispInfomationDialog(getResources().getString(R.string.message_working_not_use));

//                //ルート検索画面を表示
//                Intent intent = new Intent(getApplicationContext(), RouteSearchActivity.class);
//                startActivity(intent);
            }
        });

        //友達ボタン
        lblFriend = (TextView)findViewById(R.id.lblFriend);
        lblFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //未ログインの場合は、ダイアログで使用できない事を通知する
                if (myAccount.accountId == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.message_not_login_not_use));
                    return;
                }
                //アカウントが凍結している場合は、ダイアログで使用できない事を通知する
                if (myAccount.freezeFlg) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.error_freeze));
                    return;
                }

                //友達一覧画面を表示
                Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                startActivity(intent);
            }
        });

        //メールボタン
        lblMail = (TextView)findViewById(R.id.lblMail);
        lblMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //未ログインの場合は、ダイアログで使用できない事を通知する
                if (myAccount.accountId == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.message_not_login_not_use));
                    return;
                }
                //アカウントが凍結している場合は、ダイアログで使用できない事を通知する
                if (myAccount.freezeFlg) {
                    //ダイアログを表示する
                    dispInfomationDialog(getResources().getString(R.string.error_freeze));
                    return;
                }

                //メール一覧画面を表示
                Intent intent = new Intent(getApplicationContext(), DirectMessageListActivity.class);
                startActivity(intent);
            }
        });

        //設定ボタン
        lblSettings = (TextView)findViewById(R.id.lblSettings);
        lblSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //設定画面を表示
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        //権限情報の許可を申請
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            //地図情報
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            //ストレージ
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[0]), 1);
            }
        }

        //マイアカウントの値を取得する。
        getMyAccount();

        //お知らせのリストをローカルDBより取得する。
        getNoticeLocal();

        //お知らせのリスト
        ListView lisNoticeList = (ListView)findViewById(R.id.lisNoticeList);
        lisNoticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //お知らせ詳細画面を表示
                Intent intent = new Intent(getApplicationContext(), NoticeDetailActivity.class);
                //お知らせIDを取得
                Long noticeId = (Long)view.getTag();
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_NOTICE_ID, noticeId);
                startActivity(intent);
            }
        });
        noticeDtoListAdapter = new NoticeDtoListAdapter();
        lisNoticeList.setAdapter(noticeDtoListAdapter);

        //お知らせのリストをサーバーより取得する。
        getNoticeList();
    }

    /**
     * お気に入りのリストをDBより取得する。
     */
    private void getNoticeLocal() {
        NoticeDao noticeDao = new NoticeDao();

        List<Notice> noticeList = noticeDao.getNoticeList();

        if (noticeDtoList == null) {
            noticeDtoList = new ArrayList<>();
        } else {
            noticeDtoList.clear();
        }

        for (Notice notice: noticeList) {
            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setNotice(notice);
            noticeDtoList.add(noticeDto);
        }
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
            //ログイン画面からの戻り時処理
            case BestTravelConstant.ACTIVITY_REQUEST_KEY_LOGIN:
                if (resultCode == RESULT_CANCELED) {
                    //キャンセルで戻ってきた場合は、アプリを終了する
                    finish();
                }
        }
    }

    /**
     * マイアカウントの情報をサーバーより取得し更新する
     */
    private void getMyAccount() {

        //マイアカウントの情報を取得する
        AccountService accountService = getRetrofit().create(AccountService.class);
        Call<MyAccountAllDataDto> reps = accountService.getMyAccountData(myAccount.accessToken);
        reps.enqueue(new Callback<MyAccountAllDataDto>() {
            @Override
            public void onResponse(Call<MyAccountAllDataDto> call, Response<MyAccountAllDataDto> response) {

                //戻り値の取得
                MyAccountAllDataDto myAccountAllDataDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //マイアカウントを再取得する
                        getMyAccount();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(myAccountAllDataDto.resultCode, listener)) {
                    return ;
                }

                //凍結エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_FREEZE_ERROR == myAccountAllDataDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_freeze));

                    //マイステータスを更新する
                    //凍結フラグ
                    myAccount.freezeFlg = true;
                    MyAccountDao myAccountDao = new MyAccountDao();
                    myAccountDao.updateMyAccount(myAccount);

                    return;
                }
                //エラーが発生した場合
                if (BestTravelConstant.RESULT_CODE_ERROR == myAccountAllDataDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_not_exist_user));

                    //ログアウトする
                    logout();

                    return;
                }
                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == myAccountAllDataDto.resultCode) {
                    //マイステータスを更新する
                    MyAccountDao myAccountDao = new MyAccountDao();
                    FriendDao friendDao = new FriendDao();
                    BlockDao blockDao = new BlockDao();

                    //マイステータスを更新する
                    myAccount = myAccountAllDataDto.myAccountDto.getLastLoginMyAccount(myAccount);

                    //友達を全て削除する
                    friendDao.deleteAllFriend(myAccount.accountId);

                    //フレンドリストを取得する
                    for (AccountDto friendDto: myAccountAllDataDto.friendListDto.friendList) {
                        //ローカルのDBに友達情報を登録する
                        Friend resultFriend = friendDto.getFriend(myAccount);
                        resultFriend.updateDate = new Date();
                        friendDao.insertFriend(resultFriend);
                    }

                    //ブロック情報を全て削除する
                    blockDao.deleteAllBlock(myAccount.accountId);

                    //フレンドリストを取得する
                    for (AccountDto blockDto: myAccountAllDataDto.blockListDto.blockList) {
                        //ローカルのDBにブロック情報を登録する
                        Block resultBlock = blockDto.getBlock(myAccount);
                        resultBlock.updateDate = new Date();
                        blockDao.insertBlock(resultBlock);
                    }

                    saveDbMyRouteList(myAccountAllDataDto.myRouteDtoList);
                    saveDbFavoriteRouteList(myAccountAllDataDto.favoriteRouteDtoList);


                    //ルート検索時間の更新
                    myAccount.myRouteGetLastDate = myAccountAllDataDto.getLastDate;
                    myAccount.favoriteGetLastDate = myAccountAllDataDto.getLastDate;
                    //凍結フラグ
                    myAccount.freezeFlg = false;
                    myAccountDao.updateMyAccount(myAccount);
                }
            }

            @Override
            public void onFailure(Call<MyAccountAllDataDto> call, Throwable t) {
                //エラーメッセージを表示
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * サーバーより取得したマイルートリストをDBに保存する。
     *
     * @param routeDtoList サーバーから取得したルートリスト
     */
    private void saveDbMyRouteList(List<RouteDto> routeDtoList) {

        //リストが取得できた場合は、ルームリストを更新する
        if (routeDtoList == null) {
            return;
        }
        RouteDao routeDao = new RouteDao();

        //ルートリストを置き換える
        for (RouteDto routeDto: routeDtoList) {

            //削除対象の場合
            if (routeDto.deleteFlg) {
                //DBよりデータを削除
                routeDao.deleteRoute(routeDto.routeId, myAccount.accountId);
            } else {
                Route route = routeDao.getRoute(routeDto.routeId, myAccount.accountId);
                if (route == null) {
                    //新規登録
                    routeDao.insertRoute(routeDto.getMyRoute(route, myAccount.accountId));

                } else {

                    //更新日時が古い場合は、処理を行わない
                    if (route.updateDate.compareTo(routeDto.updateDate) > 0) {
                        continue;
                    }

                    //更新
                    routeDao.updateRoute(routeDto.getMyRoute(route, myAccount.accountId));
                }
            }
        }
    }

    /**
     * サーバーより取得したお気に入りルートリストをDBに保存する。
     *
     * @param routeDtoList サーバーから取得したルートリスト
     */
    private void saveDbFavoriteRouteList(List<RouteDto> routeDtoList) {

        //リストが取得できた場合は、ルームリストを更新する
        if (routeDtoList == null) {
            return;
        }

        RouteDao routeDao = new RouteDao();

        //ルートリストを置き換える
        for (RouteDto routeDto: routeDtoList) {

            //削除対象の場合
            if (routeDto.deleteFlg) {
                //DBよりデータを削除
                routeDao.deleteRoute(routeDto.routeId, myAccount.accountId);
            } else {
                Route route = routeDao.getRoute(routeDto.routeId, myAccount.accountId);
                if (route == null) {
                    //新規登録
                    routeDao.insertRoute(routeDto.getFavoriteRoute(null, myAccount.accountId));

                } else {

                    //更新日時が古い場合は、処理を行わない
                    if (route.updateDate.compareTo(routeDto.updateDate) > 0) {
                        continue;
                    }

                    //更新
                    routeDao.updateRoute(routeDto.getFavoriteRoute(route, myAccount.accountId));
                }
            }
        }
    }

    /**
     * お知らせリストをサーバーより取得する。
     */
    private void getNoticeList() {

        NoticeService noticeService = getRetrofit().create(NoticeService.class);
        Call<NoticeListDto> reps = noticeService.getNoticeList();
        reps.enqueue(new Callback<NoticeListDto>() {

            @Override
            public void onResponse(Call<NoticeListDto> call, Response<NoticeListDto> response) {
                //戻り値の取得
                NoticeListDto noticeListDto = response.body();

                //正常終了した場合
                if (BestTravelConstant.RESULT_CODE_OK == noticeListDto.resultCode) {

                    //お知らせ情報をDBに登録
                    NoticeDao noticeDao = new NoticeDao();
                    for (NoticeDto noticeDto: noticeListDto.noticeDtoList) {
                        Notice notice = noticeDto.getNotice();
                        Notice localNotice = noticeDao.getNotice(notice.noticeId);

                        if (!noticeDto.deleteFlg) {
                            if (localNotice == null) {
                                noticeDao.insertNotice(notice);
                            } else {
                                noticeDao.updateNotice(notice);
                            }
                        } else {
                            if (localNotice != null) {
                                noticeDao.deleteNotice(notice.noticeId);
                            }
                        }
                    }
                    //お知らせのリストをローカルDBより取得する。
                    getNoticeLocal();

                    //リストの再描画
                    noticeDtoListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NoticeListDto> call, Throwable t) {
                //エラーメッセージを表示
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * お知らせリストのアダプタークラス
     */
    public class NoticeDtoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return noticeDtoList.size();
        }

        @Override
        public Object getItem(int i) {
            return noticeDtoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return noticeDtoList.get(i).noticeId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.view_notice_item, viewGroup, false);
            }

            //友達情報の取得
            NoticeDto noticeDto = noticeDtoList.get(i);

            // 開始日
            TextView lblStartDate = (TextView)view.findViewById(R.id.lblStartDate);
            lblStartDate.setText(BestTravelUtil.convertDateToStringDispDate(noticeDto.startDate));

            // タイトル
            TextView lblTitle = (TextView)view.findViewById(R.id.lblTitle);
            lblTitle.setText(noticeDto.title);

            //お知らせID
            view.setTag(noticeDto.noticeId);

            return view;
        }
    }
}
