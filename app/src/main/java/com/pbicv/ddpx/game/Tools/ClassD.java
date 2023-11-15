package com.pbicv.ddpx.game.Tools;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pbicv.ddpx.R;
import com.pbicv.ddpx.game.pojo.ImportantValue;


import java.util.Map;

public class ClassD extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        LogUtils.dTag(this.getClass().getName(), "Refreshed token: " + token);
        LogUtils.dTag(this.getClass().getName(), "sendRegistrationTokenToServer token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String str;
        String str2;
        String str3;
        String str4;

        LogUtils.dTag(this.getClass().getName(), "From: " + remoteMessage.getFrom());
        Map<String, String> data = remoteMessage.getData();

        if (data.isEmpty()) {
            str = "";
            str2 = str;
            str3 = str2;
            str4 = str3;
        } else {
            LogUtils.dTag(this.getClass().getName(), "Message data payload: " + remoteMessage.getData());
            String valueOf = String.valueOf(remoteMessage.getData().get("jumpTitle"));
            str2 = String.valueOf(remoteMessage.getData().get("jumpDesc"));
            str = valueOf;
            str3 = String.valueOf(remoteMessage.getData().get("jumpPath"));
            str4 = String.valueOf(remoteMessage.getData().get("jumpParam"));
        }
        RemoteMessage.Notification remoteMessageNotification = remoteMessage.getNotification();
        if (remoteMessageNotification != null) {
            LogUtils.dTag(this.getClass().getName(), "Message Notification Body: " + remoteMessageNotification.getBody());

            String body = remoteMessageNotification.getBody();
            if (body != null) {
                LogUtils.dTag(this.getClass().getName(), " body : " + body);
                buildNotificationMessage(body, str, str2, str3, str4);
            }
        }
    }

    private final void buildNotificationMessage(String str, String str2, String str3, String str4, String str5) {
        Intent intent = new Intent(this, ClassC.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        intent.putExtra("jumpTitle", str2);
        intent.putExtra("jumpDesc", str3);
        intent.putExtra("jumpPath", str4);
        intent.putExtra("jumpParam", str5);
        String string = "fcmChannelId_" + ImportantValue.APP_ID;

        NotificationCompat.Builder contentIntent = new NotificationCompat.Builder(this, string).setSmallIcon(R.mipmap.icon).setContentTitle(getString(R.string.app_name)).setContentText(str).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(activity);
        Object systemService = getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager notificationManager = (NotificationManager) systemService;
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel(string, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(5, contentIntent.build());
    }

}
