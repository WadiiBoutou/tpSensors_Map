package com.example.drawer.ui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ProximityFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private LineChart proximityChart;

    private List<Entry> proximityEntries = new ArrayList<>();
    private LineDataSet proximityDataSet;
    private LineData proximityData;

    private int timeIndex = 0; // X-axis index for time

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_proximity, container, false);

        // Initialize LineChart
        proximityChart = root.findViewById(R.id.proximity_chart);
        setupChart();

        // Initialize SensorManager
        sensorManager = (SensorManager) requireContext().getSystemService(requireContext().SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            // Display a message if the proximity sensor is not available
            proximityEntries.add(new Entry(0, 0));
            proximityDataSet.notifyDataSetChanged();
            proximityData.notifyDataChanged();
            proximityChart.invalidate();
        }

        return root;
    }

    private void setupChart() {
        // Initialize the dataset
        proximityDataSet = new LineDataSet(proximityEntries, "Proximity (cm)");
        proximityDataSet.setColor(getResources().getColor(R.color.purple_200));
        proximityDataSet.setCircleColor(getResources().getColor(R.color.teal_200));
        proximityDataSet.setLineWidth(2f);
        proximityDataSet.setCircleRadius(4f);
        proximityDataSet.setDrawValues(false);

        // Initialize the LineData
        proximityData = new LineData(proximityDataSet);
        proximityChart.setData(proximityData);

        // Configure the chart
        proximityChart.getDescription().setEnabled(false);
        proximityChart.setTouchEnabled(true);
        proximityChart.setDragEnabled(true);
        proximityChart.setScaleEnabled(true);
        proximityChart.setPinchZoom(true);

        // Configure X-axis
        XAxis xAxis = proximityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, true);

        // Configure Y-axis
        YAxis leftAxis = proximityChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setLabelCount(5, true);

        YAxis rightAxis = proximityChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];

            // Add the proximity value to the chart
            proximityEntries.add(new Entry(timeIndex++, distance));
            if (proximityEntries.size() > 50) { // Limit to 50 entries
                proximityEntries.remove(0);
            }

            proximityDataSet.notifyDataSetChanged();
            proximityData.notifyDataChanged();
            proximityChart.notifyDataSetChanged();
            proximityChart.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}