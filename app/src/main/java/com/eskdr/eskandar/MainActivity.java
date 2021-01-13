package com.eskdr.eskandar;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private VoiceRecorder voiceRecorder;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Intent acquirePermissionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acquirePermissionIntent = new Intent(this, PermissionUtils.class);
        acquirePermissionIntent.putExtra("permissions", permissions);
        startActivityForResult(acquirePermissionIntent, REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION) {
            int permissionResult = data.getIntExtra("permissionResult", 0);

            if (permissionResult != 0) {
                acquirePermissionIntent = new Intent(this, PermissionUtils.class);
                acquirePermissionIntent.putExtra("permissions", permissions);
                startActivityForResult(acquirePermissionIntent, REQUEST_PERMISSION);
            } else {
                fragmentManager = getSupportFragmentManager();
                voiceRecorder = new VoiceRecorder();

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.recorder_fragment, voiceRecorder).commitAllowingStateLoss();

                MapView mapView = new MapView(this);
                ViewGroup mapViewContainer = findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                mapView.setShowCurrentLocationMarker(true);
            }
        }
    }
}
