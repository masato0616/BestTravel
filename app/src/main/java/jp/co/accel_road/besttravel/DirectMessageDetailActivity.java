package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.DirectMessageAccountDao;
import jp.co.accel_road.besttravel.dao.DirectMessageDao;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.DirectMessage;
import jp.co.accel_road.besttravel.entity.DirectMessageAccount;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.model.DirectMessageDto;
import jp.co.accel_road.besttravel.model.DirectMessageListDto;
import jp.co.accel_road.besttravel.service.DirectMessageService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ダイレクトメッセージの詳細を表示するアクティビティ
 */
public class DirectMessageDetailActivity extends BaseActivity {

    /** 表示対象のアカウントDTO */
    private AccountDto dispAccountDto;
    /** ダイレクトメッセージのリスト */
    private List<DirectMessageDto> directMessageDtoList;

    /** ダイレクトメッセージのリストのアダプター */
    private DirectMessageDtoListAdapter directMessageDtoListAdapter;

    /** メッセージ入力欄 */
    private EditText txtSendMessage;
    /** 検索ボタン */
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message_detail);

        //ツールバーの設定
        setToolbar();

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        dispAccountDto = (AccountDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO);

        //ダイレクトメッセージのリストをローカルDBより取得
        setDirectMessageDtoList();


        //ダイレクトメッセージ一覧のアダプター設定
        ListView lisDirectMessageList = (ListView)findViewById(R.id.lisDirectMessageList);
        directMessageDtoListAdapter = new DirectMessageDtoListAdapter();
        lisDirectMessageList.setAdapter(directMessageDtoListAdapter);

        //メッセージ入力欄
        txtSendMessage = (EditText)findViewById(R.id.txtSendMessage);
        //送信ボタン
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ダイレクトメッセージを送信する
                sendDirectMessage();
            }
        });
    }

    /**
     * ダイレクトメッセージを送信する
     */
    private void sendDirectMessage() {

        //送信する文字列を取得
        String sendMessage = txtSendMessage.getText().toString();

        //送信する文字列が存在しない場合は処理を終了する。
        if (sendMessage == null || "".equals(sendMessage)) {
            return;
        }

        DirectMessageDto directMessageDto = new DirectMessageDto();
        // アカウントID
        directMessageDto.accountId = dispAccountDto.accountId;
        // 送信・受信区分
        directMessageDto.sendReceiveKbn = BestTravelConstant.SEND_RECEIVE_KBN_SEND;
        // メッセージ
        directMessageDto.message = sendMessage;


        //ダイレクトメッセージを送信する
        DirectMessageService directMessageService = getRetrofit().create(DirectMessageService.class);
        Call<DirectMessageDto> reps = directMessageService.insertDirectMessage(directMessageDto, myAccount.accessToken);
        reps.enqueue(new Callback<DirectMessageDto>() {
            @Override
            public void onResponse(Call<DirectMessageDto> call, Response<DirectMessageDto> response) {

                //戻り値のダイレクトメッセージ
                DirectMessageDto directMessageDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //ダイレクトメッセージを再取得する
                        sendDirectMessage();
                    }
                };
                //アクセストークンのチェック
                if(!checkAccessToken(directMessageDto.resultCode, listener)) {
                    return ;
                }

                //ダイレクトメッセージアカウントに情報があるか確認する
                DirectMessageAccountDao directMessageAccountDao = new DirectMessageAccountDao();
                DirectMessageAccount directMessageAccount = directMessageAccountDao.getDirectMessageAccount(directMessageDto.accountId, myAccount.accountId);
                if (directMessageAccount == null) {
                    //存在しない場合は、データを登録する
                    directMessageAccount = new DirectMessageAccount();
                    // マイアカウントID
                    directMessageAccount.myAccountId = myAccount.accountId;
                    // アカウントID
                    directMessageAccount.accountId = dispAccountDto.accountId;
                    // ユーザーID
                    directMessageAccount.userId = dispAccountDto.userId;
                    // ユーザー名
                    directMessageAccount.userName = dispAccountDto.userName;
                    // ユーザーアイコンURL
                    directMessageAccount.thumbnailUserIconUrl = dispAccountDto.thumbnailIconUrl;
                    // 最後に送受信したメッセージ
                    directMessageAccount.lastMessage = directMessageDto.message;
                    // 最終送受信日時
                    directMessageAccount.lastTransceiverDate = directMessageDto.transceiverDate;
                    // 更新日時
                    directMessageAccount.updateDate = new Date();

                    directMessageAccountDao.insertDirectMessageAccount(directMessageAccount);
                } else {
                    // マイアカウントID
                    directMessageAccount.myAccountId = myAccount.accountId;
                    // アカウントID
                    directMessageAccount.accountId = dispAccountDto.accountId;
                    // ユーザーID
                    directMessageAccount.userId = dispAccountDto.userId;
                    // ユーザー名
                    directMessageAccount.userName = dispAccountDto.userName;
                    // ユーザーアイコンURL
                    directMessageAccount.thumbnailUserIconUrl = dispAccountDto.thumbnailIconUrl;
                    // 最後に送受信したメッセージ
                    directMessageAccount.lastMessage = directMessageDto.message;
                    // 最終送受信日時
                    directMessageAccount.lastTransceiverDate = directMessageDto.transceiverDate;
                    // 更新日時
                    directMessageAccount.updateDate = new Date();

                    directMessageAccountDao.updateDirectMessageAccount(directMessageAccount);
                }
                //送信したダイレクトメッセージをDBに登録
                DirectMessageDao directMessageDao = new DirectMessageDao();
                directMessageDao.insertDirectMessage(directMessageDto.getDirectMessage(myAccount.accountId));

                //メッセージ入力欄をクリア
                txtSendMessage.setText("");

                //最新の情報を取得する
                getDirectMessage();
            }

            @Override
            public void onFailure(Call<DirectMessageDto> call, Throwable t) {

            }
        });
    }


    /**
     * サーバーよりダイレクトメッセージの情報を取得する
     */
    private void getDirectMessage() {
        //ダイレクトメールの取得が一度もない場合
        if (myAccount.directMessageGetLastDate == null) {

            //ダイレクトメッセージの一覧を取得する
            DirectMessageService directMessageService = getRetrofit().create(DirectMessageService.class);
            Call<DirectMessageListDto> reps = directMessageService.getDirectMessage(myAccount.accessToken);
            reps.enqueue(new Callback<DirectMessageListDto>() {
                @Override
                public void onResponse(Call<DirectMessageListDto> call, Response<DirectMessageListDto> response) {
                    DirectMessageListDto directMessageListDto = response.body();

                    //アクセストークンでエラーとなった場合の再実行処理を定義
                    AccessTokenListener listener = new AccessTokenListener() {
                        @Override
                        public void onAgainSend() {
                            //ダイレクトメッセージの情報を再取得する
                            getDirectMessage();
                        }
                    };
                    //アクセストークンのチェック
                    if(!checkAccessToken(directMessageListDto.resultCode, listener)) {
                        return ;
                    }

                    //ダイレクトメッセージの保存
                    saveDirectMessageDtoList(directMessageListDto);
                    //ダイレクトメッセージのリストをローカルDBより取得しセットする
                    setDirectMessageDtoList();

                    //ダイレクトメッセージ一覧の更新
                    directMessageDtoListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<DirectMessageListDto> call, Throwable t) {

                }
            });
        } else {
            //ダイレクトメッセージの一覧を取得する
            DirectMessageService directMessageService = getRetrofit().create(DirectMessageService.class);
            Call<DirectMessageListDto> reps = directMessageService.getDirectMessageDifference(BestTravelUtil.convertJsonDateToString(myAccount.directMessageGetLastDate), myAccount.accessToken);
            reps.enqueue(new Callback<DirectMessageListDto>() {
                @Override
                public void onResponse(Call<DirectMessageListDto> call, Response<DirectMessageListDto> response) {
                    DirectMessageListDto directMessageListDto = response.body();

                    //ダイレクトメッセージの保存
                    saveDirectMessageDtoList(directMessageListDto);
                    //ダイレクトメッセージのリストをローカルDBより取得しセットする
                    setDirectMessageDtoList();
                    //ダイレクトメッセージ一覧の更新
                    directMessageDtoListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<DirectMessageListDto> call, Throwable t) {

                }
            });
        }
    }


    /**
     * サーバーより取得したダイレクトメッセージのリストをローカルDBに保存する
     *
     * @param directMessageListDto サーバーより取得したダイレクトメッセージのリスト
     */
    private void saveDirectMessageDtoList(DirectMessageListDto directMessageListDto) {

        //前回のダイレクトメッセージDTO
        DirectMessageDto beforeDirectMessageDto = null;
        DirectMessageAccountDao directMessageAccountDao = new DirectMessageAccountDao();
        DirectMessageDao directMessageDao = new DirectMessageDao();

        List<DirectMessageDto> directMessageDtoList = directMessageListDto.directMessageDtoList;
        for (DirectMessageDto directMessageDto: directMessageDtoList) {

            //前回とアカウントが異なる場合
            if (beforeDirectMessageDto != null && beforeDirectMessageDto.accountId.longValue() != directMessageDto.accountId.longValue()) {
                saveDirectMessageAccount(beforeDirectMessageDto, directMessageAccountDao);
            }

            //ダイレクトメッセージの登録
            DirectMessage directMessage = directMessageDao.getDirectMessage(directMessageDto.directMessageId);
            if (directMessage == null) {
                directMessageDao.insertDirectMessage(directMessageDto.getDirectMessage(myAccount.accountId));
            } else {
                DirectMessage resultDirectMessage = directMessageDto.getDirectMessage(myAccount.accountId);
                //メッセージが登録されている場合
                if (resultDirectMessage.message != null && !"".equals(resultDirectMessage.message)) {
                    directMessageDao.updateDirectMessage(directMessageDto.getDirectMessage(myAccount.accountId));
                }
            }
            beforeDirectMessageDto = directMessageDto;
        }
        //最終アカウントの登録
        if (beforeDirectMessageDto != null) {
            saveDirectMessageAccount(beforeDirectMessageDto, directMessageAccountDao);
        }

        //マイアカウントの更新
        myAccount.directMessageGetLastDate = directMessageListDto.directMessageLastDate;
        MyAccountDao myAccountDao = new MyAccountDao();
        myAccountDao.updateMyAccount(myAccount);
    }

    /**
     * ダイレクトメッセージアカウントテーブルへの保存を行う
     *
     * @param directMessageDto 保存するダイレクトメッセージアカウント
     * @param directMessageAccountDao ダイレクトメッセージアカウントDAO
     */
    private void saveDirectMessageAccount (DirectMessageDto directMessageDto, DirectMessageAccountDao directMessageAccountDao) {

        DirectMessageAccount directMessageAccount = directMessageAccountDao.getDirectMessageAccount(directMessageDto.accountId, myAccount.accountId);
        if (directMessageAccount == null) {
            //ダイレクトメッセージアカウントテーブルの登録
            directMessageAccountDao.insertDirectMessageAccount(directMessageDto.getDirectMessageAccount(myAccount.accountId));
        } else {
            //ダイレクトメッセージアカウントテーブルの更新
            DirectMessageAccount directMessageAccountDb = directMessageDto.getDirectMessageAccount(myAccount.accountId);

            directMessageAccountDb.directMessageAccountId = directMessageAccount.directMessageAccountId;
            //アカウントのみの更新の場合
            if (directMessageDto.message == null || "".equals(directMessageDto.message)) {
                // 最後に送受信したメッセージ
                directMessageAccountDb.lastMessage = directMessageAccount.lastMessage;
                // 最終送受信日時
                directMessageAccountDb.lastTransceiverDate = directMessageAccount.lastTransceiverDate;
            }

            directMessageAccountDao.updateDirectMessageAccount(directMessageAccountDb);
        }
    }

    /**
     * ローカルDBよりダイレクトメッセージアカウントのリストを取得して表示対象のリストに設定する
     */
    private void setDirectMessageDtoList() {

        if (directMessageDtoList == null) {
            directMessageDtoList = new ArrayList<>();
        } else {
            directMessageDtoList.clear();
        }

        //ダイレクトメッセージのリストをローカルDBより取得
        DirectMessageDao directMessageDao = new DirectMessageDao();
        List<DirectMessage> directMessageList = directMessageDao.getDirectMessageListByAccountId(dispAccountDto.accountId, myAccount.accountId);
        for (DirectMessage directMessage: directMessageList) {
            DirectMessageDto directMessageDto = new DirectMessageDto();
            directMessageDto.setDirectMessage(directMessage);
            directMessageDtoList.add(directMessageDto);
        }
    }

    /**
     * ダイレクトメッセージ一覧のアダプター
     */
    public class DirectMessageDtoListAdapter extends BaseAdapter {

        LayoutInflater layoutInflater = null;

        public DirectMessageDtoListAdapter() {
            this.layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            return directMessageDtoList.size();
        }

        @Override
        public DirectMessageDto getItem(int position) {

            //取得する値が範囲外の場合はnullを返す
            if (directMessageDtoList.size() >= position) {
                return null;
            }
            return directMessageDtoList.get(position);
        }

        @Override
        public long getItemId(int position) {

            //取得する値が範囲外の場合はnullを返す
            if (directMessageDtoList.size() >= position) {
                return -1;
            }
            return directMessageDtoList.get(position).directMessageId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ダイレクトメッセージを取得
            DirectMessageDto directMessageDto = directMessageDtoList.get(position);

            //自分のメッセージかどうか判断する
            if (directMessageDto.sendReceiveKbn.intValue() == BestTravelConstant.SEND_RECEIVE_KBN_SEND) {
                convertView = layoutInflater.inflate(R.layout.view_my_message_item, parent, false);

            } else {
                convertView = layoutInflater.inflate(R.layout.view_message_item, parent, false);
                //ユーザー名
                TextView lblUserName = (TextView)convertView.findViewById(R.id.lblUserName);
                lblUserName.setText(directMessageDto.userName);
            }

            //アイコン画像
            ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
            Picasso.with(getApplicationContext()).load(directMessageDto.userIconUrl).fit().centerInside().into(imgIcon);

            //送受信日付
            TextView lblTransceiverDate = (TextView)convertView.findViewById(R.id.lblTransceiverDate);
            lblTransceiverDate.setText(BestTravelUtil.convertDateToStringDispDateTime(directMessageDto.transceiverDate));

            //メッセージ
            TextView lblMessage = (TextView)convertView.findViewById(R.id.lblMessage);
            lblMessage.setText(directMessageDto.message);

            return convertView;
        }
    }
}
