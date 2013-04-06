package com.moviles.audioplayer;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetAudioControlProvider extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.v("XXXZ", "WIDGET onEnabled");
	}
//    private final String ACTION_WIDGET_PLAY = "PlaySong";
//    private final String ACTION_WIDGET_PAUSE = "PauseSong";
//    private final String ACTION_WIDGET_STOP = "StopSong";   
    private final int INTENT_FLAGS = 0;
    private final int REQUEST_CODE = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews widgetLayout = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent prevIntent = new Intent(AudioPlayerLocalService.PREVIOUS);
        Intent playIntent = new Intent(AudioPlayerLocalService.PLAY);
        Intent pauseIntent = new Intent(AudioPlayerLocalService.PAUSE);
        Intent nextIntent = new Intent(AudioPlayerLocalService.NEXT);

        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, prevIntent, INTENT_FLAGS);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, playIntent, INTENT_FLAGS);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, pauseIntent, INTENT_FLAGS);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, nextIntent, INTENT_FLAGS);

        widgetLayout.setOnClickPendingIntent(R.id.imagePrev, prevPendingIntent);
        widgetLayout.setOnClickPendingIntent(R.id.imagePlay, playPendingIntent);
        widgetLayout.setOnClickPendingIntent(R.id.imagePause, pausePendingIntent);
        widgetLayout.setOnClickPendingIntent(R.id.imageNext, nextPendingIntent);
        
        appWidgetManager.updateAppWidget(appWidgetIds, widgetLayout);         
    }
}