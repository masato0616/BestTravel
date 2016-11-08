package jp.co.accel_road.besttravel.listener;

/**
 * アクセストークン再取得後の処理を行うリスナー
 *
 * Created by masato on 2015/12/30.
 */
public interface AccessTokenListener {
    /** 再実行処理 */
    public void onAgainSend();
}
