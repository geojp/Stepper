package edu.uw.ubicomplab.accelapp;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Step counter
    private StepCounter stepCounter;
    private SignalGrapher signalGrapher2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etMax = findViewById(R.id.eTMax);
        final EditText etMin = findViewById(R.id.eTMin);
        final EditText etSkip = findViewById(R.id.eTSkip);

        // init step counter
        stepCounter = new StepCounter(new StepCounter.StepEventListener() {
            @Override
            public void onStep(int steps) {
                TextView tvStep = findViewById(R.id.tvStep);
                tvStep.setText(steps + "");
            }
        });

        // init signal graphers

        signalGrapher2 = new SignalGrapher(findViewById(R.id.graph2));

        // Get the sensors
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 5000);

        // set button handler
        Button btnClear = findViewById(R.id.bClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepCounter.reset();
            }
        });

        Button btnSet = findViewById(R.id.bSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double hi = Double.parseDouble(etMax.getText().toString());
                double lo = Double.parseDouble(etMax.getText().toString());
                int skip = Integer.parseInt(etSkip.getText().toString());
                stepCounter.updateThreshSkip(hi,lo,skip);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            DataPoint[] pts = stepCounter.addDataPoint(event.values[0], event.values[1], event.values[2]);
            //signalGrapher.addSignalPoint(pts[0]);
            signalGrapher2.addSignalPoint(pts[1]);

            TextView vMin = findViewById(R.id.tvMin);
            vMin.setText(String.format("%.2f", stepCounter.getMinY()));
            TextView vMax = findViewById(R.id.tvMax);
            vMax.setText(String.format("%.2f", stepCounter.getMaxY()));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
