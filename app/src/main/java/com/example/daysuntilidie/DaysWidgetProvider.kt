package com.example.daysuntilidie

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DaysWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        @JvmStatic
        private fun calculateDaysLeft(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val month = sharedPreferences.getInt(MainActivity.PREF_MONTH, -1)
            val day = sharedPreferences.getInt(MainActivity.PREF_DAY, -1)
            val year = sharedPreferences.getInt(MainActivity.PREF_YEAR, -1)
            val expectedAge = sharedPreferences.getInt(MainActivity.PREF_EXPECTED_AGE, -1)

            if (month == -1 || day == -1 || year == -1 || expectedAge == -1) {
                return "Set data in app"
            }

            val birthdate = LocalDate.of(year, month, day)
            val deathDate = birthdate.plusYears(expectedAge.toLong())
            val today = LocalDate.now()
            val daysLeft = ChronoUnit.DAYS.between(today, deathDate)

            return if (daysLeft >= 0) {
                "Days left: $daysLeft"
            } else {
                "Past expected age"
            }
        }

        @JvmStatic
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val daysLeft = calculateDaysLeft(context)

            val views = RemoteViews(context.packageName, R.layout.days_left_widget)
            views.setTextViewText(R.id.widget_days_left, daysLeft)

            // Launch MainActivity when the widget is clicked
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_days_left, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        @JvmStatic
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, DaysWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
