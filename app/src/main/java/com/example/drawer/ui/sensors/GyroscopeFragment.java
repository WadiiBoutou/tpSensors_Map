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
import androidx.fragment.app.Fragment;

import com.example.drawer.R;

public class GyroscopeFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private BallView ballView;

    private float screenWidth, screenHeight;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gyroscope, container, false);

        // Initialize BallView
        ballView = new BallView(requireContext());
        ViewGroup container2D = root.findViewById(R.id.device_3d_container);
        container2D.addView(ballView);

        // Get screen dimensions
        root.post(() -> {
            screenWidth = container2D.getWidth();
            screenHeight = container2D.getHeight();
            ballView.setScreenDimensions(screenWidth, screenHeight);
            ballView.updateBallPosition(screenWidth / 2, screenHeight / 2); // Center the ball
        });

        // Initialize SensorManager
        sensorManager = (SensorManager) requireContext().getSystemService(requireContext().SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Convert rotation matrix to orientation angles
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);

            // Get pitch and roll
            float pitch = (float) Math.toDegrees(orientation[1]); // X-axis rotation
            float roll = (float) Math.toDegrees(orientation[2]);  // Y-axis rotation

            // Map pitch and roll to screen coordinates
            float ballX = screenWidth / 2 + roll * (screenWidth / 90); // Roll affects horizontal movement
            float ballY = screenHeight / 2 - pitch * (screenHeight / 90); // Pitch affects vertical movement

            // Update ball position
            ballView.updateBallPosition(ballX, ballY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}