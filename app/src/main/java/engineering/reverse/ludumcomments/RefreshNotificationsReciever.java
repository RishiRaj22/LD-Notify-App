package engineering.reverse.ludumcomments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author Rishi Raj
 */

public class RefreshNotificationsReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);

        int gameno = prefs.getInt("gameno",-1);
        if(gameno == -1)
            return;

        AlarmManager alarmManager = (AlarmManager)
                context.getApplicationContext().
                        getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        context,
                        112,
                        new Intent(context, NotificationTimer.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                1000 * 60 * 12,
                pendingIntent);
    }
}
