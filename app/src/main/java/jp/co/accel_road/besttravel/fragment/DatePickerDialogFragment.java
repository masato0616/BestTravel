package jp.co.accel_road.besttravel.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;

/**
 * 日付選択のダイアログを表示するクラス
 *
 * Created by masato on 2015/12/30.
 */
public class DatePickerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date)getArguments().getSerializable(BestTravelConstant.PARAMETER_KEY_DATE);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //呼び出し元のフラグメントを取得する
        DatePickerDialog.OnDateSetListener activity = (DatePickerDialog.OnDateSetListener)getActivity();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyDialogTheme, activity, year, month, day);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        return datePickerDialog;
    }


}
