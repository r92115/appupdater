package com.ngfv.appupdater;

import com.ngfv.appupdater.enums.AppUpdaterError;
import com.ngfv.appupdater.enums.Display;
import com.ngfv.appupdater.enums.UpdateFrom;
import com.ngfv.appupdater.objects.Update;

/**
 * @author Rajeev on 21/9/16.
 */
interface IAppUpdater {

    /**
     * Set the type of message used to notify the user when a new update has been found. Default: DIALOG.
     *
     * @param display how the update will be shown
     * @return this
     */
    AppUpdater setDisplay(Display display);

    /**
     * Set the source where the latest update can be found. Default: GOOGLE_PLAY.
     *
     * @param updateFrom source where the latest update is uploaded.
     * @return this
     */
    AppUpdater setUpdateFrom(UpdateFrom updateFrom);

    /**
     * Set a custom title for the dialog when an update is available.
     *
     * @param title for the dialog
     * @return this
     */
    AppUpdater setDialogTitleWhenUpdateAvailable(String title);

    /**
     * Set a custom title for the dialog when an update is available.
     *
     * @param title for the dialog
     * @return this
     */
    AppUpdater setTitleOnUpdateAvailable(String title);

    /**
     * Set a custom description for the dialog when an update is available.
     *
     * @param description for the dialog
     * @return this
     */
    AppUpdater setDialogDescriptionWhenUpdateAvailable(String description);

    /**
     * Set a custom description for the dialog when an update is available.
     *
     * @param description for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateAvailable(String description);

    /**
     * Set a custom description for the dialog when no update is available.
     *
     * @param description resource from the strings xml file for the dialog
     * @return this
     */
    AppUpdater setDialogDescriptionWhenUpdateNotAvailable(String description);

    /**
     * Set a custom description for the dialog when no update is available.
     *
     * @param description for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateNotAvailable(String description);

    /**
     * Set a custom "Update" button text when a new update is available.
     *
     * @param text for the update button
     * @return this
     */
    AppUpdater setDialogButtonUpdate(String text);

    /**
     * Set a custom "Update" button text when a new update is available.
     *
     * @param text for the update button
     * @return this
     */
    AppUpdater setButtonUpdate(String text);

    /**
     * Set a custom "Later" button text when a new update is available.
     *
     * @param text for the later button
     * @return this
     */
    AppUpdater setDialogButtonLater(String text);

    /**
     * Set a custom "Later" button text when a new update is available.
     *
     * @param text for the later button
     * @return this
     */
    AppUpdater setButtonLater(String text);

    /**
     * Execute AppUpdater in background.
     *
     * @return this
     */
    AppUpdater init();

    /**
     * Execute AppUpdater in background.
     */
    void start();

    /**
     * Stops the execution of AppUpdater.
     */
    void stop();

    interface LibraryListener {
        void onSuccess(Update update);

        void onFailed(AppUpdaterError error);
    }
}
