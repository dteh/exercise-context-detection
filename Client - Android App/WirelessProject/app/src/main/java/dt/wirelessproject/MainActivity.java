package dt.wirelessproject;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import android.os.FileObserver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static String user;
    public static DirManager dirManager;
    static boolean listening = false;
    public static TextView AT;
    LogService logger = new LogService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // COMMENT OUT THESE TWO DIRMANAGER LINES TO DISABLE SENDING FILE TO SERVER
        dirManager = new DirManager(
                getApplicationContext().getExternalFilesDir(null).getAbsolutePath());
        dirManager.startWatching();
        AT = (TextView) findViewById(R.id.predictedActivity);


        //scheduleAlarm();
    }

    public void getActivityType(View view){
        if(!listening) {
            String username = ((EditText) findViewById(R.id.editText)).getText().toString();
            MainActivity.user = username;
            System.out.println(user);
            listening = true;
            ((Button)findViewById(R.id.trainingButton)).setText("Stop Recording");
            // start listening process
            Toast.makeText(this, "Now recording!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LogService.class);
            i.putExtra("type","PREDICT");
            i.putExtra("activityName","PREDICT");
            getApplicationContext().startService(i);
            findViewById(R.id.editText).setVisibility(View.INVISIBLE);

        }else{

            listening = false;
            ((Button)findViewById(R.id.trainingButton)).setText("Record Activity Timeline");
            // stop listening process
            Toast.makeText(this, "Stopped recording", Toast.LENGTH_SHORT).show();
            findViewById(R.id.editText).setVisibility(View.VISIBLE);


        }
    }

    private void scheduleAlarm(){
        Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this,21337,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000 * 60 * 3,pIntent);
    }

    public void setActivityText(String text){
        ((TextView)this.findViewById(R.id.predictedActivity)).setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        listening = false;
        System.exit(0);
    }
}
