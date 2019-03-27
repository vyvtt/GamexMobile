package com.gamex.services.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.gamex.R;
import com.gamex.utils.Constant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String exhibitionId = remoteMessage.getData().get(Constant.EXTRA_EX_ID);
        String exhibitionName = remoteMessage.getData().get(Constant.EXTRA_EX_NAME);
        String exhibitionImage = remoteMessage.getData().get(Constant.EXTRA_EX_IMG);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notficationBody = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "My_Channel_01");
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notficationBody);
        builder.setSmallIcon(R.drawable.ic_alert);
        builder.setAutoCancel(true);

        Intent intent = new Intent(click_action);
        intent.putExtra(Constant.EXTRA_EX_NAME, exhibitionName);
        intent.putExtra(Constant.EXTRA_EX_ID, exhibitionId);
        intent.putExtra(Constant.EXTRA_EX_IMG, exhibitionImage);

//        Intent backIntent = new Intent(this, MainActivity.class);
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        Intent intent = new Intent(this, ExhibitionDetailActivity.class);
//        intent.putExtra(Constant.EXTRA_EX_NAME, exhibitionName);
//        intent.putExtra(Constant.EXTRA_EX_ID, exhibitionId);
//        intent.putExtra(Constant.EXTRA_EX_IMG, exhibitionImage);
//
//        final PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
//                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //đẩy noti lên trạng thái ưu tiên
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My_Channel_01", "My_Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        manager.notify((int)(System.currentTimeMillis()/1000), builder.build());

    }
}
