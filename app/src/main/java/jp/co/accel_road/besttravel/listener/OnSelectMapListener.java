package jp.co.accel_road.besttravel.listener;

import com.google.android.gms.maps.model.LatLng;

/**
 * マップ選択時の処理を行うリスナー
 *
 * Created by masato on 2015/12/30.
 */
public interface OnSelectMapListener {
    public void onSelectMap(LatLng latLng);
}
