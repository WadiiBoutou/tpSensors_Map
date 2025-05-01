package com.example.drawer.ui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.Manifest;

import com.example.drawer.R;

public class StepCounterFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "StepCounterFragment";
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor stepDetectorSensor;
    private TextView stepCountTextView;
    private int initialStepCount = -1;
    private int cumulativeStepCount = 0; // Stores the total steps across sessions

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_step_counter, container, false);
        stepCountTextView = root.findViewById(R.id.step_count_text);

        // Check for Activity Recognition permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }

        // Initialize SensorManager and Sensors
        sensorManager = (SensorManager) requireContext().getSystemService(getContext().SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepCounterSensor != null) {
            Log.d(TAG, "Step Counter sensor detected.");
        } else {
            Log.e(TAG, "Step Counter sensor not available on this device.");
            stepCountTextView.setText("Step Counter sensor not supported on this device.");
        }

        if (stepDetectorSensor != null) {
            Log.d(TAG, "Step Detector sensor detected.");
        } else {
            Log.e(TAG, "Step Detector sensor not available on this device.");
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "Step Counter sensor listener registered.");
        }
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "Step Detector sensor listener registered.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Log.d(TAG, "Sensor listeners unregistered.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.d(TAG, "Step Counter event detected: " + event.values[0]);

            if (initialStepCount == -1) {
                // Set the initial step count only once
                initialStepCount = (int) event.values[0];
                Log.d(TAG, "Initial step count set to: " + initialStepCount);
            }

            // Calculate steps taken during this session
            int stepsTaken = (int) (event.values[0] - initialStepCount);
            cumulativeStepCount = stepsTaken; // Update the cumulative step count
            stepCountTextView.setText("Steps: " + cumulativeStepCount);
            Log.d(TAG, "Step count updated: " + cumulativeStepCount);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            Log.d(TAG, "Step Detector event detected.");
            // Increment cumulative step count for each step detected
            cumulativeStepCount++;
            stepCountTextView.setText("Steps: " + cumulativeStepCount);
            Log.d(TAG, "Step count updated (Step Detector): " + cumulativeStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}