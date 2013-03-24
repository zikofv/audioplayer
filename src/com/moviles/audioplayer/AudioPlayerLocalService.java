package com.moviles.audioplayer;


import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;


public class AudioPlayerLocalService extends Service implements OnPreparedListener, OnCompletionListener {

	private Cursor cursor;
	private Uri uri;//El uri del album
	private MediaPlayer mp;
    private final IBinder mBinder = new AudioPlayerLocalBinder();// El Binder para los clientes
    private BoundListener listenerActivity;//Los clientes que van a escuchar
    
    //Binder class
	public class AudioPlayerLocalBinder extends Binder {
		AudioPlayerLocalService getService(){
			return AudioPlayerLocalService.this;//El binder devuelve la instancia de esta clase
		}
		
		public void setListener(BoundListener listener){
			listenerActivity = listener;//Seteamos la actividad que va a escuchar
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.v("XXX", "Llamamos al onBind del service");
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnPreparedListener(this);
		return mBinder;//We return the binder to the client
	}
	
	@Override
	public boolean onUnbind(Intent i){
		boolean b = super.onUnbind(i);
		Log.v("XXX", "Dentro de unbind del service");
		return b;
	}
	
	@Override
	public void onCreate(){
		Log.v("XXX", "Se llamo al oncreate del service");
	}
	
	
	public void setAlbum(long idAlbum){
		CursorLoader cl = new CursorLoader(getApplicationContext());
		cl.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		cl.setSelection("album_id = " + Long.toString(idAlbum));
		cursor = cl.loadInBackground();
		cursor.moveToFirst();
		
		try {
			if (mp != null){
				if (mp.isPlaying()){mp.stop();mp.reset();}
				mp.setDataSource(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
				mp.prepareAsync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.start();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
	}
	

}
