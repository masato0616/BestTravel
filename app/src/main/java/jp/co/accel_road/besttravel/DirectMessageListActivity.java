package jp.co.accel_road.besttravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.DirectMessageAccountDao;
import jp.co.accel_road.besttravel.dao.DirectMessageDao;
import jp.co.accel_road.besttravel.dao.MyAccountDao;
import jp.co.accel_road.besttravel.entity.DirectMessage;
import jp.co.accel_road.besttravel.entity.DirectMessageAccount;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.model.DirectMessageDto;
import jp.co.accel_road.besttravel.model.DirectMessageListDto;
import jp.co.accel_road.besttravel.service.DirectMessageService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ダイレクトメッセージの一覧を表示するアクティビティ
 */
public class DirectMessageListActivity extends BaseActivity {

    /** ダイレクトメッセージ追加ボタン */
    private FloatingActionButton btnDirectMessageAdd;

    /** ダイレクトメッセージのリスト */
    private List<DirectMessageDto> directMessageDtoList;

    /** ダイレクトメッセージリストのアダプター */
    private DirectMessageDtoListAdapter directMessageDtoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message_list);

        //ツールバーの設定
        setToolbar();

        //ダイレクトメッセージ追加ボタン
        btnDirectMessageAdd = (FloatingActionButton)findViewById(R.id.btnDirectMessageAdd);
        btnDirectMessageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ダイレクトメッセージ用の友達選択を表示
                Intent intent = new Intent(getApplicationContext(), DirectMessageFriendSelectActivity.class);
                startActivity(intent);
            }
        });

        //ダイレクトメールの取得が一度もない場合
        if (myAccount.directMessageGetLastDate == null) {

            //ダイレクトメッセージを取得する
            getDirectMessage();
        } else {
            //ダイレクトメッセージのリストを前回受信後からの差分のみ取得する
            getDirectMessageDifference();
        }

        if (directMessageDtoList == null) {
            directMessageDtoList = new ArrayList<>();
        }

        //ダイレクトメッセージ一覧のアダプター設定
        ListView lisDirectMessageList = (ListView)findViewById(R.id.lisDirectMessageList);
        directMessageDtoListAdapter = new DirectMessageDtoListAdapter();
        lisDirectMessageList.setAdapter(directMessageDtoListAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();

        //ダイレクトメッセージのリストをローカルDBより取得しセットする
        setDirectMessageDtoList();
    }

    /**
     * ダイレクトメッセージのリストをサーバーより取得する
     */
    private void getDirectMessage() {

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

                        //ダイレクトメッセージのリストを再取得する
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
    }

    /**
     * ダイレクトメッセージのリストを前回受信後からの差分のみ取得する
     */
    private void getDirectMessageDifference() {

        //ダイレクトメッセージの一覧を取得する
        DirectMessageService directMessageService = getRetrofit().create(DirectMessageService.class);
        Call<DirectMessageListDto> reps = directMessageService.getDirectMessageDifference(BestTravelUtil.convertJsonDateToString(myAccount.directMessageGetLastDate), myAccount.accessToken);
        reps.enqueue(new Callback<DirectMessageListDto>() {
            @Override
            public void onResponse(Call<DirectMessageListDto> call, Response<DirectMessageListDto> response) {
                DirectMessageListDto directMessageListDto = response.body();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {

                        //ダイレクトメッセージのリストを再取得する
                        getDirectMessageDifference();
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
                directMessageDao.updateDirectMessage(directMessageDto.getDirectMessage(myAccount.accountId));
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
            DirectMessageAccount beforeDirectMessageAccount = directMessageDto.getDirectMessageAccount(myAccount.accountId);
            beforeDirectMessageAccount.directMessageAccountId = directMessageAccount.directMessageAccountId;
            directMessageAccountDao.updateDirectMessageAccount(beforeDirectMessageAccount);
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

        //ダイレクトメッセージアカウントのリストを取得する
        DirectMessageAccountDao directMessageAccountDao = new DirectMessageAccountDao();
        List<DirectMessageAccount> directMessageAccountList = directMessageAccountDao.getDirectMessageAccountList(myAccount.accountId);
        for (DirectMessageAccount directMessageAccount: directMessageAccountList) {
            DirectMessageDto directMessageDto = new DirectMessageDto();
            directMessageDto.setDirectMessageAccount(directMessageAccount);
            directMessageDtoList.add(directMessageDto);
        }
    }

    /**
     * ダイレクトメッセージアカウントのリストアダプタークラス
     */
    public class DirectMessageDtoListAdapter extends BaseAdapter {

        LayoutInflater layoutInflater = null;

        public DirectMessageDtoListAdapter() {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return directMessageDtoList.size();
        }

        @Override
        public Object getItem(int position) {
            //取得する値が範囲外の場合はnullを返す
            if (directMessageDtoList.size() >= position) {
                return null;
            }
            return directMessageDtoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            //取得する値が範囲外の場合は-1を返す
            if (directMessageDtoList.size() >= position) {
                return -1;
            }
            return directMessageDtoList.get(position).directMessageId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.view_direct_message_account_item, parent, false);
            }

            DirectMessageDto directMessageDto = directMessageDtoList.get(position);

            //アイコン画像
            ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
            Picasso.with(getApplicationContext()).load(directMessageDto.userIconUrl).fit().centerInside().into(imgIcon);

            //ユーザー名
            TextView lblUserName = (TextView)convertView.findViewById(R.id.lblUserName);
            lblUserName.setText(directMessageDto.userName);
            //最終送受信日時
            TextView lblLastTransceiverDate = (TextView)convertView.findViewById(R.id.lblLastTransceiverDate);
            lblLastTransceiverDate.setText(BestTravelUtil.convertDateToStringDispDateTime(directMessageDto.transceiverDate));
            //最終メッセージ
            TextView lblLastMessage = (TextView)convertView.findViewById(R.id.lblLastMessage);
            lblLastMessage.setText(directMessageDto.message);

            //リストクリック時のパラメーターを設定
            convertView.setTag(directMessageDto);
            //リストクリック時処理
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //アカウントDTO
                    DirectMessageDto directMessageDto = (DirectMessageDto)view.getTag();

                    //ダイレクトメッセージ詳細画面を表示
                    Intent intent = new Intent(getApplicationContext(), DirectMessageDetailActivity.class);
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ACCOUNT_DTO, directMessageDto.getAccountDto());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
