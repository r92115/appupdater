package com.ngfv.appupdater;

import android.content.Context;
import android.os.AsyncTask;

import com.ngfv.appupdater.enums.AppUpdaterError;
import com.ngfv.appupdater.enums.UpdateFrom;
import com.ngfv.appupdater.objects.Update;

/**
 * @author Rajeev on 21/9/16.
 */

class UtilsAsync {

    static class LatestAppVersion extends AsyncTask<Void, Void, Update> {
        private Context context;
        private LibraryPreferences libraryPreferences;
        private Boolean fromUtils;
        private UpdateFrom updateFrom;
        private AppUpdater.LibraryListener listener;

        LatestAppVersion(Context context, boolean fromUtils, UpdateFrom updateFrom, AppUpdater.LibraryListener listener) {
            this.context = context;
            this.libraryPreferences = new LibraryPreferences(context);
            this.fromUtils = fromUtils;
            this.updateFrom = updateFrom;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!fromUtils && !libraryPreferences.getAppUpdaterShow()) {
                    cancel(true);
                }
            } else {
                listener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel(true);
            }
        }

        @Override
        protected Update doInBackground(Void... voids) {
            return UtilsLibrary.getLatestAppVersionHttp(context, updateFrom);
        }

        @Override
        protected void onPostExecute(Update update) {
            super.onPostExecute(update);
            if (UtilsLibrary.isStringAVersion(update.getLatestVersion())) {
                listener.onSuccess(update);
            } else {
                listener.onFailed(AppUpdaterError.UPDATE_VARIES_BY_DEVICE);
            }
        }
    }
}
