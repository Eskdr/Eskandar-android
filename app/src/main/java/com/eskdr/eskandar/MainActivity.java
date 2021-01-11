package com.eskdr.eskandar;

import android.Manifest;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final int LOCATION_PERMISSION = 1;
    private final String TAG = MainActivity.class.getSimpleName();
    private MapPoint currentMapPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KakaoSdk.init(this, getString(R.string.kakao_app_key));

        login();

        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        if (PermissionUtils
                .requestPermission(this, LOCATION_PERMISSION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setShowCurrentLocationMarker(true);

            mapView.setCurrentLocationEventListener(
                    new MapView.CurrentLocationEventListener() {
                        @Override
                        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
                            currentMapPoint = mapPoint;
                        }

                        @Override
                        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

                        }

                        @Override
                        public void onCurrentLocationUpdateFailed(MapView mapView) {

                        }

                        @Override
                        public void onCurrentLocationUpdateCancelled(MapView mapView) {

                        }
                    }
            );
        }

        findViewById(R.id.postButton).setOnClickListener(
                v -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LocationService service = retrofit.create(LocationService.class);

                    if(currentMapPoint == null){
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
