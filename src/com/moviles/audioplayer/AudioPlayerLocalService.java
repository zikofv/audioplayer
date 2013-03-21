package com.moviles.audioplayer;

import java.io.IOException;

import android.app.Service;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;


public class AudioPlayerLocalService extends Service implements OnPreparedListener, OnCompletionListener {

	Cursor cursor;
	//El uri del album
	Uri uri;	
	MediaPlayer mp;
	
    // El Binder para los clientes
    private final IBinder mBinder = new AudioPlayerLocalBinder();
    
    //Los clientes que van a escuchar
    private BoundListener listenerActivity;
    
    //Binder class
	public class AudioPlayerLocalBinder extends Binder {
		AudioPlayerLocalService getService(){
			//El binder devuelve la instancia de esta clase
			return AudioPlayerLocalService.this;
		}
		//Seteamos la actividad que va a escuchar
		public void setListener(BoundListener listener){
			listenerActivity = listener;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		//We return the binder to the client
		return mBinder;
	}
	
	public void setAlbum(long idAlbum){
		CursorLoader cl = new CursorLoader(getApplicationContext());
		cl.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		cl.setSelection("album_id = " + Long.toString(idAlbum));
		Cursor c = cl.loadInBackground();
		c.moveToFirst();
		
		Log.v("XXX", "Vamos a loguear que hay en title");
		Log.v("XXX", c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)));
		Log.v("XXX", "Esto es lo que tiene DATA");
		Log.v("XXX", MediaStore.Audio.Media.DATA);
		try {
			mp.setDataSource(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
			mp.setOnPreparedListener(this);
			mp.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Este metodo esta disponible para los clientes
	 */
	void print(){
		Log.d("XXX", "Imprimiendo desde el LocalService");
		listenerActivity.nextSong();
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.start();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.v("XXX", "Termino el tema loquito!!!!!!!");
	}
}
