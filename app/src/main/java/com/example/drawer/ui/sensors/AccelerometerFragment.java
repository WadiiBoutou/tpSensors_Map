package com.example.drawer.ui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.drawer.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class AccelerometerFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LineChart speedChart;
    private List<Entry> speedEntries = new ArrayList<>();
    private float lastX, lastY, lastZ;
    private long lastUpdateTime;
    private Handler handler;
    private int timeCounter = 0; // Counter for the X-axis (time)

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_accelerometer, container, false);
        speedChart = root.findViewById(R.id.speed_chart);

        // Initialize SensorManager and Accelerometer
        sensorManager = (SensorManager) requireContext().getSystemService(getContext().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        handler = new Handler(Looper.getMainLooper());

        setupChart();
        return root;
    }

    private void setupChart() {
        // Configure the chart
        speedChart.getDescription().setEnabled(false);
        speedChart.setTouchEnabled(true);
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        speedChart.setPinchZoom(true);
        speedChart.setBackgroundColor(getResources().getColor(android.R.color.black)); // Set background to black

        // Configure X-axis
        XAxis xAxis = speedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // Disable grid lines
        xAxis.setTextColor(getResources().getColor(android.R.color.white)); // Set X-axis label color to white
        xAxis.setAxisLineColor(getResources().getColor(android.R.color.white)); // Set X-axis line color to white

        // Configure Y-axis (Left)
        YAxis leftAxis = speedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Disable grid lines
        leftAxis.setTextColor(getResources().getColor(android.R.color.white)); // Set Y-axis label color to white
        leftAxis.setAxisLineColor(getResources().getColor(android.R.color.white)); // Set Y-axis line color to white

        // Disable the right Y-axis
        YAxis rightAxis = speedChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Set initial data
        LineData data = new LineData();
        speedChart.setData(data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacksAndMessages(null); // Stop updates when fragment is inactive
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastUpdateTime) > 500) { // Update every 0.5 seconds
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            float speed = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / (currentTime - lastUpdateTime) * 10000;

            // Add the speed to the graph
            addEntry(speed);

            lastX = x;
            lastY = y;
            lastZ = z;
            lastUpdateTime = currentTime;
        }
    }

    private void addEntry(float speed) {
        LineData data = speedChart.getData();
        if (data != null) {
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                data.addDataSet(dataSet);
            }

            // Add a new entry
            data.addEntry(new Entry(timeCounter++, speed), 0);
            data.notifyDataChanged();

            // Refresh the chart
            speedChart.notifyDataSetChanged();
            speedChart.setVisibleXRangeMaximum(10); // Show the last 10 seconds
            speedChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Speed (m/s)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.purple_500));
        set.setLineWidth(2f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}