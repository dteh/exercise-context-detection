package dt.wirelessproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dt on 10/15/15.
 */
public class LogService extends IntentService implements SensorEventListener{

    boolean recording;
    File file;
    List<String> gyro;
    List<String> acceleration;
    List<String> proximity;
    List<String> light;
    Timestamp t;
    Long start;

    public LogService(){
        super("Logging-Service");
    }

    /**
     * Handling intent
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Service", "Running the logging service!");

        while(MainActivity.listening) {
            startRecording(4, getApplicationContext());
            String s = intent.getStringExtra("type") + "," + intent.getStringExtra("activityName");
            printToFile(s, getApplicationContext());
        }

        Log.i("Service", "Done recording! Killing service..");
        stopSelf();
    }

    /**
     * Writes contents of sensor arrays and location to file
     * @param dataTag - string to tag data as (training/scheduled/whatever)
     */
    private void printToFile(String dataTag,Context context){
        String typeString = "[TYPE = \"" + dataTag + "\"]";
        Log.i("Recording","Writing information to file");
        file = new File(context.getExternalFilesDir(null),start.toString());
        Log.i("file",file.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(typeString.getBytes());
            fos.write("\n\n".getBytes());

            fos.write("[ACCELERATION]".getBytes());
            fos.write("\n".getBytes());
            for(int i = 0; i < acceleration.size();i++){
                fos.write(acceleration.get(i).getBytes());
                fos.write("\n".getBytes());
            }

            fos.write("\n".getBytes());
            fos.write("[GYRO]".getBytes());
            fos.write("\n".getBytes());
            for(int i = 0; i < gyro.size();i++) {
                fos.write(gyro.get(i).getBytes());
                fos.write("\n".getBytes());
            }
            fos.close();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"ERROR: File operation",
                    Toast.LENGTH_LONG);
        }


    }

    /**
     * Starts recording sensor output and location
     * @param lengthInSeconds number of seconds to record for
     */
    private void startRecording(int lengthInSeconds,Context context){
        //init sensors/location
        Log.i("Recording","Started recording sensors");
        SensorManager sMgr = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sAccel = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor sGyro = sMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sMgr.registerListener(this,sAccel,SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this,sGyro,SensorManager.SENSOR_DELAY_NORMAL);

        gyro = new ArrayList<String>();
        acceleration = new ArrayList<String>();

        //start recording from sensors
        recording = true;
        start = System.currentTimeMillis();
        //record for lengthInSeconds
        while(System.currentTimeMillis() < start + 1000 * lengthInSeconds);
        //stop
        recording = false;
        Log.i("Recording","Finished recording");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && recording){
            acceleration.add("["+new Timestamp(System.currentTimeMillis()).toString()+"] " +
                    event.values[0] + "," + event.values[1] + "," + event.values[2]);

        }else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE && recording){
            gyro.add("["+new Timestamp(System.currentTimeMillis()).toString()+"] " +
                    event.values[0] + "," + event.values[1] + "," + event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
