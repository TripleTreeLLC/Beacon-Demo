package com.example.paulburton.beacondemo;

/**
 * Created by paulburton on 4/21/17.
 */

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.connection.internal.protocols.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyApplication extends Application {

    private BeaconManager beaconManager;

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();

        placesByBeacons.put("59979:65037", new ArrayList<String>() {{
            add("Kitchen");
        }});

        placesByBeacons.put("31538:18865", new ArrayList<String>() {{
            add("Bay Area");
        }});

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

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Created");

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setBackgroundScanPeriod(1000, 1000);

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                if (region.getIdentifier().equals("Kitchen")) {
                    showNotification(
                            "Pro-tip:",
                            "Make sure to refill the coffee!", R.drawable.pizza);
                    beaconManager = new BeaconManager(getApplicationContext());
                    beaconManager.setBackgroundScanPeriod(1000, 1000);
                    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                        @Override
                        public void onServiceReady() {
                            beaconManager.startMonitoring(new Region(
                                    "Bay Area",
                                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                                    31538, 18865));
                        }
                    });
                }
                if (region.getIdentifier().equals("Bay Area")) {
                    showNotification(
                            "Pro-tip:",
                            "Don't bug Paul", R.drawable.city);
                    beaconManager = new BeaconManager(getApplicationContext());
                    beaconManager.setBackgroundScanPeriod(1000, 1000);
                    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                        @Override
                        public void onServiceReady() {
                            beaconManager.startMonitoring(new Region(
                                    "Kitchen",
                                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                                    59979, 65037));
                        }
                    });
                }
                if (region.getIdentifier().equals("Parlor")) {
                    showNotification(
                            "Pro-tip:",
                            "Grab a bottle from the cabinet and pour one!", R.drawable.drink);
                    beaconManager = new BeaconManager(getApplicationContext());
                    beaconManager.setBackgroundScanPeriod(1000, 1000);
                    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                        @Override
                        public void onServiceReady() {
                            beaconManager.startMonitoring(new Region(
                                    "Kitchen",
                                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                                    59979, 65037));
                        }
                    });
                }
            }
            @Override
            public void onExitedRegion(Region region) {


            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {

                beaconManager.startMonitoring(new Region(
                        "Kitchen",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        59979, 65037));
                beaconManager.startMonitoring(new Region(
                        "Bay Area",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        31538, 18865));
                beaconManager.startMonitoring(new Region(
                        "Parlor",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        65053, 20370));
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String title, String message, int icon) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}