package jp.co.accel_road.besttravel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import jp.co.accel_road.besttravel.R;
import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.listener.ProgressBarDialogListener;

/**
 * プログレスバーダイアログを表示するクラス
 *
 * Created by masato on 2015/12/30.
 */
public class ProgressBarDialogFragment extends DialogFragment {

    /** メッセージ */
    private TextView lblMessage;
    /** 処理中のプログレスバー */
    private ProgressBar prbProcessing;

    /** 処理がエラーとなったか判断するフラグ */
    private boolean errorFlg;

    /** プログレスバーのボタン押下時処理 */
    private ProgressBarDialogListener progressBarDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //ダイアログに表示する文字列を取得
        String dispMessage = getArguments().getString(BestTravelConstant.PARAMETER_KEY_DISP_MESSAGE);

        //ダイアログのレイアウトを取得
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.fragment_progress_bar_dialog, null);

        //メッセージ
        lblMessage = (TextView)convertView.findViewById(R.id.lblMessage);
        lblMessage.setText(dispMessage);

        //処理中のプログレスバー
        prbProcessing = (ProgressBar)convertView.findViewById(R.id.prbProcessing);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(convertView);
        builder.setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            //OKボタンを非活性
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
        }
    }

    public void setProgressBarDialogListener(ProgressBarDialogListener progressBarDialogListener) {
        this.progressBarDialogListener = progressBarDialogListener;
    }

    /**
     * 処理終了時の処理
     */
    public void finishProgress(String message, boolean errorFlgParam) {

        AlertDialog dialog = (AlertDialog) getDialog();

        //エラーとなったかどうか判断するフラグに値を設定
        this.errorFlg = errorFlgParam;

        //OKボタンを活性
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(true);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //リスナーがセットされている場合は、その処理を行う
                if (progressBarDialogListener != null) {
                    progressBarDialogListener.onFinishProgress(errorFlg);
                }
                //ダイアログを終了する
                dismiss();
            }
        });

        //メッセージを変更
        lblMessage.setText(message);

        //プログレスバーを非表示に設定
        prbProcessing.setVisibility(View.INVISIBLE);
    }
}
