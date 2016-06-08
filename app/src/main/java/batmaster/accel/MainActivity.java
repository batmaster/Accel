package batmaster.accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private SensorEventListener listener;

    private long lastUpdate = 0;

    private float maxSpeed = 0;

    // 100 ms
    private static float UPDATE_RATE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        listener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                Sensor mySensor = event.sensor;

                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float ax = event.values[0];
                    float ay = event.values[1];
                    float az = event.values[2];

                    long curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate) > UPDATE_RATE) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        // m/s^2 => km/hr
                        float dx = (float) (ax * Math.pow(UPDATE_RATE / 1000, 2));
                        float dy = (float) (ay * Math.pow(UPDATE_RATE / 1000, 2));
                        float dz = (float) (az * Math.pow(UPDATE_RATE / 1000, 2));

                        float d = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
                        float kmhr = (float) (d * 3600 / UPDATE_RATE);

                        Log.d("axis", dx + " " + dy + " " + dz + " " + d + " " + kmhr);
                        if (kmhr > maxSpeed) {
                            maxSpeed = kmhr;
                            textView.setText(kmhr + "");
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        senSensorManager.registerListener(listener, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(listener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(listener);
    }
}
