package com.example.shubh.access;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.*;
import java.lang.Math;
import static com.example.shubh.access.MainActivity.ip;
import static com.example.shubh.access.MainActivity.port;
import static com.example.shubh.access.MainActivity.start_time_shared;
import static com.example.shubh.access.MainActivity.shared_lat;
import static com.example.shubh.access.MainActivity.shared_lon;

/**
 * Created by shubh on 19/03/18.
 */

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 1000; // 1 seconds
    public static float direction_heading = 0;
    // run on another Thread to avoid crash

    // timer handling
    public static Timer mTimer = null;
    private GPSTracker gps;
    private SensorManager mSensorManager;
    private Sensor mSensor, linear_acceleration_sensor,gyroscope_sensor,orientation_sensor,magnetic_field_sensor;
    private float x,y,z;
    private float alpha = (float) 0.8;
    private float gravity[] = new float[3];
    private float linear_acceleration[] = new float[3];
    double total_acceleration = (float) 0.0;
    private float magnetic_field[] = new float[3];
    private float orientation[] = new float[3];
    private Calendar cal;
    private double arr[] = new double[10000];
    int index = 20;
    Intent intent;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        gps = new GPSTracker(TimeService.this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linear_acceleration_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientation_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        magnetic_field_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



//        SensorEvent event =
        //intent = getIntent();
        //docFilePath = intent.getExtras().getString("FilePath");
//        starttime = cal.getTimeInMillis();
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }


    class TimeDisplayTimerTask extends TimerTask {

//        public void run() {
//            // run on another thread
//            mHandler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    // display toast
//                    Toast.makeText(getApplicationContext(), getDateTime(),
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            });
//
//        }

        @Override
        public void run() {
            // run on another thread
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
//                    double total = Math.sqrt(x * x + y * y + z * z);



                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                    linear_acceleration[0] = event.values[0] - gravity[0];
                    linear_acceleration[1] = event.values[1] - gravity[1];
                    linear_acceleration[2] = event.values[2] - gravity[2];
                    total_acceleration = Math.sqrt(linear_acceleration[0]*linear_acceleration[0]+linear_acceleration[1]*linear_acceleration[1]+
                    linear_acceleration[2]*linear_acceleration[2]);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

            }, linear_acceleration_sensor, SensorManager.SENSOR_DELAY_FASTEST);

            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    magnetic_field[0] = event.values[0];
                    magnetic_field[1] = event.values[1];
                    magnetic_field[2] = event.values[2];
//                    double total = Math.sqrt(x * x + y * y + z * z);
//
//
//
//                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//                    linear_acceleration[0] = event.values[0] - gravity[0];
//                    linear_acceleration[1] = event.values[1] - gravity[1];
//                    linear_acceleration[2] = event.values[2] - gravity[2];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

            }, magnetic_field_sensor, SensorManager.SENSOR_DELAY_FASTEST);

            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    orientation[0] = event.values[0];
                    orientation[1] = event.values[1];
                    orientation[2] = event.values[2];
//                    double total = Math.sqrt(x * x + y * y + z * z);
//
//
//
//                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//                    linear_acceleration[0] = event.values[0] - gravity[0];
//                    linear_acceleration[1] = event.values[1] - gravity[1];
//                    linear_acceleration[2] = event.values[2] - gravity[2];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

            }, orientation_sensor, SensorManager.SENSOR_DELAY_FASTEST);

            //long starttime = start_time_shared;
            cal = Calendar.getInstance();
            if(magnetic_field[0] != 0)
                direction_heading = (float) (Math.atan2(magnetic_field[1],magnetic_field[0])*( 180 / Math.PI));


            int bool_dir = 1; // 1 means South
            // Orientation[0] is the direction in degrees where the mobile is heading towards
            // Thus it can be used to know which direction a person is walking
            // And what would be next landmark in the way.
            // If the map is dense then it can be discretized in 8 direction or else.
            // At present it is set for North/ South only
            if( (orientation[0]< 160) || ( 330 < orientation[0]))
                bool_dir = 0; // 0 means North
            arr[index] = (double) (orientation[0]);
            if(orientation[0] > 330)
                arr[index] = (double) (orientation[0]-330);
            double avg = 0.0;
            for (int i = 0; i<5; i++)
                avg += arr[index-i];
            avg = avg/5;
            index++;

            int avg_header = 1;
            if( (avg < 160) || ( 330 < avg))
                avg_header = 0; // 0 means North


            String message = ((double)(cal.getTimeInMillis() - start_time_shared))/1000 + " Latitude " +shared_lat + " Longitude: " +shared_lon
                    + " Acceleration " + (double) linear_acceleration[0]+" "+ (double) linear_acceleration[1]+" "+ (double) linear_acceleration[2] + " "+total_acceleration
                    +" Magnetic_Field "+ (double) magnetic_field[0]+" "+ (double) magnetic_field[1]+" "+ (double) magnetic_field[2]
                    +" Orientation "+ (double) orientation[0]+" "+ (double) orientation[1]+" "+ (double) orientation[2]
                    + " Heading: "+ bool_dir + " avg: " + avg+" avg_head " + avg_header;

            AsyncTCPSend t1 = new AsyncTCPSend(ip, port, message);
            t1.execute();
        }


        private String getl()
        {
            return  "";
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }


    }
    public void stopTask() {

        if (mTimer != null) {
            //hTextView.setText("Timer canceled: " + nCounter);

            Log.d("TIMER", "timer canceled");
            mTimer.cancel();
        }
    }

}