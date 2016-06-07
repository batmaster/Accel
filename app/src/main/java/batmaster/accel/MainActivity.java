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
    private float last_x, last_y, last_z;

    private float maxSpeed = 0;

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
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    long curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                        Log.d("speed", speed + "");
                        if (speed > maxSpeed) {
                            maxSpeed = speed;
                            textView.setText(speed + "");
                            Log.d("speed max", speed + "");
                        }

                        last_x = x;
                        last_y = y;
                        last_z = z;
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
