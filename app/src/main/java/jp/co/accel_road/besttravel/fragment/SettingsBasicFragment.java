package jp.co.accel_road.besttravel.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import jp.co.accel_road.besttravel.R;


/**
 * 設定画面の基本設定タブの処理を行うフラグメントクラス
 *
 * Created by masato on 2015/11/22.
 */
public class SettingsBasicFragment extends PreferenceFragment {

    /**
     * 設定画面の基本設定タブの初期表示時の処理を行う。
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_basic_preference);
    }
}
