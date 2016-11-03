package com.example.buglab.liveon.utility;

/**
 * Created by aabir on 10/26/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.buglab.liveon.activity.AlarmResponseActivity;
import com.example.buglab.liveon.activity.AlarmSetActivity;

import com.example.buglab.liveon.R;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static String message="description";
    public static final String WAKE = "Wake up";
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, AlarmReceiver.class), 0);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_liveon_logo)
                        .setContentTitle("Alarm")
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);


        //DATABASE
        String id=intent.getStringExtra("requestCode");
        Log.d("ALARMDB","Inside receiver id="+id);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());


//        AlarmResponseActivity.description.setText(message);
        Intent intent2 = new Intent(context,AlarmResponseActivity.class);
        intent2.putExtra(WAKE, true);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("requestCode", id);
        context.startActivity(intent2);

        // For our recurring task, we'll just display a message
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }

}
