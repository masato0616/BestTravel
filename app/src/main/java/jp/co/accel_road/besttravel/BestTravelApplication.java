package jp.co.accel_road.besttravel;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by masato on 2015/11/10.
 */
public class BestTravelApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
