package jp.co.accel_road.besttravel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 要素全てを表示するグリッドビュー
 *
 * Created by masato on 2016/07/09.
 */
public class AllDispGridView extends GridView {

    public AllDispGridView(Context context) {
        super(context);
    }

    public AllDispGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AllDispGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
