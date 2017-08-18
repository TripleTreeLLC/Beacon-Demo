package com.example.paulburton.beacondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();

        placesByBeacons.put("59979:65037", new ArrayList<String>() {{
            add("Kitchen");
        }});
        /*
        placesByBeacons.put("31538:18865", new ArrayList<String>() {{
            add("Bay Area");
        }});
        */
        placesByBeacons.put("65053:20370", new ArrayList<String>() {{
            add("Parlor");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    TextView nearestRoom;
    TextSwitcher placeDescription;
    ImageView placeImage;
    ImageView contextImage;
    TextView placeContent;
    TextView placeDetails;
    TextView title;
    RelativeLayout header;
    RelativeLayout subHeader;

    String prevPlace = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //  To get your AppId and AppToken you need to create new application in Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), "paul-tripletreellc-com-s-p-7sa", "dfc5a042c326172a7684b4d87d87a476");
// Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        placeDescription = (TextSwitcher) findViewById(R.id.placeDescription);
        placeContent = (TextView) findViewById(R.id.placeContent);
        placeImage = (ImageView) findViewById(R.id.placeImage);
        contextImage = (ImageView) findViewById(R.id.contextImage);
        placeDetails = (TextView) findViewById(R.id.placeDetails);
        title = (TextView) findViewById(R.id.title);
        header = (RelativeLayout) findViewById(R.id.header);
        subHeader = (RelativeLayout) findViewById(R.id.sub);

// specify the in/out animations you wish to use

        placeDescription.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView () {
                Typeface face = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
                TextView t = new TextView(getApplicationContext());
                t.setTypeface(face);
                t.setTextColor(Color.parseColor("#ffffff"));
                t.setTextSize(20);
                return t;
            }
        });

        placeDescription.setInAnimation(this, R.anim.slide_in_left);
        placeDescription.setOutAnimation(this, R.anim.slide_in_right);
        placeDescription.setText("Red Brain Media");

        //provide two TextViews for the TextSwitcher to use
        //you can apply styles to these Views before adding
        //textSwitcher.addView(placeDetails);

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            }
        }

        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    // TODO: update the UI here
                    //Log.d("Airport", "Nearest places: " + places);

                    if (places.size() > 0 && !prevPlace.equals(places.get(0))) {
                        placeDescription.setText(places.get(0));
                        prevPlace = places.get(0);

                        if (places.get(0).equals("Kitchen")) {
                            title.setText("Dinner Time");
                            header.setBackgroundColor(Color.parseColor("#283593"));
                            subHeader.setBackgroundColor(Color.parseColor("#303F9F"));
                            title.setTextColor(Color.parseColor("#304FFE"));
                            contextImage.setImageResource(R.drawable.pizza);
                            placeDetails.setText("Cooking Things");
                            placeContent.setText("Hungry eh? Come on in. " +
                                    "Here you can do things like cook food and brew coffee. " +
                                    "Make sure to clean up after yourself :)");
                            placeImage.setImageResource(R.drawable.kitchen);
                        }

                        if (places.get(0).equals("Bay Area")) {
                            title.setText("It's Bayonce Bitch");
                            header.setBackgroundColor(Color.parseColor("#00695C"));
                            subHeader.setBackgroundColor(Color.parseColor("#00796B"));
                            title.setTextColor(Color.parseColor("#00BFA5"));
                            contextImage.setImageResource(R.drawable.city);
                            placeDetails.setText("Triple Tree, Orbital Content");
                            placeContent.setText("Welcome to the Bay Area! " +
                                    "It's where Triple Tree and Orbital " +
                                    "collide. Don't enter if you're not ready to hustle.");
                            placeImage.setImageResource(R.drawable.sanfran);
                        }

                        if (places.get(0).equals("Parlor")) {
                            title.setText("The Dungeon");
                            header.setBackgroundColor(Color.parseColor("#4527A0"));
                            subHeader.setBackgroundColor(Color.parseColor("#512DA8"));
                            title.setTextColor(Color.parseColor("#6200EA"));
                            contextImage.setImageResource(R.drawable.drink);
                            placeDetails.setText("Drinks, bowls and waterfalls");
                            placeContent.setText("The parlor is pretty cool. " +
                                    "There are a bunch of fun lights and stuff, " +
                                    "just be careful of the alligator.");
                            placeImage.setImageResource(R.drawable.parlor);
                        }
                    }
                }
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Gimme")
                        .setPositiveButton("Do it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

}
