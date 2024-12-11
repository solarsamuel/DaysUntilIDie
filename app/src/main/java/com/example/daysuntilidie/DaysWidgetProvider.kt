package com.example.daysuntilidie

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class DaysWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val sharedPreferences = context.getSharedPreferences("DaysUntilIDiePrefs", Context.MODE_PRIVATE)
        val resultText = sharedPreferences.getString("result", "Days left: ")

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widgetResultText, resultText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
