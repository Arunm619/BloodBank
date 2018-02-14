package com.atribus.bloodbankyrc.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.atribus.bloodbankyrc.MainActivity;
import com.atribus.bloodbankyrc.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by root on 14/2/18.
 */
public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
   // SharedPreferences prefs;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages.
        String notificationTitle = null, notificationBody = null;
        String dataTitle = null, dataMessage = null;

       // prefs = getApplicationContext().getSharedPreferences("MYDB", MODE_PRIVATE);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
            dataTitle = remoteMessage.getData().get("title");
            dataMessage = remoteMessage.getData().get("message");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle, notificationBody, dataTitle, dataMessage);
    }

    //  Create and show a simple notification containing the received FCM messages
    private void sendNotification(String notificationTitle, String notificationBody, String dataTitle, String dataMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

    /*    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, "hey")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hello! Its urgent!")
                .setContentText("We need you!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
        }


    */

        PugNotification.with(this)
                .load()
                .title("Time to save some lives!")
                .message("We need youQ")
                .bigTextStyle("a")

                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()

                .build();
    }
}
