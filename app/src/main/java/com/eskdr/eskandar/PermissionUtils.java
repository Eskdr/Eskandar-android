package com.eskdr.eskandar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionUtils extends Activity {

    private static final int REQUEST_PERMISSION = 1;
    private static Intent requestIntent = null;

    public static boolean permissionGranted(
            int requestCode, int permissionCode, int[] grantResults) {
        return requestCode == permissionCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        requestIntent = getIntent();
        String[] permissions = requestIntent.getStringArrayExtra("permissions");

        for (String s : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, s);
            boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                permissionsNeeded.add(s);
            }
        }

        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_PERMISSION);
        } else {
            requestIntent.putExtra("permissionResult", 0);
            setResult(RESULT_OK, requestIntent);

            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int granted = 0;
        for (int e : grantResults) {
            granted += e;
        }

        requestIntent.putExtra("permissionResult", granted);
        setResult(RESULT_OK, requestIntent);

        finish();
    }
}
