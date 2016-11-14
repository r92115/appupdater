package com.ngfv.appupdater;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.ngfv.appupdater.enums.UpdateFrom;
import com.ngfv.appupdater.objects.Update;
import com.ngfv.appupdater.objects.Version;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Rajeev on 21/9/16.
 */

class UtilsLibrary {
    static String getAppName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }

    private static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    static String getAppInstalledVersion(Context context) {
        String version = "0.0.0.0";

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    static Boolean isUpdateAvailable(String installedVersion, String latestVersion) {
        Boolean res = false;

        if (!installedVersion.equals("0.0.0.0") && !latestVersion.equals("0.0.0.0")) {
            Version installed = new Version(installedVersion);
            Version latest = new Version(latestVersion);
            res = installed.compareTo(latest) < 0;
        }

        return res;
    }

    static Boolean isStringAVersion(String version) {
        return version.matches(".*\\d+.*");
    }

    private static URL getUpdateURL(Context context, UpdateFrom updateFrom) {
        String res;

        switch (updateFrom) {
            default:
                res = String.format(Config.PLAY_STORE_URL, getAppPackageName(context), Locale.getDefault().getLanguage());
                break;
        }

        try {
            return new URL(res);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    static Update getLatestAppVersionHttp(Context context, UpdateFrom updateFrom) {
        Boolean isAvailable = false;
        String source = "";
        OkHttpClient client = new OkHttpClient();
        URL url = getUpdateURL(context, updateFrom);
        Request request = new Request.Builder()
                .url(url)
                .build();
        ResponseBody body = null;

        try {
            Response response = client.newCall(request).execute();
            body = response.body();
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream(), "UTF-8"));
            StringBuilder str = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                switch (updateFrom) {
                    default:
                        if (line.contains(Config.PLAY_STORE_TAG_RELEASE)) {
                            str.append(line);
                            isAvailable = true;
                        }
                        break;
                }
            }

            if (str.length() == 0) {
                Log.e("AppUpdater", "Cannot retrieve latest version. Is it configured properly?");
            }

            response.body().close();
            source = str.toString();
        } catch (FileNotFoundException e) {
            Log.e("AppUpdater", "App wasn't found in the provided source. Is it published?");
        } catch (IOException ignore) {

        } finally {
            if (body != null) {
                body.close();
            }
        }

        final String version = getVersion(updateFrom, isAvailable, source);
        final String recentChanges = getRecentChanges(updateFrom, isAvailable, source);
        final URL updateUrl = getUpdateURL(context, updateFrom);

        return new Update(version, recentChanges, updateUrl);
    }

    private static String getVersion(UpdateFrom updateFrom, Boolean isAvailable, String source) {
        String version = "0.0.0.0";
        if (isAvailable) {
            switch (updateFrom) {
                default:
                    String[] splitPlayStore = source.split(Config.PLAY_STORE_TAG_RELEASE);
                    if (splitPlayStore.length > 1) {
                        splitPlayStore = splitPlayStore[1].split("(<)");
                        version = splitPlayStore[0].trim();
                    }
                    break;
            }
        }
        return version;
    }

    private static String getRecentChanges(UpdateFrom updateFrom, Boolean isAvailable, String source) {
        String recentChanges = "";
        if (isAvailable) {
            switch (updateFrom) {
                default:
                    String[] splitPlayStore = source.split(Config.PLAY_STORE_TAG_CHANGES);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < splitPlayStore.length; i++) {
                        sb.append(splitPlayStore[i].split("(<)")[0]).append("\n");
                    }
                    recentChanges = sb.toString();
                    break;
            }
        }
        return recentChanges;
    }

    private static Intent intentToUpdate(Context context, UpdateFrom updateFrom, URL url) {
        Intent intent;

        if (updateFrom.equals(UpdateFrom.GOOGLE_PLAY)) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppPackageName(context)));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        }

        return intent;
    }

    static void goToUpdate(Context context, UpdateFrom updateFrom, URL url) {
        Intent intent = intentToUpdate(context, updateFrom, url);

        if (updateFrom.equals(UpdateFrom.GOOGLE_PLAY)) {
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                context.startActivity(intent);
            }
        } else {
            context.startActivity(intent);
        }
    }

    static Boolean isAbleToShow(Integer successfulChecks, Integer showEvery) {
        return successfulChecks % showEvery == 0;
    }

    static Boolean isNetworkAvailable(Context context) {
        Boolean res = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                res = networkInfo.isConnected();
            }
        }

        return res;
    }
}
