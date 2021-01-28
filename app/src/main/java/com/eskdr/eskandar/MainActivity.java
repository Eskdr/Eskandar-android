package com.eskdr.eskandar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.util.maps.helper.Utility;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private static final String baseUrl = "http://172.23.16.1";
    private static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private MapPoint currentMapPoint;
    private VoiceRecorder voiceRecorder;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Intent acquirePermissionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KakaoSdk.init(this, getString(R.string.kakao_app_key));

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

                return;
            } else {
                login();
            }
        }

        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setShowCurrentLocationMarker(true);

        fragmentManager = getSupportFragmentManager();
        voiceRecorder = new VoiceRecorder();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.recorder_fragment, voiceRecorder).commitAllowingStateLoss();

        findViewById(R.id.postButton).setOnClickListener(
                v -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LocationService service = retrofit.create(LocationService.class);

                    if (currentMapPoint == null) {
                        currentMapPoint = mapView.getMapCenterPoint();
                    }

                    UserApiClient.getInstance().me(
                            (me, meError) -> {
                                if (meError == null) {
                                    service.postLocation(
                                            new Location(
                                                    me.getId(),
                                                    currentMapPoint.getMapPointGeoCoord().latitude,
                                                    currentMapPoint.getMapPointGeoCoord().longitude
                                            )
                                    ).enqueue(
                                            new Callback<Location>() {
                                                @Override
                                                public void onResponse(Call<Location> call, Response<Location> response) {

                                                }

                                                @Override
                                                public void onFailure(Call<Location> call, Throwable t) {

                                                }
                                            }
                                    );
                                }

                                return null;
                            }
                    );
                }
        );

    }

    private void login() {
        if (LoginClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            LoginClient.getInstance().loginWithKakaoTalk(
                    this,
                    (t, e) -> null
            );
        } else {
            LoginClient.getInstance().loginWithKakaoAccount(
                    this,
                    (t, e) -> null
            );
        }
    }
}
