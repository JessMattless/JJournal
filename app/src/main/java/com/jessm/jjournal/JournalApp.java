package com.jessm.jjournal;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//TODO: Fix the issue with some apps not having accurate readings, even though everything works perfect for some apps
//      every now and then the numbers between the week/day readings won't add up

public class JournalApp extends Application {

    public static List<App> appList;

    static UsageStatsManager usageStatsManager;

    public static List<JournalEntry> journalList;

    /**
     * Listed as: <"AppName", <1, 30><2, 40>>
     *     The first number is how many days ago and the second is how many minutes
     */
    public static Map<String, Map<Integer, Long>> dailyUsageList;

    static long DAY_IN_MILLIS = 86400000;

    public static boolean PERMISSIONS = false;

    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        dailyUsageList = new HashMap<>();
        journalList = new ArrayList<>();
        generateAppList();
    }

    public void generateAppList() {

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_WEEKLY,
                System.currentTimeMillis() - (DAY_IN_MILLIS * 7),
                System.currentTimeMillis());

        if (!usageStatsList.isEmpty()) {
            PackageManager packageManager = getPackageManager();
            appList = new ArrayList<>();

            long APP_MINIMUM_TIME = 5;
            for (UsageStats packageStats : usageStatsList) {
                try {
                    ApplicationInfo appInfo = packageManager.getApplicationInfo(packageStats.getPackageName(), 0);

                    long appUsage = TimeUnit.MILLISECONDS.toMinutes(packageStats.getTotalTimeInForeground());
                    if (appUsage < APP_MINIMUM_TIME) continue;

                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                    List<ResolveInfo> resolvedInfoList = packageManager.queryIntentActivities(mainIntent, 0);

                    String appName = appInfo.loadLabel(packageManager).toString();
                    if (resolvedInfoList.stream().noneMatch(item -> item.activityInfo.packageName.equals(packageStats.getPackageName()))) {
                        continue;
                    }

                    Drawable appImage = appInfo.loadIcon(packageManager);
                    Bitmap bitmap = drawableToBitmap(appImage);

                    boolean exists = false;
                    for (App listApp : appList) {
                        if (listApp.getName().equals(appName)) {
                            exists = true;
                            listApp.setUsage(listApp.getUsage() + appUsage);
                            break;
                        }
                    }
                    if (exists) continue;

                    App app = new App(appName, appUsage, bitmap);
                    appList.add(app);

                } catch (PackageManager.NameNotFoundException ignored) { }
            }

            // From today > 7 days ago
            for (int i = 0; i < 7 ; i++) {
                List<UsageStats> usageStatsDay = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        System.currentTimeMillis() - (DAY_IN_MILLIS * (i + 1)),
                        System.currentTimeMillis() - (DAY_IN_MILLIS * i));

                for (UsageStats packageStats : usageStatsDay) {
                    try {
                        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageStats.getPackageName(), 0);

                        String appName = appInfo.loadLabel(packageManager).toString();
                        long appUsage = TimeUnit.MILLISECONDS.toMinutes(packageStats.getTotalTimeInForeground());


                        if (appList.stream().noneMatch(item -> item.getName().equals(appName))) continue;

                        if (appUsage > APP_MINIMUM_TIME) {
                            if (dailyUsageList.containsKey(appName)) {
                                Map<Integer, Long> usage = dailyUsageList.get(appName);
                                if (usage != null && usage.containsKey(i)) {
                                    usage.put(i, usage.get(i) + appUsage);
                                }
                            }
                            else {
                                Map<Integer, Long> innerMap = new HashMap<>();
                                innerMap.put(i, appUsage);

                                dailyUsageList.put(appName, innerMap);
                            }
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            appList.sort((app, other) -> {
                if (app.getUsage() == other.getUsage()) return 0;
                return app.getUsage() < other.getUsage() ? -1 : 1;
            });
            Collections.reverse(appList);

        }
    }

    Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        int iconWidth = drawable.getIntrinsicWidth();
        int iconHeight = drawable.getIntrinsicHeight();
        if (drawable instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable icon = (AdaptiveIconDrawable)drawable;

            Drawable foreground = icon.getForeground();
            Drawable background = icon.getBackground();

            bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            background.setBounds(0, 0, iconWidth, iconHeight);
            background.draw(canvas);
            foreground.setBounds(0, 0, iconWidth, iconHeight);
            foreground.draw(canvas);

            Bitmap maskBitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);

            Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            xferPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            xferPaint.setColor(Color.RED);
            maskCanvas.drawRoundRect(0,0,iconWidth, iconHeight, 40, 40, xferPaint);

            xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(maskBitmap, 0, 0, xferPaint);
        }
        else if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;

            bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
        }
        else if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            bitmap = bitmapDrawable.getBitmap();
        }
        else {
            bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        }

        return bitmap;
    }

    public static boolean permissionsGranted() {
        Calendar usageCalendar = Calendar.getInstance();
        usageCalendar.add(Calendar.DAY_OF_YEAR, -7);

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - 604800000, System.currentTimeMillis());
        PERMISSIONS = !usageStatsList.isEmpty();
        return PERMISSIONS;
    }

}
