package jp.co.accel_road.besttravel;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import jp.co.accel_road.besttravel.fragment.SettingsBillingFragment;
import jp.co.accel_road.besttravel.fragment.SettingsBasicFragment;
import jp.co.accel_road.besttravel.model.AccountDto;
import jp.co.accel_road.besttravel.view.SlidingTabLayout;

/**
 * 設定画面のアクティビティ
 */
public class SettingsActivity extends BaseActivity {

    //マイステータスのアカウント情報
    private AccountDto accountModel;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /** アカウント設定のフラグメント */
    private SettingsBasicFragment settingsBasicFragment;
    /** アイテム課金タブのフラグメント */
    private SettingsBillingFragment settingsBillingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //ツールバーの設定
        setToolbar();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // タブの表示内容をセットする
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sdtTitleTab);
        slidingTabLayout.setViewPager(mViewPager);
    }

    /**
     * キー押下時の処理を行う
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){

        //戻るボタン押下時処理
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * タブ表示用のアダプタークラス
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Bundle args = new Bundle();
            switch(position) {
                case 0:
                    //アカウント設定タブを表示する
                    if (settingsBasicFragment == null) {
                        settingsBasicFragment = new SettingsBasicFragment();
                    }
                    return settingsBasicFragment;
                case 1:
                    //アイテム課金タブを表示する
                    if (settingsBillingFragment == null) {
                        settingsBillingFragment = new SettingsBillingFragment();
                    }
                    return settingsBillingFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "設定項目";
                case 1:
                    return "課金項目";
            }
            return null;
        }
    }
}
