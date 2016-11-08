package jp.co.accel_road.besttravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.co.accel_road.besttravel.common.BestTravelConstant;
import jp.co.accel_road.besttravel.model.AlbumDataDto;
import jp.co.accel_road.besttravel.model.AlbumDto;

/**
 * アルバムデータを表示するアクティビティ
 */
public class AlbumDataDetailActivity extends BaseActivity {

    /** 対象となるアルバムデータのリスト */
    private List<AlbumDataDto> albumDataDtoList;
    /** 現在表示対象のアルバムデータID */
    private int dispAlbumDataIndex;

    private Button btnDelete;
    private Button btnDownload;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_data_detail);

        //ツールバーの設定
        setToolbar();

        //呼び出し時のパラメーターを取得
        Intent intent = getIntent();
        //アルバムデータDTOのリストを取得
        AlbumDto albumDto = (AlbumDto)intent.getSerializableExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DTO);
        albumDataDtoList = albumDto.albumDataDtoList;
        //最初に表示するアルバムデータIDを取得
        dispAlbumDataIndex = intent.getIntExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DATA_INDEX, 0);

        //表示対象となるアルバムデータを取得する
        AlbumDataDto albumDataDto = albumDataDtoList.get(dispAlbumDataIndex);

        // アイコン画像
        ImageView imgIcon = (ImageView)findViewById(R.id.imgIcon);
        if (albumDataDto.newFlg) {
            Picasso.with(getApplicationContext()).load(albumDataDto.localFileUrl).fit().centerInside().into(imgIcon);
        } else {
            Picasso.with(getApplicationContext()).load(albumDataDto.fileUrl).fit().centerInside().into(imgIcon);
        }

        //削除ボタン
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //現在表示しているアルバムデータを取得
                AlbumDataDto albumDataDto = albumDataDtoList.get(dispAlbumDataIndex);

                //削除指定を変更する
                albumDataDto.deleteFlg = !albumDataDto.deleteFlg;

                //削除ボタンに指定するラベル
                setDeleteButtonLavel(albumDataDto.deleteFlg);
            }
        });
        //削除ボタンに指定するラベル
        setDeleteButtonLavel(albumDataDto.deleteFlg);

        //ダウンロードボタン
        btnDownload = (Button)findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //閉じるボタン
        btnClose = (Button)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //戻り時の処理を行う
                Intent intent = new Intent();
                AlbumDto albumDto = new AlbumDto();
                albumDto.albumDataDtoList = albumDataDtoList;
                intent.putExtra(BestTravelConstant.PARAMETER_KEY_ALBUM_DTO, albumDto);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 削除ボタンに表示するラベルを指定します。
     *
     * @param deleteFlg
     */
    private void setDeleteButtonLavel(boolean deleteFlg) {

        if (deleteFlg) {
            btnDelete.setText("削除解除");
        } else {
            btnDelete.setText("削除");
        }
    }

}
