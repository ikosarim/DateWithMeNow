package com.example.datingapplication.ui.date_map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapplication.R;
import com.example.datingapplication.ui.cancel_reason.CancelReasonActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.mapbox.api.directions.v5.DirectionsCriteria.OVERVIEW_FULL;
import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_WALKING;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.maps.Style.LIGHT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class DateMapActivity extends AppCompatActivity implements OnLocationClickListener, OnMapReadyCallback {

    private String time;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    private GeoJsonSource source;

    private EditText inputPlace;
    private FloatingActionButton yourLocation;
    private FloatingActionButton searchButton;

    private ArrayList<PersonCardViewModel> personCardViewModelList = new ArrayList<>();
    private ListView personCardList;
    private FrameLayout personCardListFrame;
    private Button backToMapButton;
    private Button cancelDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_date_map);

        time = getIntent().getStringExtra("time");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        inputPlace = findViewById(R.id.search_place);
        yourLocation = findViewById(R.id.back_to_camera_tracking_mode);

        searchButton = findViewById(R.id.search_human);
        searchButton.setOnClickListener(v -> setPersonCardMode());

        personCardList = (ListView) findViewById(R.id.list_person_cards);
        personCardListFrame = (FrameLayout) findViewById(R.id.person_card_list_frame);
        personCardListFrame.setEnabled(false);
        personCardListFrame.setVisibility(INVISIBLE);
        backToMapButton = (Button) findViewById(R.id.return_to_map_button);
        backToMapButton.setOnClickListener(v -> clearSearchData());
        cancelDateButton = (Button) findViewById(R.id.cancel_date);
        cancelDateButton.setOnClickListener(v -> cancelDateRunActivity());
        cancelDateButton.setEnabled(false);
        cancelDateButton.setVisibility(INVISIBLE);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(LIGHT, this::enableLocationComponent);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .foregroundDrawable(R.drawable.mapbox_marker_icon_default)
                    .build();

            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            locationComponent.addOnLocationClickListener(this);

            yourLocation.setOnClickListener(view -> determineYourLocation());

            initDottedLineSourceAndLayer(loadedMapStyle);

        }
    }

    private void determineYourLocation() {
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.zoomWhileTracking(16f);
        Toast.makeText(DateMapActivity.this, getString(R.string.tracking_enabled),
                Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }


    private void clearSearchData() {
        personCardViewModelList = new ArrayList<>();
        setScreenMode(true);
    }

    private void setPersonCardMode() {
//        Делаю get запрос и получаю массив анкет
        PersonCardViewModel model1 = new PersonCardViewModel(
                getString(R.string.name1),
                getString(R.string.age1),
                getString(R.string.comment1),
                getString(R.string.latitude1),
                getString(R.string.longitude1),
                getString(R.string.phoneNumber1)
        );
        PersonCardViewModel model2 = new PersonCardViewModel(
                getString(R.string.name2),
                getString(R.string.age2),
                getString(R.string.comment2),
                getString(R.string.latitude2),
                getString(R.string.longitude2),
                getString(R.string.phoneNumber2)
        );
        PersonCardViewModel model3 = new PersonCardViewModel(
                getString(R.string.name3),
                getString(R.string.age3),
                getString(R.string.comment3),
                getString(R.string.latitude3),
                getString(R.string.longitude3),
                getString(R.string.phoneNumber3)
        );
        PersonCardViewModel model4 = new PersonCardViewModel(
                getString(R.string.name4),
                getString(R.string.age4),
                getString(R.string.comment4),
                getString(R.string.latitude4),
                getString(R.string.longitude4),
                getString(R.string.phoneNumber4)
        );

        PersonCardAdapter personCardAdapter = new PersonCardAdapter(this, R.layout.fragment_person_card, personCardViewModelList);

        personCardList.setAdapter(personCardAdapter);

        String choicePlace = inputPlace.getText().toString();
        if (choicePlace.contains("Горького") || choicePlace.contains("горького")) {
            personCardViewModelList.add(model1);
            personCardViewModelList.add(model2);
        } else if (choicePlace.contains("Победы") || choicePlace.contains("победы")) {
            personCardViewModelList.add(model3);
        } else if (choicePlace.contains("калиновского") || choicePlace.contains("Калиновского")) {
            personCardViewModelList.add(model4);
        }


        setScreenMode(false);
    }

    private void setScreenMode(boolean mapMode) {
        mapView.setVisibility(mapMode ? VISIBLE : INVISIBLE);

        inputPlace.setEnabled(mapMode);
        inputPlace.setVisibility(mapMode ? VISIBLE : INVISIBLE);

        searchButton.setEnabled(mapMode);
        searchButton.setVisibility(mapMode ? VISIBLE : INVISIBLE);

        yourLocation.setEnabled(mapMode);
        yourLocation.setVisibility(mapMode ? VISIBLE : INVISIBLE);

        personCardListFrame.setEnabled(!mapMode);
        personCardListFrame.setVisibility(mapMode ? INVISIBLE : VISIBLE);
    }

    public void buildRouteToSelectedPerson(Point destination) {
        personCardViewModelList = new ArrayList<>();
        mapView.setVisibility(VISIBLE);
        inputPlace.setEnabled(false);
        inputPlace.setVisibility(INVISIBLE);
        searchButton.setEnabled(false);
        searchButton.setVisibility(INVISIBLE);
        yourLocation.setEnabled(true);
        yourLocation.setVisibility(VISIBLE);
        personCardListFrame.setEnabled(false);
        personCardListFrame.setVisibility(INVISIBLE);

        cancelDateButton.setEnabled(true);
        cancelDateButton.setVisibility(VISIBLE);

        Location currentLocation = locationComponent.getLastKnownLocation();
        Point directionsOriginPoint = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());

        MapboxDirections client = MapboxDirections.builder()
                .origin(directionsOriginPoint)
                .destination(destination)
                .overview(OVERVIEW_FULL)
                .profile(PROFILE_WALKING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Timber.d("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.d("No routes found");
                    return;
                }
                drawNavigationPolylineRoute(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.d("Error: %s", throwable.getMessage());
                if (!throwable.getMessage().equals("Coordinate is invalid: 0,0")) {
                    Toast.makeText(DateMapActivity.this,
                            "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDottedLineSourceAndLayer(@NonNull Style loadedMapStyle) {
        dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        loadedMapStyle.addSource(new GeoJsonSource("SOURCE_ID", dashedLineDirectionsFeatureCollection));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        "DIRECTIONS_LAYER_ID", "SOURCE_ID").withProperties(
                        lineWidth(4.5f),
                        lineColor(Color.BLACK),
                        lineTranslate(new Float[]{0f, 4f})
                ), "road-label-small");
    }

    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                List<Feature> directionsRouteFeatureList = new ArrayList<>();
                LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                List<Point> coordinates = lineString.coordinates();
                for (int i = 0; i < coordinates.size(); i++) {
                    directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)));
                }
                dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                source = style.getSourceAs("SOURCE_ID");
                if (source != null) {
                    source.setGeoJson(dashedLineDirectionsFeatureCollection);
                }
            });
        }
    }

    private void cancelDateRunActivity() {
        dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        source.setGeoJson(dashedLineDirectionsFeatureCollection);

        inputPlace.setEnabled(true);
        inputPlace.setVisibility(VISIBLE);
        searchButton.setEnabled(true);
        searchButton.setVisibility(VISIBLE);
        cancelDateButton.setEnabled(false);
        cancelDateButton.setVisibility(INVISIBLE);

        Intent intent = new Intent(DateMapActivity.this, CancelReasonActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
