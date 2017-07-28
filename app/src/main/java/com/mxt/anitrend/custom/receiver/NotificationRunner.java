package com.mxt.anitrend.custom.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mxt.anitrend.custom.service.ServiceScheduler;
import com.mxt.anitrend.util.ApplicationPrefs;
import com.mxt.anitrend.util.DefaultPreferences;

/**
 * Created by max on 2017/03/03.
 */

public class NotificationRunner extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                if(new ApplicationPrefs(context).isAuthenticated()) {
                    if (new DefaultPreferences(context).isNotificationEnabled())
                        new ServiceScheduler(context).scheduleJob();
                    else
                        new ServiceScheduler(context).cancelJob();
                }
                break;
        }
    }
}