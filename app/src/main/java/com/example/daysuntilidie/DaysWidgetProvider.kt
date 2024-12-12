package com.example.daysuntilidie

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DaysWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidget(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val sharedPreferences = context.getSharedPreferences("DaysUntilIDiePrefs", Context.MODE_PRIVATE)
            val resultText = sharedPreferences.getString("result", "Days left: ")

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                views.setTextViewText(R.id.widgetTextView, resultText)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        fun updateDaysWidget(context: Context) {
            val sharedPreferences = context.getSharedPreferences("DaysUntilIDiePrefs", Context.MODE_PRIVATE)
            val birthdateString = sharedPreferences.getString("birthdate", "")
            val expectedAge = sharedPreferences.getInt("expectedAge", 0)

            if (!birthdateString.isNullOrEmpty() && expectedAge > 0) {
                try {
                    val birthdate = LocalDate.parse(birthdateString, DateTimeFormatter.ISO_DATE)
                    val deathDate = birthdate.plusYears(expectedAge.toLong())
                    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deathDate)

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
                    remoteViews.setTextViewText(R.id.widgetTextView, "Days left: $daysLeft")

                    val thisWidget = ComponentName(context, DaysWidgetProvider::class.java)
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews)
                } catch (e: Exception) {
                    e.printStackTrace()

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
                    remoteViews.setTextViewText(R.id.widgetTextView, "Error: Invalid data.")

                    val thisWidget = ComponentName(context, DaysWidgetProvider::class.java)
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews)
                }
            }
        }
    }
}

