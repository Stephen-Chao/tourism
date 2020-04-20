package com.example.seyaha;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bitvale.switcher.SwitcherX;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiper.MaterialSpinner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DetailedActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "DetailedActivity";
    public int totalCost = 0;
    private GoogleMap mMap;
    ViewPager viewPager;
    List<String> imageUrls;
    List<Place> mPlace;
    ViewPagerAdapter adapter;
    double latitude, longitude;
    String placeName;
    MediaPlayer mp;
    AlertDialog alertDialog;
    Button cancel_temp_dialog_btn, cancel_airquality_dialog_btn;
    TextView min_temp, max_temp, humidity_tv, wind_tv;
    private final String APIKEY = "4e4480d5039580a36c576fa58a0c1d3a";
    ListView activitiesListView;
    private OpenWeatherApi openWeatherApi;
    private double tempApiResult, min, max, speed;
    private int humidity;
    ImageButton zoom_in, zoom_out, text_to_speech;
    View map_view, costView, tempView, qualityView, internetView;
    private Toolbar mToolbar;
    private TextView mTextView;
    ScrollView scrollView;
    SupportMapFragment mapFragment;
    FrameLayout seasonFlip, timeToGoFlip, estimationFlip, ageFlip;
    TextView seasonTv, timeToGoTv, ageTv1, ageTv2, estimationTv, costTv, tempTv, tempOverallTv, airQualityTv, internetTv, placeNameInfo, placeNameRecommendations, placeNameLocation, description, placeNameTitle, entrenceFeesPrice, foodPrice, transportationPrice, nearbyTv;
    RoundCornerProgressBar costProgressBar, tempProgressBar, airQualityProgressBar, internetProgressBar;
    View frontLayoutSeason, backLayoutSeason, frontLayoutTime, backLayouTime, frontLayoutAge, backLayoutAge, frontLayoutEstimated, backLayoutEstimated;
    ImageView seasonImg, timeToGoImg, estimationImg, costDetails;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean[] mIsBackVisible = {false, false, false, false};
    int i = 1, size;
    public static Sharedpreference prefs;
    String Tour_id;
    int index = 1;
    SwitcherX foodCheckbox, entrenceFeesCheckbox, transportationCheckbox, sleepCheckBox;
    String Parent_Key;
    double lat;
    double lng;
    MaterialSpinner overNightSpinner;
    Button applyBtn, cancelBtn, cancel_internet_dialog_btn;
    int PublicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        fillView();
        ActionBarChecker();
        ApiConfig();
        loadAnimations();
        setFirstPlaceDetails();
    }

    private void showClarificationDialog() {
        Button cancel_clarification_dialog_btn;
        final CheckBox dont_show_again;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.clarification_dialog, null);
        builder.setView(mView);
        cancel_clarification_dialog_btn = mView.findViewById(R.id.cancel_clarification_dialog);
        dont_show_again = mView.findViewById(R.id.clarification_checkBox);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_clarification_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dont_show_again.isChecked()) {
                    prefs.setboolPrefs("clarification_dialog", true);
                } else {
                    prefs.setboolPrefs("clarification_dialog", false);
                }
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    public void showDetailedCost(final int position) {
        View mView = getLayoutInflater().inflate(R.layout.cost_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        totalCost = 0;
        foodCheckbox = mView.findViewById(R.id.food_checkBox);
        entrenceFeesCheckbox = mView.findViewById(R.id.entrance_fees_checkBox);
        transportationCheckbox = mView.findViewById(R.id.transportation_checkBox);
        sleepCheckBox = mView.findViewById(R.id.sleep_checkBox);
        entrenceFeesCheckbox.setChecked(prefs.getboolPrefs("enterence", true), false);
        foodCheckbox.setChecked(prefs.getboolPrefs("food", true), false);
        transportationCheckbox.setChecked(prefs.getboolPrefs("trans", true), false);
        sleepCheckBox.setChecked(prefs.getboolPrefs("sleep_checkbox", true), false);

        final TextView entrenceFeesPrice = mView.findViewById(R.id.entrance_fees_price);
        entrenceFeesPrice.setText(mPlace.get(position).cost.entranceFees + " EU");

        TextView foodPrice = mView.findViewById(R.id.food_price);
        foodPrice.setText(mPlace.get(position).cost.food + " EU");


        final TextView transportationPrice = mView.findViewById(R.id.transportation_price);
        transportationPrice.setText(mPlace.get(position).cost.transportation + " EU");

        final MaterialSpinner overNightSpinner = mView.findViewById(R.id.over_night_spinner);
        final OverNightSpinnerAdapter overNightAdapter = new OverNightSpinnerAdapter(this, R.layout.over_night_spinner_item, mPlace.get(position).cost.overNightStay);
        overNightSpinner.setAdapter(overNightAdapter);
        overNightSpinner.setSelection(prefs.getintPrefs("sleep", 1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (SplashScreenActivity.lan.equals("zh"))
                overNightSpinner.setTextDirection(View.TEXT_DIRECTION_RTL);
            else if (SplashScreenActivity.lan.equals("en"))
                overNightSpinner.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        overNightSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                index = i;
            }

            @Override
            public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {
                index = 1;
            }
        });

        if (!sleepCheckBox.isChecked()) {
            overNightSpinner.setVisibility(View.GONE);
        } else {
            overNightSpinner.setVisibility(View.VISIBLE);
        }
        sleepCheckBox.setOnCheckedChangeListener(new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean checked) {


                if (checked) {
                    overNightSpinner.setVisibility(View.VISIBLE);
                } else {

                    overNightSpinner.setVisibility(View.GONE);
                }
                return null;
            }
        });

        ActivityCostAdapter activityCostAdapter = new ActivityCostAdapter(this, mPlace.get(position).activities);
        ListView activitiesListView = mView.findViewById(R.id.activities_list_view);
        activitiesListView.setAdapter(activityCostAdapter);
        Button applyBtn = mView.findViewById(R.id.apply_cost_dialog_btn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Integer KEY : ActivityCostAdapter.map.keySet()) {
                    boolean val = ActivityCostAdapter.map.get(KEY);
                    ActivityCostAdapter.SetCheckedActivities(KEY, val);
                }
                int activity_cost = 0;
                for (Integer KEY : ActivityCostAdapter.cost_map.keySet()) {
                    activity_cost += ActivityCostAdapter.cost_map.get(KEY);
                }
                int act_time = 0;
                for (Integer KEY : ActivityCostAdapter.time_map.keySet()) {
                    act_time += ActivityCostAdapter.time_map.get(KEY);
                }
                prefs.setintPrefs("act_time", act_time);
                setEstimatedTime(transportationCheckbox.isChecked() ? mPlace.get(position).estimatedTime + act_time : act_time);
                alertDialog.dismiss();
                prefs.setboolPrefs("trans", transportationCheckbox.isChecked());
                prefs.setboolPrefs("food", foodCheckbox.isChecked());
                prefs.setboolPrefs("enterence", entrenceFeesCheckbox.isChecked());
                prefs.setintPrefs("sleep", index);
                prefs.setboolPrefs("sleep_checkbox", sleepCheckBox.isChecked());

                if (entrenceFeesCheckbox.isChecked()) {
                    totalCost += mPlace.get(position).cost.entranceFees;
                }
                if (foodCheckbox.isChecked()) {
                    totalCost += mPlace.get(position).cost.food;
                }
                if (transportationCheckbox.isChecked()) {
                    totalCost += mPlace.get(position).cost.transportation;
                }

                if (sleepCheckBox.isChecked()) {
                    totalCost += mPlace.get(position).cost.overNightStay.get(overNightSpinner.getSelection());
                }
                totalCost += activity_cost;
                //ActivityCostAdapter.totalcost=0;
                prefs.setintPrefs("total_cost", totalCost);
                setCostProgress(totalCost);

            }
        });

        Button canelBtn = mView.findViewById(R.id.cancel_cost_dialog_btn);
        canelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void temp_btn(View view) {

        Button cancel_temp_dialog_btn;
        TextView min_temp, max_temp, humidity_tv, wind_tv;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.temperature_popup, null);
        builder.setView(mView);
        cancel_temp_dialog_btn = mView.findViewById(R.id.cancel_temp_dialog);
        max_temp = mView.findViewById(R.id.max_temp);
        min_temp = mView.findViewById(R.id.min_temp);
        humidity_tv = mView.findViewById(R.id.humidity_tv);
        wind_tv = mView.findViewById(R.id.wind_tv);
        if (tempView.getParent() != null) {
            ((ViewGroup) tempView.getParent()).removeView(tempView);
        }
        builder.setView(tempView);
        max_temp.setText((int) max + "");
        min_temp.setText((int) min + "");
        humidity_tv.setText(humidity + "");
        wind_tv.setText((int) speed + "");
        alertDialog = builder.create();
        alertDialog.show();
        cancel_temp_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void quality_btn(View view) {
        Button cancel_airquality_dialog_btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.airquality_popup, null);
        builder.setView(mView);
        cancel_airquality_dialog_btn = mView.findViewById(R.id.cancel_airquality_dialog);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_airquality_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    String place_id(String name) {
        name = name.replaceAll("\\s+", "");
        return name.toLowerCase();
    }

    public void internet_btn(View view) {
        Button cancel_internet_dialog_btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.internet_popup, null);
        builder.setView(mView);
        cancel_internet_dialog_btn = mView.findViewById(R.id.cancel_internet_dialog);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_internet_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    private void getTempApi(double latitude, double longitude) {
        Call<JsonObject> call = openWeatherApi.getTemp(latitude, longitude, APIKEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    return;
                }
                JsonObject root = response.body();
                JsonObject main = root.getAsJsonObject("main");
                JsonElement element_temp = main.get("temp");
                JsonElement element_min_temp = main.get("temp_min");
                JsonElement element_max_temp = main.get("temp_max");
                JsonElement element_humidity = main.get("humidity");

                JsonObject wind = root.getAsJsonObject("wind");
                JsonElement element_speed = wind.get("speed");

                tempApiResult = element_temp.getAsDouble() - 273.15;
                min = element_min_temp.getAsDouble() - 273.15;
                max = element_max_temp.getAsDouble() - 273.15;
                humidity = element_humidity.getAsInt();
                speed = element_speed.getAsDouble() * 3.6;
                setTempProgress((int) tempApiResult);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    // send Lat and Lng to PlacesActivity.
    public void place_btn(View view) {
        Intent intent = new Intent(DetailedActivity.this, PlacesActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }

    // update Lat and Lng for each place.
    public void updateLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    public void pageSelectedPlace(int position) {
        Parent_Key = Tour_id + place_id(mPlace.get(position).nameEN);
        prefs = new Sharedpreference(DetailedActivity.this, Parent_Key);
        int deff = mPlace.get(position).cost.entranceFees + mPlace.get(position).cost.food + mPlace.get(position).cost.transportation + mPlace.get(position).cost.overNightStay.get(1);
        int total = prefs.getintPrefs("total_cost", deff);
        setCostProgress(total);
        setSeason(mPlace.get(position).recommendedSeason);
        getTempApi(mPlace.get(position).latitude, mPlace.get(position).longitude);
        setAirQualityProgress(mPlace.get(position).airQuality);
        setInternetProgress(mPlace.get(position).internet);
        setTimeToGo(mPlace.get(position).recommendedTime);
        setAge(mPlace.get(position).recommendedAge);
        setEstimatedTime(mPlace.get(position).estimatedTime + prefs.getintPrefs("act_time", 0));
        mp = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(mPlace.get(position).voiceURL, "raw", getPackageName()));
        if (SplashScreenActivity.lan.equalsIgnoreCase("zh")) {
            description.setText(mPlace.get(position).descZH);
            placeNameRecommendations.setText(mPlace.get(position).nameZH);
            placeNameInfo.setText(mPlace.get(position).nameZH);
            placeNameLocation.setText(mPlace.get(position).nameZH);
            placeNameTitle.setText(mPlace.get(position).nameZH);
        } else {
            description.setText(mPlace.get(position).descEN);
            placeNameRecommendations.setText(mPlace.get(position).nameEN);
            placeNameInfo.setText(mPlace.get(position).nameEN);
            placeNameLocation.setText(mPlace.get(position).nameEN);
            placeNameTitle.setText(mPlace.get(position).nameEN);
        }

        latitude = mPlace.get(position).latitude;
        longitude = mPlace.get(position).longitude;
        placeName = mPlace.get(position).nameEN;
        mapFragment.getMapAsync(DetailedActivity.this);

        scrollView.fullScroll(View.FOCUS_UP);
        description.scrollTo(0, 0);
    }

    private void run_viewPager() {
        imageUrls = new ArrayList<>();

        for (int i = 0; i < mPlace.size(); i++) {
            imageUrls.add(mPlace.get(i).imageURL);
        }

        adapter = new ViewPagerAdapter(imageUrls, this);
        viewPager = findViewById(R.id.ViewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(100, 0, 100, 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                Parent_Key = Tour_id + place_id(mPlace.get(position).nameEN);
                prefs = new Sharedpreference(DetailedActivity.this, Parent_Key);
                int deff = mPlace.get(position).cost.entranceFees + mPlace.get(position).cost.food + mPlace.get(position).cost.transportation + mPlace.get(position).cost.overNightStay.get(1);
                int total = prefs.getintPrefs("total_cost", deff);
                setCostProgress(total);
                setSeason(mPlace.get(position).recommendedSeason);
                getTempApi(mPlace.get(position).latitude, mPlace.get(position).longitude);
                updateLocation(mPlace.get(position).latitude, mPlace.get(position).longitude);
                setAirQualityProgress(mPlace.get(position).airQuality);
                setInternetProgress(mPlace.get(position).internet);
                setTimeToGo(mPlace.get(position).recommendedTime);
                setAge(mPlace.get(position).recommendedAge);
                setEstimatedTime(mPlace.get(position).estimatedTime + prefs.getintPrefs("act_time", 0));
                mp = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(mPlace.get(position).voiceURL, "raw", getPackageName()));
                if (SplashScreenActivity.lan.equalsIgnoreCase("zh")) {
                    description.setText(mPlace.get(position).descZH);
                    placeNameRecommendations.setText(mPlace.get(position).nameZH);
                    placeNameInfo.setText(mPlace.get(position).nameZH);
                    placeNameLocation.setText(mPlace.get(position).nameZH);
                    placeNameTitle.setText(mPlace.get(position).nameZH);
                } else {
                    description.setText(mPlace.get(position).descEN);
                    placeNameRecommendations.setText(mPlace.get(position).nameEN);
                    placeNameInfo.setText(mPlace.get(position).nameEN);
                    placeNameLocation.setText(mPlace.get(position).nameEN);
                    placeNameTitle.setText(mPlace.get(position).nameEN);
                }

                latitude = mPlace.get(position).latitude;
                longitude = mPlace.get(position).longitude;
                placeName = mPlace.get(position).nameEN;
                mapFragment.getMapAsync(DetailedActivity.this);

                scrollView.fullScroll(View.FOCUS_UP);
                description.scrollTo(0, 0);

                pageSelectedPlace(position);
                costDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailedCost(position);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println(state);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng googleMapPlace = new LatLng(latitude, longitude);
        MarkerOptions my_own_marker = new MarkerOptions().position(googleMapPlace).title(placeName);
        my_own_marker.icon((getBitmapDescriptor(R.drawable.ic_pin)));
        mMap.addMarker(my_own_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googleMapPlace, 16.0f));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        //mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private BitmapDescriptor getBitmapDescriptor(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setCostProgress(int cost) {

        costTv.setText(cost + " " + getResources().getString(R.string.EU));
        costProgressBar.setProgress(costProgressBar.getMax() - cost);

        if (cost <= 100) {
            costProgressBar.setProgressColor(Color.GREEN);
        } else if (cost > 100 && cost <= 200) {
            costProgressBar.setProgressColor(Color.rgb(255, 165, 0));
        } else if (cost > 200 && cost <= 250) {
            costProgressBar.setProgressColor(Color.RED);

        } else {
            costProgressBar.setProgress(10);
            costProgressBar.setProgressColor(Color.RED);
        }

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(costProgressBar, "progress", 0.0f, costProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();

    }

    private void setTempProgress(int temp) {

        if (temp <= 10) {
            tempProgressBar.setProgressColor(Color.RED);
            tempProgressBar.setProgress(temp);
            tempOverallTv.setText(getResources().getString(R.string.cold));
            tempTv.setText(temp + "");

        } else if (temp > 10 && temp <= 18) {
            tempProgressBar.setProgress(temp);
            tempProgressBar.setProgressColor(Color.rgb(255, 165, 0));
            tempOverallTv.setText(getResources().getString(R.string.normal));
            tempTv.setText(temp + "");
        } else if (temp > 18 && temp <= 30) {
            tempProgressBar.setProgressColor(Color.GREEN);
            tempProgressBar.setProgress(55);
            tempOverallTv.setText(getResources().getString(R.string.perfect));
            tempTv.setText(temp + "");
        } else {
            tempProgressBar.setProgressColor(Color.RED);
            tempProgressBar.setProgress(55 - temp);
            tempOverallTv.setText(getResources().getString(R.string.hot));
            tempTv.setText(temp + "");
        }

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(tempProgressBar, "progress", 0.0f, tempProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();

    }

    private void setAirQualityProgress(int airQuality) {
        airQualityTv.setText(airQuality + " " + "\u00B5" + "g/m3");

        airQualityProgressBar.setProgress(100 - airQuality);
        if (airQuality <= 25) {
            airQualityProgressBar.setProgressColor(Color.GREEN);
        } else if (airQuality > 25 && airQuality <= 50) {
            airQualityProgressBar.setProgressColor(Color.rgb(255, 165, 0));
        } else {
            airQualityProgressBar.setProgressColor(Color.RED);

        }

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(airQualityProgressBar, "progress", 0.0f, airQualityProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();
    }

    private void setInternetProgress(int internet) {
        ObjectAnimator progressAnimator;

        switch (internet) {
            case 0:
                internetProgressBar.setProgressColor(Color.RED);
                internetProgressBar.setProgress(25);
                internetTv.setText(getResources().getString(R.string.bad_internet));
                break;

            case 1:
                internetProgressBar.setProgressColor(Color.rgb(255, 165, 0));
                internetProgressBar.setProgress(50);
                internetTv.setText(getResources().getString(R.string.okay_internet));
                break;
            case 2:
                internetProgressBar.setProgressColor(Color.rgb(255, 215, 0));
                internetProgressBar.setProgress(75);
                internetTv.setText(getResources().getString(R.string.good_internet));
                break;
            case 3:
                internetProgressBar.setProgressColor(Color.GREEN);
                internetProgressBar.setProgress(100);
                internetTv.setText(getResources().getString(R.string.great_internet));
                break;

        }

        progressAnimator = ObjectAnimator.ofFloat(internetProgressBar, "progress", 0.0f, internetProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();
    }

    private void setSeason(int season) {
        changeCameraDistance(frontLayoutSeason, backLayoutSeason);
        switch (season) {
            case 0:
                seasonTv.setText(getResources().getString(R.string.summer_season));
                seasonImg.setImageResource(R.drawable.ic_summer);
                break;

            case 1:
                seasonTv.setText(getResources().getString(R.string.winter_season));
                seasonImg.setImageResource(R.drawable.ic_winter);
                break;

            case 2:
                seasonTv.setText(getResources().getString(R.string.spring_season));
                seasonImg.setImageResource(R.drawable.ic_spring);
                break;
            case 3:
                seasonTv.setText(getResources().getString(R.string.autumn_season));
                seasonImg.setImageResource(R.drawable.ic_autumn);
                break;
        }
    }

    private void setTimeToGo(int time) {

        changeCameraDistance(frontLayoutTime, backLayouTime);


        switch (time) {
            case 0:
                timeToGoTv.setText(getResources().getString(R.string.day_time));
                break;

            case 1:
                timeToGoTv.setText(getResources().getString(R.string.night_time));
                break;
        }
        timeToGoImg.setImageResource(R.drawable.ic_day_and_night);
    }

    private void setAge(String age) {

        ageTv2.setTextSize(20);
        ageTv1.setTextSize(20);
        ageTv1.setText(age);
        ageTv2.setText(age);

        changeCameraDistance(frontLayoutAge, backLayoutAge);

    }

    private void setEstimatedTime(int estimatedTime) {

        estimationTv.setText(estimatedTime + "Hrs");
        estimationImg.setImageResource(R.drawable.ic_stopwatch);

        changeCameraDistance(frontLayoutEstimated, backLayoutEstimated);

    }

    public void flipCard(View front, View back, boolean mIsBackVisible[], int position) {
        if (!mIsBackVisible[position]) {
            mSetRightOut.setTarget(front);
            mSetLeftIn.setTarget(back);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible[position] = true;
        } else {
            mSetRightOut.setTarget(back);
            mSetLeftIn.setTarget(front);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible[position] = false;
        }
    }

    private void changeCameraDistance(View front, View back) {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        front.setCameraDistance(scale);
        back.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    // calculate the cost of transportation using the yellow taxi day charging rate
    private int calculateTransportationCost(int distance, int duration) {
        int cost = 250; // starter fee in fils
        cost += (distance / 100) * 24; // 24 fils per 100 meters
        cost += duration * 30; // 30 fils per 1 minute
        System.out.println(cost);

        return cost / 1000; // return cost in EU
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cost_details_btn:
                showDetailedCost(0);
                break;

            case R.id.text_to_speech:
                mp.start();
                break;

            case R.id.apply_cost_dialog_btn:
                for (Integer KEY : ActivityCostAdapter.map.keySet()) {
                    boolean val = ActivityCostAdapter.map.get(KEY);
                    ActivityCostAdapter.SetCheckedActivities(KEY, val);
                }
                int activity_cost = 0;
                for (Integer KEY : ActivityCostAdapter.cost_map.keySet()) {
                    activity_cost += ActivityCostAdapter.cost_map.get(KEY);
                }
                int act_time = 0;
                for (Integer KEY : ActivityCostAdapter.time_map.keySet()) {
                    act_time += ActivityCostAdapter.time_map.get(KEY);
                }
                prefs.setintPrefs("act_time", act_time);
                setEstimatedTime(transportationCheckbox.isChecked() ? mPlace.get(PublicPosition).estimatedTime + act_time : act_time);
                alertDialog.dismiss();
                prefs.setboolPrefs("trans", transportationCheckbox.isChecked());
                prefs.setboolPrefs("food", foodCheckbox.isChecked());
                prefs.setboolPrefs("enterence", entrenceFeesCheckbox.isChecked());
                prefs.setintPrefs("sleep", index);
                prefs.setboolPrefs("sleep_checkbox", sleepCheckBox.isChecked());

                if (entrenceFeesCheckbox.isChecked()) {
                    totalCost += mPlace.get(PublicPosition).cost.entranceFees;
                }
                if (foodCheckbox.isChecked()) {
                    totalCost += mPlace.get(PublicPosition).cost.food;
                }
                if (transportationCheckbox.isChecked()) {
                    totalCost += mPlace.get(PublicPosition).cost.transportation;
                }

                if (sleepCheckBox.isChecked()) {
                    totalCost += mPlace.get(PublicPosition).cost.overNightStay.get(overNightSpinner.getSelection());
                }
                totalCost += activity_cost;
                //ActivityCostAdapter.totalcost=0;
                prefs.setintPrefs("total_cost", totalCost);
                setCostProgress(totalCost);
                break;

            case R.id.cancel_cost_dialog_btn:
            case R.id.cancel_temp_dialog:
            case R.id.cancel_airquality_dialog:
                alertDialog.dismiss();
                break;
            case R.id.cancel_internet_dialog:
                alertDialog.cancel();
                break;
            case R.id.zoomin:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.zoomout:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.season_btn:
                flipCard(frontLayoutSeason, backLayoutSeason, mIsBackVisible, 0);
                break;
            case R.id.time_btn:
                flipCard(frontLayoutTime, backLayouTime, mIsBackVisible, 1);
                break;
            case R.id.age_btn:
                flipCard(frontLayoutAge, backLayoutAge, mIsBackVisible, 2);
                break;
            case R.id.estimated_btn:
                flipCard(frontLayoutEstimated, backLayoutEstimated, mIsBackVisible, 3);
                break;

        }
    }


    public void fillView() {
        costView = getLayoutInflater().inflate(R.layout.cost_popup, null);
        tempView = getLayoutInflater().inflate(R.layout.temperature_popup, null);
        qualityView = getLayoutInflater().inflate(R.layout.airquality_popup, null);
        internetView = getLayoutInflater().inflate(R.layout.internet_popup, null);

        foodCheckbox = costView.findViewById(R.id.food_checkBox);
        entrenceFeesCheckbox = costView.findViewById(R.id.entrance_fees_checkBox);
        transportationCheckbox = costView.findViewById(R.id.transportation_checkBox);
        sleepCheckBox = costView.findViewById(R.id.sleep_checkBox);
        entrenceFeesPrice = costView.findViewById(R.id.entrance_fees_price);
        foodPrice = costView.findViewById(R.id.food_price);
        transportationPrice = costView.findViewById(R.id.transportation_price);
        overNightSpinner = costView.findViewById(R.id.over_night_spinner);
        activitiesListView = costView.findViewById(R.id.activities_list_view);
        applyBtn = costView.findViewById(R.id.apply_cost_dialog_btn);
        applyBtn.setOnClickListener(this);
        cancelBtn = costView.findViewById(R.id.cancel_cost_dialog_btn);
        cancelBtn.setOnClickListener(this);
        cancel_temp_dialog_btn = tempView.findViewById(R.id.cancel_temp_dialog);
        cancel_temp_dialog_btn.setOnClickListener(this);
        max_temp = tempView.findViewById(R.id.max_temp);
        min_temp = tempView.findViewById(R.id.min_temp);
        humidity_tv = tempView.findViewById(R.id.humidity_tv);
        wind_tv = tempView.findViewById(R.id.wind_tv);
        cancel_airquality_dialog_btn = qualityView.findViewById(R.id.cancel_airquality_dialog);
        cancel_airquality_dialog_btn.setOnClickListener(this);
        cancel_temp_dialog_btn.setOnClickListener(this);
        mToolbar = findViewById(R.id.detailed_toolbar);
        mTextView = findViewById(R.id.toolbar_title);
        placeNameRecommendations = findViewById(R.id.place_name_recommendations);
        placeNameInfo = findViewById(R.id.place_name_information_about);
        placeNameLocation = findViewById(R.id.place_name_location);
        zoom_in = findViewById(R.id.zoomin);
        zoom_out = findViewById(R.id.zoomout);
        zoom_in.setOnClickListener(this);
        zoom_out.setOnClickListener(this);
        description = findViewById(R.id.description_tv);
        description.setOnTouchListener(this);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setOnTouchListener(this);
        placeNameTitle = findViewById(R.id.place_name_title);
        cancel_internet_dialog_btn = internetView.findViewById(R.id.cancel_internet_dialog);
        cancel_internet_dialog_btn.setOnClickListener(this);
        viewPager = findViewById(R.id.ViewPager);
        frontLayoutTime = findViewById(R.id.front_time);
        backLayouTime = findViewById(R.id.back_time);
        timeToGoTv = backLayouTime.findViewById(R.id.back_text);
        timeToGoImg = frontLayoutTime.findViewById(R.id.front_icon);
        frontLayoutAge = findViewById(R.id.front_ag);
        backLayoutAge = findViewById(R.id.back_age);
        ageTv1 = backLayoutAge.findViewById(R.id.back_text);
        ageTv2 = frontLayoutAge.findViewById(R.id.back_text);
        frontLayoutEstimated = findViewById(R.id.front_estimated);
        backLayoutEstimated = findViewById(R.id.back_estimated);
        estimationTv = backLayoutEstimated.findViewById(R.id.back_text);
        estimationImg = frontLayoutEstimated.findViewById(R.id.front_icon);


        //information about the place deceleration
        costProgressBar = findViewById(R.id.cost_progress);
        tempProgressBar = findViewById(R.id.temp_progress);
        airQualityProgressBar = findViewById(R.id.air_quality_progress);
        internetProgressBar = findViewById(R.id.internet_progress);
        costTv = findViewById(R.id.cost_tv);
        tempTv = findViewById(R.id.temp_tv);
        tempOverallTv = findViewById(R.id.temp_overall_tv);
        airQualityTv = findViewById(R.id.air_quality_tv);
        internetTv = findViewById(R.id.internet_tv);

        //recommendations about the place deceleration
        seasonFlip = findViewById(R.id.season_btn);
        seasonFlip.setOnClickListener(this);
        frontLayoutSeason = seasonFlip.findViewById(R.id.front_season);
        backLayoutSeason = seasonFlip.findViewById(R.id.back_season);
        seasonTv = backLayoutSeason.findViewById(R.id.back_text);
        seasonImg = frontLayoutSeason.findViewById(R.id.front_icon);
        timeToGoFlip = findViewById(R.id.time_btn);
        timeToGoFlip.setOnClickListener(this);
        ageFlip = findViewById(R.id.age_btn);
        ageFlip.setOnClickListener(this);
        estimationFlip = findViewById(R.id.estimated_btn);
        estimationFlip.setOnClickListener(this);

        //progress details deceleration
        costDetails = findViewById(R.id.cost_details_btn);
        costDetails.setOnClickListener(this);
        text_to_speech = findViewById(R.id.text_to_speech);
        text_to_speech.setOnClickListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //clarification_dialog
        prefs = new Sharedpreference(this, Parent_Key);
        if (!prefs.getboolPrefs("clarification_dialog", false)) {
            showClarificationDialog();
        }

        nearbyTv = findViewById(R.id.nearby_tv);
        if(nearbyTv.getText().toString().length()!=0){
            nearbyTv.setText(getResources().getString(R.string.nearby)+" ");
        }
    }

    public void ActionBarChecker() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mTextView.setText(R.string.detailed_activity_title);
    }

    public void ApiConfig() {
        //retrofit config
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherApi = retrofit.create(OpenWeatherApi.class);
    }

    public void setFirstPlaceDetails() {

        Intent i = getIntent();
        mPlace = (List<Place>) i.getSerializableExtra("places");
        Tour_id = i.getStringExtra("tour_id");
        Parent_Key = Tour_id + place_id(mPlace.get(0).nameEN);
        description.setMovementMethod(new ScrollingMovementMethod());
        latitude = mPlace.get(0).latitude;
        longitude = mPlace.get(0).longitude;
        placeName = mPlace.get(0).nameEN;
        map_view = mapFragment.getView();
        mapFragment.getMapAsync(this);
        run_viewPager();
        int deff = mPlace.get(0).cost.entranceFees + mPlace.get(0).cost.food + mPlace.get(0).cost.transportation + mPlace.get(0).cost.overNightStay.get(1);
        getTempApi(mPlace.get(0).latitude, mPlace.get(0).longitude);
        setAirQualityProgress(mPlace.get(0).airQuality);
        setInternetProgress(mPlace.get(0).internet);
        setSeason(mPlace.get(0).recommendedSeason);
        setAge(mPlace.get(0).recommendedAge);
        setEstimatedTime(mPlace.get(0).estimatedTime + prefs.getintPrefs("act_time", 0));
        setCostProgress(prefs.getintPrefs("total_cost", deff));
        setTimeToGo(mPlace.get(0).recommendedTime);

        try {
            mp = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(mPlace.get(0).voiceURL, "raw", getPackageName()));
        } catch (Exception e) {
            Log.d(TAG, "" + mPlace.get(0).nameEN + " \n" + e);
        }

        if (SplashScreenActivity.lan.equalsIgnoreCase("zh")) {
            description.setText(mPlace.get(0).descZH);
            placeNameRecommendations.setText(mPlace.get(0).nameZH);
            placeNameInfo.setText(mPlace.get(0).nameZH);
            placeNameLocation.setText(mPlace.get(0).nameZH);
            placeNameTitle.setText(mPlace.get(0).nameZH);
        } else {
            placeNameTitle.setText(mPlace.get(0).nameEN);
            description.setText(mPlace.get(0).descEN);
            placeNameRecommendations.setText(mPlace.get(0).nameEN);
            placeNameInfo.setText(mPlace.get(0).nameEN);
            placeNameLocation.setText(mPlace.get(0).nameEN);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.scrollview:
                description.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case R.id.description_tv:
                description.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return false;
    }


}