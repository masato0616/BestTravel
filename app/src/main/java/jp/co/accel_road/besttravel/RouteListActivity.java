package jp.co.accel_road.besttravel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.fragment.RouteListFragment;
import jp.co.accel_road.besttravel.view.SlidingTabLayout;

/**
 * マイルート一覧のアクティビティ
 */
public class RouteListActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /** マイルートタブのフラグメント */
    private RouteListFragment myRouteListFragment;
    /** お気に入りタブのフラグメント */
    private RouteListFragment favoriteRouteListFragment;

    /**
     * 初期表示時の処理を行う。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        //ツールバーの設定
        setToolbar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sdtTitleTab);
        slidingTabLayout.setViewPager(mViewPager);
    }

    /**
     * マイルートのフラグメントを取得する
     * @return
     */
    public RouteListFragment getMyRouteListFragment() {
        return myRouteListFragment;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
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
                    //マイルートタブを表示する
                    if (myRouteListFragment == null) {
                        myRouteListFragment = new RouteListFragment();
                    }
                    args.putInt(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD, BestTravelConstant.ROUTE_KBN_CD_MY_ROUTE);
                    myRouteListFragment.setArguments(args);
                    return myRouteListFragment;
                case 1:
                    //お気に入りタブを表示する
                    if (favoriteRouteListFragment == null) {
                        favoriteRouteListFragment = new RouteListFragment();
                    }
                    args.putInt(BestTravelConstant.PARAMETER_KEY_ROUTE_KBN_CD, BestTravelConstant.ROUTE_KBN_CD_FAVORITE_ROUTE);
                    favoriteRouteListFragment.setArguments(args);
                    return favoriteRouteListFragment;
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
                    return "マイルート";
                case 1:
                    return "お気に入り";
            }
            return null;
        }
    }
}
