package jp.co.accel_road.besttravel;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * データーベース定義を持つクラス
 *
 * Created by masato on 2015/11/12.
 */
@Database(name = BestTravelDatabase.NAME, version = BestTravelDatabase.VERSION, generatedClassSeparator = "_")
public class BestTravelDatabase {
    public static final String NAME = "BestTravelDb";

    public static final int VERSION = 1;
}
