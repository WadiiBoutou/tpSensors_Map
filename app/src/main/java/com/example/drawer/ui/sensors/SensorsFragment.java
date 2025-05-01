package com.example.drawer.ui.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.drawer.R;

import java.util.ArrayList;
import java.util.List;

public class SensorsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensors, container, false);

        // Get the SensorManager
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        // Get the list of sensors
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Prepare a list of sensor names
        List<String> sensorNames = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            sensorNames.add(sensor.getName());
        }

        // Set up the ListView
        ListView listView = root.findViewById(R.id.sensors_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, sensorNames);
        listView.setAdapter(adapter);

        return root;
    }
}