package dt.wirelessproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by dt on 10/15/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    String status;
    public AlarmReceiver(){
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,LogService.class);
        context.startService(i);
        try{
            new ActivityGetter(MainActivity.user,MainActivity.dirManager.remoteHost).execute();
        }catch(Exception e){};
        Log.i("Alarm","Alarm Received");
    }
}
