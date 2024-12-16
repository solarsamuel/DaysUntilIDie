package com.example.daysuntilidie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.daysuntilidie.DaysWidgetProvider;

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_ON) {
            DaysWidgetProvider.updateAllWidgets(context)
        }
    }
}
