package com.example.shubh.access;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button StartRec;
    private TextView textView;
    private TextView latView,lonView;
    private EditText addressText, portText, messageText;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Socket client;
    private AsyncTCPSend tcpSend,tcpSend1,tcpSend2;
    private Socket socket;
    private DataOutputStream writeOut;
    private int i = 0;

    //private String address;
    //private int port;
    private String message;
    private String response = "";
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder ;
    private Random random ;
    private final String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 10;
    private ThreadPoolExecutor threadPoolExecutor;
    private ExecutorService executor;
    private Intent timerIntent,stoptimerIntent;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public Intent start_time_intent;
    public static long start_time_shared = 0;
    public static double shared_lat = 0;
    public static double shared_lon = 0;
    public static int port = 0;
    public static String ip = "";
    public static int stop_timer_int = 0;

    MediaPlayer mediaPlayer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerIntent = new Intent(this, TimeService.class);
        stoptimerIntent = new Intent(this, TimeService.class);
        start_time_intent = new Intent(getApplicationContext(),TimeService.class);
//        i.putExtra("FilePath", start_time);
//        startActivity(i);

        //threadPoolExecutor = new ThreadPoolExecutor(1,1,100, TimeUnit.MILLISECONDS,)

        StartRec = (Button) findViewById(R.id.start_rec);
        latView = (TextView) findViewById(R.id.latitude);
        lonView = (TextView) findViewById(R.id.longitude);
        addressText = (EditText) findViewById(R.id.ipaddress);
        portText = (EditText) findViewById(R.id.port);
        //messageText = (EditText) findViewById(R.id.msgText);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //textView.append("\n " + location.getLongitude() + " " + location.getLatitude());
                latView.setText(""+ location.getLatitude());
                lonView.setText(""+location.getLongitude());
                executor = Executors.newSingleThreadExecutor();
                //executor = AsyncTask.SERIAL_EXECUTOR;
                double lon = location.getLongitude();
                double lat = location.getLatitude();
                shared_lat = lat;
                shared_lon = lon;
                latView.setText(""+lat);
                lonView.setText(""+lon);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            client = new Socket("10.194.30.173", 46011);
//                            PrintWriter printWriter = new PrintWriter(client.getOutputStream());
//                            printWriter.write("longitude: " + lon);
//                            printWriter.write("\n latitude: " + lat);
//                            printWriter.flush();
//                            printWriter.close();
//                            client.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 10);


            return;
        } else {
            configureButton();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
        }
    }

    private void configureButton() {
        StartRec.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ip = addressText.getText().toString();
                if(ip.equals("")){
                    ip = "172.20.10.4";
                }
                //ip = "172.20.10.2";
                port = Integer.parseInt(portText.getText().toString());
                if(port == 1){
                    port = 47008;
                }
                //message = messageText.getText().toString();
                Calendar cal = Calendar.getInstance();
                long start_time =cal.getTimeInMillis();
                start_time_shared = start_time;

                GPSTracker gps = new GPSTracker(MainActivity.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    latView.setText(""+latitude);
                    lonView.setText(""+longitude);
                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    //new AsyncTCPSend(ip,port,"Latitude "+latitude+" Longitude "+longitude).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , 500, 0, locationListener);

                if (i == 0) {

                    StartRec.setText("Stop Recording");

                    startService(timerIntent);

                    if(checkPermission()) {

                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        /*CreateRandomAudioFileName(5) +*/ start_time_shared+"AudioRecording.3gp";

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
////
////                    buttonStart.setEnabled(false);
////                    buttonStop.setEnabled(true);
//
                        Toast.makeText(MainActivity.this, "Recording started",
                                Toast.LENGTH_LONG).show();

                    } else {
                        requestPermission();
                    }
                    //addressText.getText().toString()
                    i = 1;
                }else
                {

                    String fmsg = "false"; //msg to stop listening and recording the video
                    i = 0;
                    StartRec.setText("Recording Stopped, ReStart App");
                    TimeService.mTimer.cancel();
                    tcpSend= new AsyncTCPSend(ip,port,fmsg);
                    tcpSend.execute();
                    mediaRecorder.stop();
                    Toast.makeText(MainActivity.this, "Recording Completed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case RequestPermissionCode:
//                if (grantResults.length> 0) {
//                    boolean StoragePermission = grantResults[0] ==
//                            PackageManager.PERMISSION_GRANTED;
//                    boolean RecordPermission = grantResults[1] ==
//                            PackageManager.PERMISSION_GRANTED;
//
//                    if (StoragePermission && RecordPermission) {
//                        Toast.makeText(MainActivity.this, "Permission Granted",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
//                    }
//                }
//                break;
//        }
//    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    public class AsyncTCPSend2 extends AsyncTask<Void, Void, Void> {
        String address;
        int port;
        String message;
        String response = "";
        AsyncTCPSend2(String addr, int p, String mes) {
            address = addr;
            port = p;
            message = mes;
        }
        AsyncTCPSend2(String mes) {
            address = "172.20.10.2";
            port = 47002;
            message = mes;
        }


        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket(address, port);
                DataOutputStream writeOut = new DataOutputStream(socket.getOutputStream());
                writeOut.write(message.getBytes());
                android.util.Log.w("SENT", String.format("[%s] %d", message, message.length()));
                writeOut.flush();

                ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream writeIn = socket.getInputStream();

                while((bytesRead = writeIn.read(buffer)) != -1) {
                    writeBuffer.write(buffer,0,bytesRead);
                    response += writeBuffer.toString("UTF-8");
                }
                response = response.substring(4);   //Server sends extra "Null" string in front of data. This cuts it out
            } catch (UnknownHostException e){
                e.printStackTrace();
                response = "Unknown HostException: " + e.toString();
                System.out.println(response);
            } catch (IOException e) {
                response = "IOException: " + e.toString();
                System.out.println(response);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//        recieve.setText(response);
            super.onPostExecute(result);
        }


    }
    private String getDateTime() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("[dd/MM - HH:mm:ss]");
        return sdf.format(new Date());
    }

}

