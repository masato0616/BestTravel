package jp.co.accel_road.besttravel.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import jp.co.accel_road.besttravel.BaseActivity;
import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.listener.AccessTokenListener;
import jp.co.accel_road.besttravel.listener.RouteListener;
import jp.co.accel_road.besttravel.model.ChatMessageDto;
import jp.co.accel_road.besttravel.service.CharMessageService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ルートチャットを表示するタブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class GroupChatFragment extends Fragment {

    /** ルートが変更された際に呼び出されるリスナー */
    private RouteListener routeListener;

    private ChatMessageListAdapter chatMessageListAdapter;

    /** メッセージ入力欄 */
    private EditText txtSendMessage;
    /** 検索ボタン */
    private Button btnSend;

    /**
     * ルートチャットの初期表示時の処理を行う。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        //グループChatのリストビュー
        ListView lisGroupChat = (ListView)rootView.findViewById(R.id.lisGroupChat);
        chatMessageListAdapter = new ChatMessageListAdapter();
        lisGroupChat.setAdapter(chatMessageListAdapter);

        //メッセージ入力欄
        txtSendMessage = (EditText)rootView.findViewById(R.id.txtSendMessage);
        //送信ボタン
        btnSend = (Button)rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //チャットメッセージを送信する
                insertChatMessage();
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
        chatMessageListAdapter.notifyDataSetChanged();
    }

    /**
     * チャットメッセージを登録する
     */
    private void insertChatMessage() {

        //送信する文字列を取得
        String sendMessage = txtSendMessage.getText().toString();

        //送信する文字列が存在しない場合は処理を終了する。
        if (sendMessage == null || "".equals(sendMessage)) {
            return;
        }

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        //ルートID
        chatMessageDto.routeId = routeListener.getRouteId();
        //メッセージ
        chatMessageDto.message = sendMessage;

        BaseActivity baseActivity = (BaseActivity)getActivity();

        //チャットメッセージの送信
        CharMessageService charMessageService = baseActivity.getRetrofit().create(CharMessageService.class);
        Call<ChatMessageDto> reps = charMessageService.insertChatMessage(chatMessageDto, baseActivity.myAccount.accessToken);
        reps.enqueue(new Callback<ChatMessageDto>() {
            @Override
            public void onResponse(Call<ChatMessageDto> call, Response<ChatMessageDto> response) {

                ChatMessageDto chatMessageDto = response.body();
                BaseActivity baseActivity = (BaseActivity)getActivity();

                //アクセストークンでエラーとなった場合の再実行処理を定義
                AccessTokenListener listener = new AccessTokenListener() {
                    @Override
                    public void onAgainSend() {
                        //チャットメッセージを再登録する
                        insertChatMessage();
                    }
                };
                //アクセストークンのチェック
                if(!baseActivity.checkAccessToken(chatMessageDto.resultCode, listener)) {
                    return ;
                }

                //メッセージ入力欄をクリア
                txtSendMessage.setText("");
                //最新の情報を取得する
                routeListener.refreshLatestRoute();
            }

            @Override
            public void onFailure(Call<ChatMessageDto> call, Throwable t) {

            }
        });
    }

    /**
     * アルバムデータ一覧のアダプター
     */
    public class ChatMessageListAdapter extends BaseAdapter {

        LayoutInflater layoutInflater = null;

        public ChatMessageListAdapter() {
            this.layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            return routeListener.getChatMessageDtoList().size();
        }

        @Override
        public ChatMessageDto getItem(int position) {

            return routeListener.getChatMessageDtoList().get(position);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //チャットメッセージ情報の取得
            ChatMessageDto chatMessageDto = routeListener.getChatMessageDtoList().get(position);

            //ログイン情報を取得
            BaseActivity baseActivity = (BaseActivity)getActivity();

            //自分のメッセージかどうか判断する
            if (chatMessageDto.sendUserId.equals(baseActivity.myAccount.userId)) {
                convertView = layoutInflater.inflate(R.layout.view_my_message_item, parent, false);

            } else {
                convertView = layoutInflater.inflate(R.layout.view_message_item, parent, false);
                //ユーザー名
                TextView lblUserName = (TextView)convertView.findViewById(R.id.lblUserName);
                lblUserName.setText(chatMessageDto.sendUserName);
            }

            //アイコン画像
            ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
            Picasso.with(getActivity()).load(chatMessageDto.sendUserIconUrl).fit().centerInside().into(imgIcon);

            //送受信日付
            TextView lblTransceiverDate = (TextView)convertView.findViewById(R.id.lblTransceiverDate);
            lblTransceiverDate.setText(BestTravelUtil.convertDateToStringDispDateTime(chatMessageDto.transceiverDate));

            //メッセージ
            TextView lblMessage = (TextView)convertView.findViewById(R.id.lblMessage);
            lblMessage.setText(chatMessageDto.message);

            return convertView;
        }
    }
}
