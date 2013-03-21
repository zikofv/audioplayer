package com.moviles.audioplayer;

import java.io.File;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class AudioPlayerIntentService extends IntentService {
	
	private static final int ONGOING_NOTIFICATION = 0;

//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.e("XXXX", "Dentro del onStartCommand");
//	    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//	    return super.onStartCommand(intent,flags,startId);
//	}
	
	public AudioPlayerIntentService(String name) {
		super("AudioPlayerIntentService");
		Log.e("XXXX", "Dentro de Constructor con String");
	}
	
	public AudioPlayerIntentService(){
		super("AudioPlayerIntentService");
		Log.e("XXXX", "Dentro de Constructor vacio");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("XXXX", "Dentro de onHandleIntent");
		Notification notification = new Notification(R.drawable.cat, getText(R.string.not_string), System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, ChooseAlbumActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.notification_title), getText(R.string.notification_message), pendingIntent);
		//Comienza a correr en el foreground
		startForeground(ONGOING_NOTIFICATION, notification);
		Log.e("XXXX", "Vamos a reproducir algo");
		Uri myUri = Uri.fromFile(new File("/mnt/sdcard/Sister.mp3"));
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(getApplicationContext(), myUri);
			mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mediaPlayer.start();	
	}

}
