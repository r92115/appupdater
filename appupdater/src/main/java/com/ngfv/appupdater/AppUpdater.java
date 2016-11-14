package com.ngfv.appupdater;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.ngfv.appupdater.enums.AppUpdaterError;
import com.ngfv.appupdater.enums.Display;
import com.ngfv.appupdater.enums.UpdateFrom;
import com.ngfv.appupdater.objects.Update;

/**
 * @author Rajeev on 21/9/16.
 */
public class AppUpdater implements IAppUpdater {

    private Context context;
    private LibraryPreferences libraryPreferences;
    private Display display;
    private UpdateFrom updateFrom;
    private Integer showEvery;
    private Boolean showAppUpdated;

    // Update available
    private String titleUpdate;
    private String descriptionUpdate;
    private String btnUpdate;
    private String btnLater;

    // Update not available
    private String titleNoUpdate;
    private String descriptionNoUpdate;

    private UtilsAsync.LatestAppVersion latestAppVersion;

    private AlertDialog alertDialog;

    /**
     * default constructor
     * @param context passing context.
     */
    public AppUpdater(Context context) {
        this.context = context;
        this.libraryPreferences = new LibraryPreferences(context);
        this.display = Display.DIALOG;
        this.updateFrom = UpdateFrom.GOOGLE_PLAY;
        this.showEvery = 1;
        this.showAppUpdated = false;

        // dialog
        this.titleUpdate = context.getResources().getString(R.string.app_updater_update_available);
        this.titleNoUpdate = context.getResources().getString(R.string.app_updater_update_not_available);
        this.btnUpdate = context.getResources().getString(R.string.app_updater_btn_update);
        this.btnLater = context.getResources().getString(R.string.app_updater_btn_later);
    }

    @Override
    public AppUpdater setDisplay(Display display) {
        this.display = display;
        return this;
    }

    @Override
    public AppUpdater setUpdateFrom(UpdateFrom updateFrom) {
        this.updateFrom = updateFrom;
        return this;
    }

    @Override
    public AppUpdater setDialogTitleWhenUpdateAvailable(String title) {
        setTitleOnUpdateAvailable(title);
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateAvailable(String title) {
        this.titleUpdate = title;
        return this;
    }

    @Override
    public AppUpdater setDialogDescriptionWhenUpdateAvailable(String description) {
        setContentOnUpdateAvailable(description);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateAvailable(String description) {
        this.descriptionUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setDialogDescriptionWhenUpdateNotAvailable(String description) {
        setContentOnUpdateNotAvailable(description);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateNotAvailable(String description) {
        this.descriptionNoUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setDialogButtonUpdate(String text) {
        setButtonUpdate(text);
        return this;
    }

    @Override
    public AppUpdater setButtonUpdate(String text) {
        this.btnUpdate = text;
        return this;
    }

    @Override
    public AppUpdater setDialogButtonLater(String text) {
        setButtonLater(text);
        return this;
    }

    @Override
    public AppUpdater setButtonLater(String text) {
        this.btnLater = text;
        return this;
    }

    @Override
    public AppUpdater init() {
        start();
        return this;
    }

    @Override
    public void start() {
        latestAppVersion = new UtilsAsync.LatestAppVersion(context,
                false,
                updateFrom,
                new LibraryListener() {
                    @Override
                    public void onSuccess(Update update) {
                        if (UtilsLibrary.isUpdateAvailable(
                                UtilsLibrary.getAppInstalledVersion(context), update.getLatestVersion())) {
                            Integer successfulChecks = libraryPreferences.getSuccessfulChecks();
                            if (UtilsLibrary.isAbleToShow(successfulChecks, showEvery)) {
                                switch (display) {
                                    case DIALOG:
                                        alertDialog = UtilsDisplay.showForceUpdateAvailableDialog(context, titleUpdate, getDescriptionUpdate(context, update, Display.DIALOG), btnUpdate, updateFrom, update.getUrlToDownload());
                                        alertDialog.show();
                                        break;
                                }
                            }
                            libraryPreferences.setSuccessfulChecks(successfulChecks + 1);
                        } else if (showAppUpdated) {
                            switch (display) {
                                case DIALOG:
                                    alertDialog = UtilsDisplay.showUpdateNotAvailableDialog(context, titleNoUpdate, getDescriptionNoUpdate(context));
                                    alertDialog.show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        if (error == AppUpdaterError.UPDATE_VARIES_BY_DEVICE) {
                            Log.e("AppUpdater", "UpdateFrom.GOOGLE_PLAY isn't valid: update varies by device.");
                        }
                    }
                });

        latestAppVersion.execute();
    }

    @Override
    public void stop() {
        if (latestAppVersion != null && !latestAppVersion.isCancelled()) {
            latestAppVersion.cancel(true);
        }
    }

    /**
     * Showing description of update.
     * @param context passing context.
     * @param update passing update
     * @param display passing display method
     * @return description.
     */
    private String getDescriptionUpdate(Context context, Update update, Display display) {
        if (descriptionUpdate == null) {
            switch (display) {
                case DIALOG:
                    if (update.getReleaseNotes() != null && !TextUtils.isEmpty(update.getReleaseNotes())) {
                        return String.format(context.getResources().getString(R.string.app_updater_update_available_description_dialog_before_release_notes), update.getLatestVersion(), update.getReleaseNotes());
                    } else {
                        return String.format(context.getResources().getString(R.string.app_updater_update_available_description_dialog), update.getLatestVersion(), UtilsLibrary.getAppName(context));
                    }
            }
        }
        return descriptionUpdate;
    }

    /**
     * If no update available
     * @param context passing context.
     * @return description from xml.
     */
    private String getDescriptionNoUpdate(Context context) {
        if (descriptionNoUpdate == null) {
            return String.format(context.getResources().getString(R.string.app_updater_update_not_available_description), UtilsLibrary.getAppName(context));
        } else {
            return descriptionNoUpdate;
        }
    }
}
