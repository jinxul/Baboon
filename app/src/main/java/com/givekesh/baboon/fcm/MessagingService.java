package com.givekesh.baboon.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.givekesh.baboon.activities.MainActivity;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Utils utils = new Utils(this);
        if (remoteMessage.getData().containsKey("new_version")) {
            int market = 0;
            String version = remoteMessage.getData().get("new_version");
            if (remoteMessage.getData().containsKey("market_id"))
                market = Integer.parseInt(remoteMessage.getData().get("market_id"));

            if (utils.shouldNotify("notifications_new_version") &&
                    utils.isUpdate(version))
                sendNotification(utils.getMarketIntent(market),
                        String.format(getString(R.string.new_version_content),
                                version,
                                utils.getMarketName(market)),
                        getString(R.string.new_version_title), getString(R.string.channel_id_new_version));
        } else {
            if (utils.shouldNotify("notifications_new_post"))
                sendNotification(new Intent(this, MainActivity.class),
                        remoteMessage.getNotification().getBody(),
                        remoteMessage.getNotification().getTitle(),
                        getString(R.string.channel_id_new_post));
        }
    }

    private void sendNotification(Intent intent, String messageBody, String title, String channelId) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(Html.fromHtml(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
