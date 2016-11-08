package jp.co.accel_road.besttravel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;

/**
 * 情報表示ダイアログを表示するクラス
 *
 * Created by masato on 2015/12/30.
 */
public class InfomationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //ダイアログに表示する文字列を取得
        String dispMessage = getArguments().getString(BestTravelConstant.PARAMETER_KEY_DISP_MESSAGE);

        //ダイアログのレイアウトを取得
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.fragment_infomation_dialog, null);

        //メッセージ
        TextView lblMessage = (TextView)convertView.findViewById(R.id.lblMessage);
        lblMessage.setText(dispMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(convertView);
        builder.setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }


}
