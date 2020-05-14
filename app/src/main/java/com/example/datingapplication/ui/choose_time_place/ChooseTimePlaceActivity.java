package com.example.datingapplication.ui.choose_time_place;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.datingapplication.R;
import com.example.datingapplication.ui.date_map.DateMapActivity;

public class ChooseTimePlaceActivity extends AppCompatActivity {

    private final Integer GPS_PERMISSION_CODE = 17;

    private Spinner dropdown;
    private Button searchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_time_place);

        sendLocationPermissionRequest(Manifest.permission.ACCESS_FINE_LOCATION, GPS_PERMISSION_CODE);

        dropdown = findViewById(R.id.spinner);
        searchButton = findViewById(R.id.search);

        dropdown.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());

        searchButton.setOnClickListener(v -> {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null){
                finish();
            }
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            while (!gpsEnabled) {
                Log.w("ChooseTimePlaceActivity", "Network provider is not enabled");
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            Log.w("ChooseTimePlaceActivity", "Network provider is enabled - " + gpsEnabled);

            Intent intent = new Intent(ChooseTimePlaceActivity.this, DateMapActivity.class);
            intent.putExtra("time", dropdown.getSelectedItem().toString());
            startActivity(intent);
        });
    }

    private void sendLocationPermissionRequest(String permission, int code) {
        ActivityCompat.requestPermissions(
                this,
                new String[] {permission},
                code
        );

    }
}
