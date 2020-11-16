package com.eskdr.eskandar;

import android.Manifest;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity {

    private final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        if (PermissionUtils
                .requestPermission(this, LOCATION_PERMISSION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setShowCurrentLocationMarker(true);
        }
    }

}
