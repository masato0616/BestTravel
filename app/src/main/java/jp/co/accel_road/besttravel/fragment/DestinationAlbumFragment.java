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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.accel_road.besttravel.AlbumDataDetailActivity;
import jp.co.accel_road.besttravel.BaseActivity;
import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.listener.RouteListener;
import jp.co.accel_road.besttravel.model.AlbumDataDto;
import jp.co.accel_road.besttravel.model.AlbumDto;
import jp.co.accel_road.besttravel.model.DestinationDto;
import jp.co.accel_road.besttravel.view.AllDispGridView;

/**
 * 目的地のアルバムを表示するタブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class DestinationAlbumFragment extends Fragment {

    private AlbumListAdapter albumListAdapter;
    /** ルートが変更された際に呼び出されるリスナー */
    private RouteListener routeListener;

    /** 画面に表示する表示日付 */
    private TextView lblDispDate;
    /** 日付減少ボタン */
    private Button btnBeforeDispDate;
    /** 日付増加ボタン */
    private Button btnAfterDispDate;

    /** 画面に表示するアルバムデータのリスト */
    private List<AlbumDataDto> albumDataDtoList;
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
        View rootView = inflater.inflate(R.layout.fragment_destination_album, container, false);

        //アルバムデータのリストを最新化する
        refreshAlbumDataDtoList();

        //アルバム一覧を表示するグリッドビュー
        AllDispGridView grdAlbumList = (AllDispGridView)rootView.findViewById(R.id.grdAlbumList);
        //マイルートのアダプターを設定
        albumListAdapter = new AlbumListAdapter(getActivity());
        grdAlbumList.setAdapter(albumListAdapter);

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

        //アルバムデータのリストを最新化する
        refreshAlbumDataDtoList();
        //ボタンの表示制御を行う
        dispDateButton();

        albumListAdapter.notifyDataSetChanged();
    }

    /**
     * アルバムデータのリストを最新化する
     */
    private void refreshAlbumDataDtoList() {

        //目的地のリストマップを取得
        List<DestinationDto> destinationDtoList = routeListener.getDispDestinationDtoList();

        if (albumDataDtoList == null) {
            albumDataDtoList = new ArrayList<>();
        } else {
            albumDataDtoList.clear();
        }

        for (DestinationDto destinationDto: destinationDtoList) {
            if (destinationDto.albumDataDtoList.isEmpty()) {
                continue;
            }

            albumDataDtoList.addAll(destinationDto.albumDataDtoList);
        }
    }

    /**
     * アルバムデータ一覧のアダプター
     */
    public class AlbumListAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater = null;

        public AlbumListAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            return albumDataDtoList.size();
        }

        @Override
        public AlbumDataDto getItem(int position) {

            return albumDataDtoList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return albumDataDtoList.get(position).albumDataId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.view_album_data_item, parent, false);
            }

            //アルバム情報の取得
            AlbumDataDto albumDataDto = albumDataDtoList.get(position);

            // アイコン画像
            ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
            imgIcon.setTag(position);
            if (albumDataDto.newFlg) {
                Picasso.with(getActivity()).load(albumDataDto.localFileUrl).fit().centerInside().into(imgIcon);
            } else {
                Picasso.with(getActivity()).load(albumDataDto.thumbnailFileUrl).fit().centerInside().into(imgIcon);
            }

            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //アルバムデータ詳細画面を表示
                    Intent intent = new Intent(getActivity(), AlbumDataDetailActivity.class);
                    //アルバムデータDTOのリスト
                    AlbumDto albumDto = new AlbumDto();
                    albumDto.albumDataDtoList = albumDataDtoList;
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DTO, albumDto);
                    //アルバムデータのインデックスを設定
                    intent.putExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DATA_INDEX, (int)view.getTag());

                    startActivityForResult(intent, BestTravelConstant.ACTIVITY_REQUEST_KEY_ALBUM_DATA_DETAIL);

                }
            });

            return convertView;
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
