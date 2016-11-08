package jp.co.accel_road.besttravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.common.BestTravelUtil;
import jp.co.accel_road.besttravel.dao.NoticeDao;
import jp.co.accel_road.besttravel.entity.Notice;
import jp.co.accel_road.besttravel.model.NoticeDto;

/**
 * お知らせ詳細画面のアクティビティ
 */
public class NoticeDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        //お知らせID
        long noticeId = intent.getLongExtra(BestTravelConstant.PARAMETER_KEY_NOTICE_ID, -1);

        //お知らせ情報の取得
        NoticeDao noticeDao = new NoticeDao();
        Notice notice = noticeDao.getNotice(noticeId);
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNotice(notice);

        //開始日
        TextView lblStartDate = (TextView)findViewById(R.id.lblStartDate);
        lblStartDate.setText(BestTravelUtil.convertDateToStringDispDate(noticeDto.startDate));

        //タイトル
        TextView lblTitle = (TextView)findViewById(R.id.lblTitle);
        lblTitle.setText(noticeDto.title);

        //内容（HTML）
        WebView webContents = (WebView)findViewById(R.id.webContents);
        webContents.loadDataWithBaseURL(null, noticeDto.contents, "text/html", "UTF-8", null);

        //閉じるボタン
        Button btnClose = (Button)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //アクティビティを閉じる
                finish();
            }
        });
    }
}
