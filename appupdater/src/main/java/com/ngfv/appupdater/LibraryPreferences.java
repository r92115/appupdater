package com.ngfv.appupdater;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Rajeev on 21/9/16.
 */
class LibraryPreferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String KeyAppUpdaterShow = "prefAppUpdaterShow";
    private static final String KeySuccessfulChecks = "prefSuccessfulChecks";

    LibraryPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    boolean getAppUpdaterShow() {
        return sharedPreferences.getBoolean(KeyAppUpdaterShow, true);
    }

    int getSuccessfulChecks() {
        return sharedPreferences.getInt(KeySuccessfulChecks, 0);
    }

    void setSuccessfulChecks(Integer checks) {
        editor.putInt(KeySuccessfulChecks, checks);
        editor.commit();
    }
}
