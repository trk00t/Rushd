package com.rushd.app.services;

// Uncomment this entire file and add google-services.json to enable push notifications.
//
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
// import android.app.PendingIntent;
// import android.content.Context;
// import android.content.Intent;
// import android.media.RingtoneManager;
// import android.net.Uri;
// import android.os.Build;
// import android.util.Log;
//
// import androidx.core.app.NotificationCompat;
//
// import com.google.firebase.messaging.FirebaseMessagingService;
// import com.google.firebase.messaging.RemoteMessage;
// import com.rushd.app.MainActivity;
// import com.rushd.app.R;
//
// public class RushdFirebaseMessagingService extends FirebaseMessagingService {
//
//     private static final String TAG = "RushdFCM";
//     private static final String CHANNEL_ID = "rushd_notifications";
//
//     @Override
//     public void onMessageReceived(RemoteMessage remoteMessage) {
//         Log.d(TAG, "Message from: " + remoteMessage.getFrom());
//
//         String title = "Rushd";
//         String body  = "";
//
//         if (remoteMessage.getNotification() != null) {
//             title = remoteMessage.getNotification().getTitle();
//             body  = remoteMessage.getNotification().getBody();
//         } else if (!remoteMessage.getData().isEmpty()) {
//             title = remoteMessage.getData().getOrDefault("title", title);
//             body  = remoteMessage.getData().getOrDefault("body", "");
//         }
//
//         sendNotification(title, body);
//     }
//
//     @Override
//     public void onNewToken(String token) {
//         Log.d(TAG, "Refreshed FCM token: " + token);
//         // TODO: Send token to your backend server
//     }
//
//     private void sendNotification(String title, String messageBody) {
//         Intent intent = new Intent(this, MainActivity.class);
//         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//         PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                 PendingIntent.FLAG_IMMUTABLE);
//
//         Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//         NotificationCompat.Builder notificationBuilder =
//                 new NotificationCompat.Builder(this, CHANNEL_ID)
//                         .setSmallIcon(R.drawable.ic_notification)
//                         .setContentTitle(title)
//                         .setContentText(messageBody)
//                         .setAutoCancel(true)
//                         .setSound(defaultSoundUri)
//                         .setContentIntent(pendingIntent);
//
//         NotificationManager notificationManager =
//                 (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             NotificationChannel channel = new NotificationChannel(
//                     CHANNEL_ID, "Rushd Notifications",
//                     NotificationManager.IMPORTANCE_DEFAULT);
//             notificationManager.createNotificationChannel(channel);
//         }
//
//         notificationManager.notify(0, notificationBuilder.build());
//     }
// }

/**
 * Placeholder so the package compiles without Firebase.
 * See the commented code above to activate push notifications.
 */
public class RushdFirebaseMessagingService {
    // Firebase not yet configured – see instructions in README.md
}
