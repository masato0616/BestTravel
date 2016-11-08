package jp.co.accel_road.besttravel.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.co.accel_road.besttravel.common.BestTravelConstant;

/**
 * 時間選択のダイアログを表示するクラス
 *
 * Created by masato on 2015/12/30.
 */
public class TimePickerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String time = (String)getArguments().getSerializable(BestTravelConstant.PARAMETER_KEY_TIME);

        int hour = 0;
        int minute = 0;

        if (time != null && !"".equals(time)) {
            String[] timeSplit = time.split(":");
            hour = Integer.parseInt(timeSplit[0]);
            minute = Integer.parseInt(timeSplit[1]);
        }

        //呼び出し元のフラグメントを取得する
        TimePickerDialog.OnTimeSetListener activity = (TimePickerDialog.OnTimeSetListener)getActivity();
        return new TimePickerDialog(getActivity(), activity, hour, minute, true);
    }
}
