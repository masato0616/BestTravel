package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.AlbumDataDao;
import jp.co.accel_road.besttravel.dao.BlockDao;
import jp.co.accel_road.besttravel.dao.DestinationDao;
import jp.co.accel_road.besttravel.dao.FriendDao;
import jp.co.accel_road.besttravel.dao.RouteDao;
import jp.co.accel_road.besttravel.dao.RouteParticipantDao;
import jp.co.accel_road.besttravel.entity.Block;
import jp.co.accel_road.besttravel.entity.Friend;
import jp.co.accel_road.besttravel.entity.Route;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.AccountRouteDto;
import jp.co.accel_road.besttravel.model.BlockListDto;
import jp.co.accel_road.besttravel.model.FriendListDto;
import jp.co.accel_road.besttravel.model.RouteDto;
import jp.co.accel_road.besttravel.service.AccountService;
import jp.co.accel_road.besttravel.service.BlockService;
import jp.co.accel_road.besttravel.service.FavoriteService;
import jp.co.accel_road.besttravel.service.FriendService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * アカウント詳細のアクティビティ
 */
public class AccountDetailActivity extends BaseActivity {

    /** ヘッダー画像 */
    private ImageView imgHeader;
    /** アイコン画像 */
    private ImageView imgIcon;
    /** 名前 */
    private TextView lblUserName;
    /** ユーザーID */
    private TextView lblUserId;

    /** ドライブサイズを表示するリニアレイアウト */
    private LinearLayout linDriveSize;
    /** 使用ドライブ容量 */
    private TextView lblUsedDriveSize;
    /** 最大ドライブ容量 */
    private TextView lblMaxDriveSize;
    /** ドライブ容量のプログレスバー */
    private ProgressBar prgDriveSize;

    /** 友達登録ボタン */
    private Button btnAddFriend;
    /** 友達解除ボタン */
    private Button btnRemoveFriend;
    /** マイアカウント編集ボタン */
    private Button btnMyAccountEdit;
    /** メッセージボタン */
    private Button btnSendMessage;
    /** ブロック追加ボタン */
    private Button btnAddBlock;
    /** ブロック解除ボタン */
    private Button btnRemoveBlock;

    /** 性別 */
    private TextView lblSex;
    /** 年齢 */
    private TextView lblAge;
    /** 居住地 */
    private TextView lblResidence;
    /** コメント */
    private TextView lblComment;
    /** ブロック時のメッセージ */
    private TextView lblBlockMessage;

    /** アカウントDTO */
    private AccountDto accountDto;
    /** ルートDTOのリスト */
    public List<RouteDto> routeDtoList;

    /** ルート情報リストのアダプター */
    private RouteDtoListAdapter routeDtoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        //ツールバーの設定
        setToolbar();

        //ルートリストを初期化する
        routeDtoList = new ArrayList();

        // ヘッダー画像
        imgHeader = (ImageView)findViewById(R.id.imgHeader);
        /// アイコン画像 */
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        // 名前
        lblUserName = (TextView)findViewById(R.id.lblUserName);
        // ユーザーID
        lblUserId = (TextView)findViewById(R.id.lblUserId);

        // ドライブサイズを表示するリニアレイアウト
        linDriveSize = (LinearLayout)findViewById(R.id.linDriveSize);
        // 使用ドライブ容量
        lblUsedDriveSize = (TextView)findViewById(R.id.lblUsedDriveSize);
        // 最大ドライブ容量
        lblMaxDriveSize = (TextView)findViewById(R.id.lblMaxDriveSize);
        // ドライブ容量のプログレスバー
        prgDriveSize = (ProgressBar)findViewById(R.id.prgDriveSize);

        // 性別
        lblSex = (TextView)findViewById(R.id.lblSex);
        // 年齢
        lblAge = (TextView)findViewById(R.id.lblAge);
        // 居住地
        lblResidence = (TextView)findViewById(R.id.lblResidence);
        // コメント
        lblComment = (TextView)findViewById(R.id.lblComment);
        // ブロック時のメッセージ
        lblBlockMessage = (TextView)findViewById(R.id.lblBlockMessage);
        //ルート情報のリスト
        ListView lisRouteList = (ListView)findViewById(R.id.lisRouteList);
        routeDtoListAdapter = new RouteDtoListAdapter();
        lisRouteList.setAdapter(routeDtoListAdapter);
        //リストクリック時処理
        lisRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //ルート詳細を表示
                Intent intent = new Intent(getApplicationContext(), RouteDetailActivity.class);

                long routeId = routeDtoList.get(position).routeId;
                for(int i = 0; i < routeDtoList.size(); i++) {
                    RouteDto routeDto = routeDtoList.get(i);

                    //対象のルートIDが等しい場合
                    if (routeDto.routeId == routeId) {
                        //目的地変更アクティビティを表示します
                        intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_DTO, routeDto);
                        intent.putExtra(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD, BestTravelConstant.ROUTE_KBN_CD_OTHER_ROUTE);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        // 友達登録ボタン
        btnAddFriend = (Button)findViewById(R.id.btnAddFriend);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //友達に追加する
                addFriend();
            }
        });
        // 友達解除
        btnRemoveFriend = (Button)findViewById(R.id.btnRemoveFriend);
        btnRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //友達解除する
                removeFriend();
            }
        });
        //編集
        btnMyAccountEdit = (Button)findViewById(R.id.btnMyAccountEdit);
        btnMyAccountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //マイアカウント編集画面を表示
                Intent intent = new Intent(getApplicationContext(), MyAccountEditActivity.class);
                startActivity(intent);
            }
        });
        // メッセージ
        btnSendMessage = (Button)findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ダイレクトメッセージ詳細画面を表示
                Intent intent = new Intent(getApplicationContext(), DirectMessageDetailActivity.class);
                //アカウントID
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, accountDto);
                startActivity(intent);
            }
        });
        // ブロック
        btnAddBlock = (Button)findViewById(R.id.btnAddBlock);
        btnAddBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ブロックする
                addBlock();
            }
        });
        // ブロック解除
        btnRemoveBlock = (Button)findViewById(R.id.btnRemoveBlock);
        btnRemoveBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ブロック解除する
                removeBlock();
            }
        });

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        accountDto = (AccountDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO);

        //アカウント情報を表示する
        setDispViewValue(accountDto);

        //アカウントIDがマイアカウントかどうかでボタンの表示処理を変更する
        if (accountDto.accountId.longValue() == myAccount.accountId.longValue()) {
            //マイアカウントの場合

            // ドライブサイズを表示するリニアレイアウト
            linDriveSize.setVisibility(View.VISIBLE);
            // 使用ドライブ容量
            lblUsedDriveSize.setVisibility(View.VISIBLE);
            lblUsedDriveSize.setText(String.valueOf(BestTravelUtil.getDispDriveSize(myAccount.usedDriveSize)));
            // 最大ドライブ容量
            lblMaxDriveSize.setVisibility(View.VISIBLE);
            lblMaxDriveSize.setText(String.valueOf(BestTravelUtil.getDispDriveSize(myAccount.maxDriveSize)));
            // ドライブ容量のプログレスバー
            prgDriveSize.setVisibility(View.VISIBLE);
            prgDriveSize.setMax((int)(myAccount.maxDriveSize / 1000000));
            prgDriveSize.setMax((int)(myAccount.maxDriveSize / 1000000));

            //各ボタンの表示制御を行う
            // 友達登録ボタン
            btnAddFriend.setVisibility(View.GONE);
            // 友達解除ボタン
            btnRemoveFriend.setVisibility(View.GONE);
            // マイアカウント編集ボタン
            btnMyAccountEdit.setVisibility(View.VISIBLE);
            // メッセージボタン
            btnSendMessage.setVisibility(View.GONE);
            // ブロックボタン
            btnAddBlock.setVisibility(View.GONE);
            // ブロック解除ボタン
            btnRemoveBlock.setVisibility(View.GONE);

        } else {
            //それ以外の場合

            // ドライブサイズを非表示
            linDriveSize.setVisibility(View.GONE);
            prgDriveSize.setVisibility(View.GONE);

            //各ボタンの表示制御を行う
            //未ログインの場合
            if (myAccount.accountId.longValue() == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {

                // 友達登録ボタン（非活性）
                btnAddFriend.setVisibility(View.VISIBLE);
                btnAddFriend.setClickable(false);
                // 友達解除ボタン
                btnRemoveFriend.setVisibility(View.GONE);
                // マイアカウント編集ボタン
                btnMyAccountEdit.setVisibility(View.GONE);
                // メッセージボタン（非活性）
                btnSendMessage.setVisibility(View.VISIBLE);
                btnAddFriend.setClickable(false);
                // ブロックボタン（非活性）
                btnAddBlock.setVisibility(View.VISIBLE);
                btnAddFriend.setClickable(false);
                // ブロック解除ボタン
                btnRemoveBlock.setVisibility(View.GONE);
            } else {

                // メッセージボタン
                btnSendMessage.setVisibility(View.VISIBLE);
                // マイアカウント編集ボタン
                btnMyAccountEdit.setVisibility(View.GONE);

                //ブロックしているかどうか確認する
                BlockDao blockDao = new BlockDao();
                Block block = blockDao.getBlockByAccountId(myAccount.accountId, accountDto.accountId);
                if (block == null) {
                    // ブロックボタン
                    btnAddBlock.setVisibility(View.VISIBLE);
                    // ブロック解除ボタン
                    btnRemoveBlock.setVisibility(View.GONE);
                } else {
                    // ブロックボタン
                    btnAddBlock.setVisibility(View.GONE);
                    // ブロック解除ボタン
                    btnRemoveBlock.setVisibility(View.VISIBLE);
                }

                //友達かどうか確認する
                FriendDao friendDao = new FriendDao();
                Friend friend = friendDao.getFriendByAccountId(myAccount.accountId, accountDto.accountId);
                if (friend == null) {
                    // 友達登録ボタン
                    btnAddFriend.setVisibility(View.VISIBLE);
                    // 友達解除ボタン
                    btnRemoveFriend.setVisibility(View.GONE);
                } else {
                    // 友達登録ボタン
                    btnAddFriend.setVisibility(View.GONE);
                    // 友達解除ボタン
                    btnRemoveFriend.setVisibility(View.VISIBLE);
                }
            }
        }

        //アカウントIDに紐づくユーザー情報を取得する
        getAccountRouteLoginUser();
    }

    /**
     * アカウントIDに紐づくユーザー情報を取得する
     */
    private void getAccountRouteLoginUser() {

        //ログインしていない場合
        if (myAccount.accountId == BestTravelConstant.NOT_LOGIN_LOGIN_ID) {
            //アカウントIDに紐づくユーザー情報を取得する
            AccountService accountService = getRetrofit().create(AccountService.class);
            Call<AccountRouteDto> reps = accountService.getAccountRoute(accountDto.accountId);
            reps.enqueue(new Callback<AccountRouteDto>() {
                @Override
                public void onResponse(Call<AccountRouteDto> call, Response<AccountRouteDto> response) {

                    //サーバーより取得したアカウント
                    AccountRouteDto accountRouteDto = response.body();

                    //ユーザーIDに紐づくデータが存在しない場合
                    if (BestTravelConstant.RESULT_CODE_NOTHING == accountRouteDto.resultCode) {
                        //エラーメッセージを表示
                        dispToast(getResources().getString(R.string.error_not_exist_user));
                        return;
                    }

                    //ユーザーIDにブロックされている場合
                    if (BestTravelConstant.RESULT_CODE_BLOCK == accountRouteDto.resultCode) {
                        //ブロック時のメッセージを表示
                        lblBlockMessage.setVisibility(View.VISIBLE);
                        //エラーメッセージを表示
                        dispToast(getResources().getString(R.string.error_block));
                        return;
                    }

                    //アカウント情報のセット
                    accountDto = accountRouteDto.accountDto;
                    //サーバーよりアカウント情報を取得して登録する
                    setDispViewValue(accountDto);

                    //ルート情報の登録
                    routeDtoList = accountRouteDto.routeDtoList;

                    //ソートする
                    Collections.sort(routeDtoList, new Comparator<RouteDto>() {
                        @Override
                        public int compare(RouteDto t0, RouteDto t1) {
                            if (t0.startDate.compareTo(t1.startDate) < 0) {
                                return 1;

                            } else if (t0.startDate.compareTo(t1.startDate) == 0) {

                                if (t0.endDate.compareTo(t1.endDate) < 0) {
                                    return 1;

                                } else if (t0.endDate.compareTo(t1.endDate) == 0) {
                                    if (t0.routeId.compareTo(t1.routeId) < 0) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                }
                            }
                            return -1;
                        }
                    });

                    //ルート一覧の再描画
                    routeDtoListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<AccountRouteDto> call, Throwable t) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        } else {
            //アカウントIDに紐づくユーザー情報を取得する
            AccountService accountService = getRetrofit().create(AccountService.class);
            Call<AccountRouteDto> reps = accountService.getAccountRouteLoginUser(accountDto.accountId, myAccount.accessToken);
            reps.enqueue(new Callback<AccountRouteDto>() {
                @Override
                public void onResponse(Call<AccountRouteDto> call, Response<AccountRouteDto> response) {

                    //サーバーより取得したアカウント
                    AccountRouteDto accountRouteDto = response.body();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {

                            //アカウントIDに紐づくユーザー情報を取得する
                            getAccountRouteLoginUser();
                        }
                    };
                    //アクセストークンのチェック
                    if(!checkAccessToken(accountRouteDto.resultCode, listener)) {
                        return ;
                    }

                    //ユーザーIDに紐づくデータが存在しない場合
                    if (BestTravelConstant.RESULT_CODE_NOTHING == accountRouteDto.resultCode) {
                        //エラーメッセージを表示
                        dispToast(getResources().getString(R.string.error_not_exist_user));
                        return;
                    }

                    //ユーザーIDにブロックされている場合
                    if (BestTravelConstant.RESULT_CODE_BLOCK == accountRouteDto.resultCode) {
                        //ブロック時のメッセージを表示
                        lblBlockMessage.setVisibility(View.VISIBLE);
                        //エラーメッセージを表示
                        dispToast(getResources().getString(R.string.error_block));
                        return;
                    }

                    //アカウント情報のセット
                    accountDto = accountRouteDto.accountDto;
                    //サーバーよりアカウント情報を取得して登録する
                    setDispViewValue(accountDto);

                    //ルート情報の登録
                    routeDtoList = accountRouteDto.routeDtoList;

                    //ソートする
                    Collections.sort(routeDtoList, new Comparator<RouteDto>() {
                        @Override
                        public int compare(RouteDto t0, RouteDto t1) {
                            if (t0.startDate.compareTo(t1.startDate) < 0) {
                                return 1;

                            } else if (t0.startDate.compareTo(t1.startDate) == 0) {

                                if (t0.endDate.compareTo(t1.endDate) < 0) {
                                    return 1;

                                } else if (t0.endDate.compareTo(t1.endDate) == 0) {
                                    if (t0.routeId.compareTo(t1.routeId) < 0) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                }
                            }
                            return -1;
                        }
                    });

                    //ルート一覧の再描画
                    routeDtoListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<AccountRouteDto> call, Throwable t) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_transceiver));
                }
            });
        }
    }

    /**
     * 友達の追加処理を行う
     */
    private void addFriend() {

        //友達に追加する
        FriendService friendService = getRetrofit().create(FriendService.class);
        Call<FriendListDto> reps = friendService.insertFriend(accountDto.accountId, myAccount.accessToken);
        reps.enqueue(new Callback<FriendListDto>() {
            @Override
            public void onResponse(Call<FriendListDto> call, Response<FriendListDto> response) {

                FriendListDto friendListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //友達の追加処理を行う
                        addFriend();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(friendListDto.resultCode, listener)) {
                    return ;
                }

                //ユーザーIDに紐づくデータが存在しない場合
                if (BestTravelConstant.RESULT_CODE_NOTHING == friendListDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_not_exist_user));
                    return;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == friendListDto.resultCode) {

                    //ローカルDBに友達を登録する
                    FriendDao friendDao = new FriendDao();
                    Friend friend = accountDto.getFriend(myAccount);
                    friendDao.insertFriend(friend);

                    // 友達登録ボタン
                    btnAddFriend.setVisibility(View.GONE);
                    // 友達解除ボタン
                    btnRemoveFriend.setVisibility(View.VISIBLE);

                    //メッセージを表示
                    dispToast(getResources().getString(R.string.message_friend_add));
                    return;
                } else {
                    //メッセージを表示
                    dispToast(getResources().getString(R.string.error_insert, "友達"));
                }
            }

            @Override
            public void onFailure(Call<FriendListDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * 友達から削除する。
     */
    private void removeFriend() {

        //友達解除する
        FriendService friendService = getRetrofit().create(FriendService.class);
        Call<FriendListDto> reps = friendService.removeFriend(accountDto.accountId, myAccount.accessToken);
        reps.enqueue(new Callback<FriendListDto>() {
            @Override
            public void onResponse(Call<FriendListDto> call, Response<FriendListDto> response) {

                FriendListDto friendListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //友達解除する
                        removeFriend();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(friendListDto.resultCode, listener)) {
                    return ;
                }

                //ユーザーIDに紐づくデータが存在しない場合
                if (BestTravelConstant.RESULT_CODE_NOTHING == friendListDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_not_exist_user));
                    return;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == friendListDto.resultCode) {

                    //ローカルDBに友達を登録する
                    FriendDao friendDao = new FriendDao();
                    Friend friend = friendDao.getFriendByAccountId(myAccount.accountId, accountDto.accountId);
                    if (friend != null) {
                        friendDao.deleteFriend(friend.friendId);
                    }

                    // 友達登録ボタン
                    btnAddFriend.setVisibility(View.VISIBLE);
                    // 友達解除ボタン
                    btnRemoveFriend.setVisibility(View.GONE);

                    //メッセージを表示
                    dispToast(getResources().getString(R.string.message_friend_remove));
                    return;
                } else {
                    //メッセージを表示
                    dispToast(getResources().getString(R.string.error_release, "友達"));
                }
            }

            @Override
            public void onFailure(Call<FriendListDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * ブロックの追加処理を行う
     */
    private void addBlock() {

        //ブロックに追加する
        BlockService blockService = getRetrofit().create(BlockService.class);
        Call<BlockListDto> reps = blockService.insertBlock(accountDto.accountId, myAccount.accessToken);
        reps.enqueue(new Callback<BlockListDto>() {
            @Override
            public void onResponse(Call<BlockListDto> call, Response<BlockListDto> response) {

                BlockListDto blockListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //ブロックの追加処理を行う
                        addBlock();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(blockListDto.resultCode, listener)) {
                    return ;
                }

                //ユーザーIDに紐づくデータが存在しない場合
                if (BestTravelConstant.RESULT_CODE_NOTHING == blockListDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_not_exist_user));
                    return;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == blockListDto.resultCode) {

                    //ローカルDBにブロック情報を登録する
                    BlockDao blockDao = new BlockDao();
                    Block block = accountDto.getBlock(myAccount);
                    blockDao.insertBlock(block);

                    // ブロックボタン
                    btnAddBlock.setVisibility(View.GONE);
                    // ブロック解除ボタン
                    btnRemoveBlock.setVisibility(View.VISIBLE);

                    //メッセージを表示
                    dispToast(getResources().getString(R.string.message_block_add));
                    return;
                } else {
                    //メッセージを表示
                    dispToast(getResources().getString(R.string.error_insert, "ブロック"));
                }
            }

            @Override
            public void onFailure(Call<BlockListDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * ブロックの解除処理を行う
     */
    private void removeBlock() {

        //ブロックを解除する
        BlockService blockService = getRetrofit().create(BlockService.class);
        Call<BlockListDto> reps = blockService.removeBlock(accountDto.accountId, myAccount.accessToken);
        reps.enqueue(new Callback<BlockListDto>() {
            @Override
            public void onResponse(Call<BlockListDto> call, Response<BlockListDto> response) {

                BlockListDto blockListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //ブロックの解除処理を行う
                        removeBlock();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(blockListDto.resultCode, listener)) {
                    return ;
                }

                //ユーザーIDに紐づくデータが存在しない場合
                if (BestTravelConstant.RESULT_CODE_NOTHING == blockListDto.resultCode) {
                    //エラーメッセージを表示
                    dispToast(getResources().getString(R.string.error_not_exist_user));
                    return;
                }

                //正常終了の場合
                if (BestTravelConstant.RESULT_CODE_OK == blockListDto.resultCode) {

                    //ローカルDBのブロック情報を削除する
                    BlockDao blockDao = new BlockDao();
                    Block block = blockDao.getBlockByAccountId(myAccount.accountId, accountDto.accountId);
                    if (block != null) {
                        blockDao.deleteBlock(block.blockId);
                    }

                    // ブロックボタン
                    btnAddBlock.setVisibility(View.VISIBLE);
                    // ブロック解除ボタン
                    btnRemoveBlock.setVisibility(View.GONE);

                    //メッセージを表示
                    dispToast(getResources().getString(R.string.message_block_remove));
                    return;
                } else {
                    //メッセージを表示
                    dispToast(getResources().getString(R.string.error_release, "ブロック"));
                }
            }

            @Override
            public void onFailure(Call<BlockListDto> call, Throwable t) {
                dispToast(getResources().getString(R.string.error_transceiver));
            }
        });
    }

    /**
     * 各ビューにアカウントの値を設定する
     */
    private void setDispViewValue(AccountDto accountDto) {

        //ヘッダー画像
        Picasso.with(this).load(accountDto.thumbnailHeaderImageUrl).fit().centerInside().into(imgHeader);
        // アイコン画像
        Picasso.with(getApplicationContext()).load(accountDto.thumbnailIconUrl).fit().centerInside().into(imgIcon);
        // 名前
        lblUserName.setText(accountDto.userName);
        // ユーザーID
        lblUserId.setText(accountDto.userId);
        // 性別
        setSexCd(accountDto.sexCd);
        // 年齢
        if (accountDto.age != null) {
            lblAge.setText(accountDto.age + "歳");
        } else {
            //未設定と表示
            lblAge.setText(getResources().getString(R.string.misettei));
        }
        // 居住地
        if (accountDto.prefectures != null) {
            lblResidence.setText(accountDto.prefectures);
        } else {
            //未設定と表示
            lblResidence.setText(getResources().getString(R.string.misettei));
        }

        // コメント
        lblComment.setText(accountDto.comment);
    }

    /**
     * コード値を元に性別の初期値を設定する。
     *
     * @return
     */
    private void setSexCd(Integer id) {

        if (id == null) {
            lblSex.setText(getResources().getString(R.string.misettei));
            return;
        }

        if (BestTravelConstant.SEX_CD_MAN == id) {
            lblSex.setText(R.string.sex_man);
        } else if (BestTravelConstant.SEX_CD_WOMAN == id) {
            lblSex.setText(R.string.sex_woman);
        }
    }

    /**
     * ルートのリストアダプタークラス
     */
    public class RouteDtoListAdapter extends BaseAdapter {

        LayoutInflater layoutInflater = null;
        /** お気に入りボタンを押下したルートID */
        private long favoriteRouteId;
        /** 押下したお気に入りボタン */
        private ToggleButton favoritePushButton;

        public RouteDtoListAdapter() {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            //取得する値が範囲外の場合は-1を返す
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

            RouteDto routeDto = routeDtoList.get(position);

            //アイコン画像
            ImageView imgMyRouteIcon = (ImageView)convertView.findViewById(R.id.imgMyRouteIcon);
            Picasso.with(getApplicationContext()).load(routeDto.iconUrl).fit().centerInside().into(imgMyRouteIcon);

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

            //お気に入りボタンの初期表示
            //現在登録されているルートの情報を表示
            RouteDao routeDao = new RouteDao();
            Route route = routeDao.getRoute(routeDto.routeId, myAccount.accountId);

            if (route == null) {
                //ルートが登録されていない場合
                imbFavorite.setChecked(false);
            } else if (route.routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE){
                //マイルートの場合ボタンを非表示
                imbFavorite.setVisibility(View.INVISIBLE);
            } else {
                //ルートが登録されていない場合
                imbFavorite.setChecked(true);
            }

            return convertView;
        }

        /**
         * お気に入りの登録処理を行う
         */
        private void insertFavorite() {

            //お気に入りを登録する
            FavoriteService favoriteService = getRetrofit().create(FavoriteService.class);
            Call<RouteDto> reps = favoriteService.insertFavoriteRoute(favoriteRouteId, myAccount.accessToken);
            reps.enqueue(new Callback<RouteDto>() {
                @Override
                public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                    RouteDto routeDto = response.body();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りの登録処理を行う
                            insertFavorite();
                        }
                    };
                    //アクセストークンのチェック
                    if(!checkAccessToken(routeDto.resultCode, listener)) {
                        return ;
                    }

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == routeDto.resultCode) {

                        //現在登録されているルートの情報を登録する
                        RouteDao routeDao = new RouteDao();
                        Route route = routeDao.getRoute(routeDto.routeId, myAccount.accountId);

                        if (route == null) {
                            routeDao.insertRoute(routeDto.getFavoriteRoute(route, myAccount.accountId));
                        } else if (route.routeKbnCd == BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                            //メッセージを表示
                            dispToast(getResources().getString(R.string.error_insert_favorite_my_route));
                        } else {
                            routeDao.updateRoute(routeDto.getFavoriteRoute(route, myAccount.accountId));
                        }
                        return;
                    } else {
                        //メッセージを表示
                        dispToast(getResources().getString(R.string.error_insert, "お気に入り"));
                        //トグルボタンの表示を戻す
                        if (favoritePushButton != null) {
                            favoritePushButton.setClickable(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteDto> call, Throwable t) {
                    dispToast(getResources().getString(R.string.error_transceiver));

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

            //お気に入りを削除する
            FavoriteService favoriteService = getRetrofit().create(FavoriteService.class);
            Call<RouteDto> reps = favoriteService.deleteFavoriteRoute(favoriteRouteId, myAccount.accessToken);
            reps.enqueue(new Callback<RouteDto>() {
                @Override
                public void onResponse(Call<RouteDto> call, Response<RouteDto> response) {

                    RouteDto routeDto = response.body();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //お気に入りの登録処理を行う
                            deleteFavorite();
                        }
                    };
                    //アクセストークンのチェック
                    if(!checkAccessToken(routeDto.resultCode, listener)) {
                        return ;
                    }

                    //正常終了の場合
                    if (BestTravelConstant.RESULT_CODE_OK == routeDto.resultCode) {

                        //現在登録されているルートの情報を登録する
                        RouteDao routeDao = new RouteDao();
                        Route route = routeDao.getRoute(routeDto.routeId, myAccount.accountId);

                        if (route != null && route.routeKbnCd != BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE) {
                            //ルート情報の削除
                            //参加者情報の削除
                            RouteParticipantDao routeParticipantDao = new RouteParticipantDao();
                            routeParticipantDao.deleteRouteParticipant(routeDto.routeId, myAccount.accountId);

                            //目的地情報の削除
                            DestinationDao destinationDao = new DestinationDao();
                            destinationDao.deleteDestinationByRouteId(routeDto.routeId, myAccount.accountId);

                            //アルバム情報の削除
                            AlbumDataDao albumDataDao = new AlbumDataDao();
                            albumDataDao.deleteAlbumDataByRouteId(routeDto.routeId, myAccount.accountId);

                            //ルート情報の削除
                            routeDao.deleteRoute(routeDto.routeId, myAccount.accountId);
                        }

                        return;
                    } else {
                        //メッセージを表示
                        dispToast(getResources().getString(R.string.error_insert, "お気に入り"));
                        //トグルボタンの表示を戻す
                        if (favoritePushButton != null) {
                            favoritePushButton.setClickable(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteDto> call, Throwable t) {
                    dispToast(getResources().getString(R.string.error_transceiver));

                    //トグルボタンの表示を戻す
                    if (favoritePushButton != null) {
                        favoritePushButton.setClickable(false);
                    }
                }
            });
        }
    }
}
