package jp.co.accel_road.besttravel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.BaseActivity;
import jp.co.accel_road.besttravel.DestinationEditActivity;
import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.listener.RouteListener;
import jp.co.accel_road.besttravel.model.DestinationDto;

/**
 * 目的地のリストを表示するタブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class DestinationListFragment extends Fragment {

    private DestinationListAdapter destinationListAdapter;
    /** 目的地のリストが変更された際に呼び出されるリスナー */
    private RouteListener routeListener;

    /** 画面に表示する表示日付 */
    private TextView lblDispDate;
    /** 日付減少ボタン */
    private Button btnBeforeDispDate;
    /** 日付増加ボタン */
    private Button btnAfterDispDate;

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
        View rootView = inflater.inflate(R.layout.fragment_destination_list, container, false);

        //目的地のリストを取得
        ListView lisDestinationList = (ListView)rootView.findViewById(R.id.lisDestinationList);
        //マイルートのアダプターを設定
        destinationListAdapter = new DestinationListAdapter(getActivity());
        lisDestinationList.setAdapter(destinationListAdapter);

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

        //表示日付の値を設定
        lblDispDate.setText(BestTravelUtil.convertDateToString(routeListener.getDispDate()));
        //ボタンの表示制御を行う
        dispDateButton();

        destinationListAdapter.notifyDataSetChanged();
    }


    /**
     * 目的地一覧のアダプター
     */
    public class DestinationListAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater = null;
        //日付変換用オブジェクト
        SimpleDateFormat sdf;

        public DestinationListAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            sdf = new SimpleDateFormat("yyyy年M月d日");
        }

        @Override
        public int getCount() {

            //目的地のリストマップを取得
            List<DestinationDto> destinationDtoList = routeListener.getDispDestinationDtoList();

            if (destinationDtoList.isEmpty()) {
                return 0;
            }

            return destinationDtoList.size() * 2 - 1;
        }

        @Override
        public DestinationDto getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.view_destination_item, parent, false);
            }

            //奇数の場合（Intervalを表示する）
            if (position%2 == 1) {
                convertView = layoutInflater.inflate(R.layout.view_destination_interval_item, parent, false);
                //インターバル時間を表示する
                TextView lblIntervalTime = (TextView)convertView.findViewById(R.id.lblIntervalTime);

            } else {
                //偶数の場合（目的地を表示する）
                convertView = layoutInflater.inflate(R.layout.view_destination_item, parent, false);

                //目的地情報を取得
                DestinationDto destinationDto = routeListener.getDispDestinationDtoList().get(position / 2);

                //開始・終了時間
                Date dispDate = routeListener.getDispDate();
                TextView lblDestinationTime = (TextView)convertView.findViewById(R.id.lblDestinationTime);
                //開始日付と等しいなら時間のみを表示する
                String startDateTime;
                if (dispDate.compareTo(destinationDto.startDate) == 0) {
                    startDateTime = destinationDto.startTime;
                } else {
                    startDateTime = BestTravelUtil.convertDateToStringMonthDate(destinationDto.startDate) + destinationDto.startTime;
                }

                String endDateTime;
                if (dispDate.compareTo(destinationDto.endDate) == 0) {
                    endDateTime = destinationDto.endTime;
                } else {
                    endDateTime = BestTravelUtil.convertDateToStringMonthDate(destinationDto.endDate) + destinationDto.endTime;
                }

                lblDestinationTime.setText(startDateTime + "～" + endDateTime);

                //目的地名
                TextView lblDestinationName = (TextView)convertView.findViewById(R.id.lblDestinationName);
                lblDestinationName.setText(destinationDto.destinationName);

                //住所
                TextView lblAddress = (TextView)convertView.findViewById(R.id.lblAddress);
                lblAddress.setText(destinationDto.address);

                //メモ
                TextView lblMemo = (TextView)convertView.findViewById(R.id.lblMemo);
                lblMemo.setText(destinationDto.memo);

                //リスト選択
                convertView.setTag(destinationDto.destinationId);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long destinationId = (long)v.getTag();
                        //目的地IDに紐づく目的地を取得
                        DestinationDto destinationDto = routeListener.getDispDestinationDto(destinationId);
                        dispDestinationEdit(destinationDto);
                    }
                });
            }
            return convertView;
        }
    }

    /**
     * 目的地編集アクティビティを表示する。
     *
     * @param destinationDto 目的地DTOを設定
     */
    private void dispDestinationEdit(DestinationDto destinationDto) {

        //目的地編集画面を表示
        Intent intent = new Intent(getContext(), DestinationEditActivity.class);
        //目的地DTOを設定
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
