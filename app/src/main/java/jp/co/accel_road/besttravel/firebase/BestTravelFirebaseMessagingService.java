package jp.co.accel_road.besttravel.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * プッシュ通知を受け取るサービスです。
 *
 * Created by masato on 2016/09/25.
 */

public class BestTravelFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 通知設定
        Map<String, String> data = remoteMessage.getData();
    }
}
