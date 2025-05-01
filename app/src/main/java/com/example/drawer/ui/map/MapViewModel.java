package com.example.drawer.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {
    // Example: LiveData to hold the selected location
    private final MutableLiveData<LatLng> selectedLocation = new MutableLiveData<>();

    public LiveData<LatLng> getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(LatLng location) {
        selectedLocation.setValue(location);
    }
}