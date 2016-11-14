package com.ngfv.appupdater;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ngfv.appupdater.enums.UpdateFrom;

import java.net.URL;

/**
 * @author Rajeev on 21/9/16.
 */
class UtilsDisplay {

    /**
     * force update the app from the store.
     * @param context passing context
     * @param title passing dialog title
     * @param content passing content
     * @param btnPositive passing update button
     * @param updateFrom from where have to update
     * @param apk passing apk url.
     * @return dialog.
     */
    static AlertDialog showForceUpdateAvailableDialog(
            final Context context,
            String title,
            String content,
            String btnPositive,
            final UpdateFrom updateFrom,
            final URL apk) {

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UtilsLibrary.goToUpdate(context, updateFrom, apk);
                    }
                }).create();
    }

    /**
     * showing later and update button to the user.
     * @param context passing context
     * @param title passing dialog title
     * @param content passing content
     * @param btnPositive passing update button
     * @param btnNegative passing later button
     * @param updateFrom from where have to update
     * @param apk passing apk url.
     * @return dialog.
     */
    static AlertDialog showUpdateAvailableDialog(
            final Context context,
            String title,
            String content,
            String btnPositive,
            String btnNegative,
            final UpdateFrom updateFrom,
            final URL apk) {

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UtilsLibrary.goToUpdate(context, updateFrom, apk);
                    }
                })
                .setNegativeButton(btnNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create();
    }

    static AlertDialog showUpdateNotAvailableDialog(final Context context, String title, String content) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }
}
