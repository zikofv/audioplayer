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
	private enum State {playing, paused, ready, not_ready, wait_on_prepared};
	private State state;
    
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
		Log.v("XXXY", "service: onBind");
		return mBinder;//We return the binder to the client
	}
	
	@Override
	public boolean onUnbind(Intent i){
		boolean b = super.onUnbind(i);
		Log.v("XXXY", "service: onUnbind");
		return b;
	}
	
	/**
	 * Este método solo se llama una vez.
	 */
	@Override
	public void onCreate(){
		super.onCreate();
		this.state = State.not_ready;
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnPreparedListener(this);
		mp.setOnCompletionListener(this);
		Log.v("XXXY", "service: onCreate");
	}
	
	@Override
	public int onStartCommand(Intent i, int flags, int hola){
		int r = super.onStartCommand(i, flags, hola);
		Log.v("XXXY", "service: onStartCommand chabon");
		return r;
	}
	
	@Override
	public void onStart(Intent i, int st){
		super.onStart(i, st);
		Log.v("XXXY", "service: onStart");
	}
	
	private void setDataSource(Cursor c){
		Log.v("XXXY", "service: En setDataSource");
		Log.v("XXXY", "service: Id acutal: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
		try {
			if (mp != null){
				if (mp.isPlaying()){
					mp.stop();
				}
				mp.reset();
				mp.setDataSource(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
				Log.v("XXXY", "service: llamamos a prepareAsync");
				mp.prepareAsync();
			}
			else {Log.v("XXXY", "service: el mp es null? WTF");}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setAlbum(long idAlbum){
		CursorLoader cl = new CursorLoader(getApplicationContext());
		cl.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		cl.setSelection("album_id = " + Long.toString(idAlbum));
		this.cursor = cl.loadInBackground();
		Log.v("XXXY", "service: Cant de rows: " + Integer.toString(cursor.getCount()));
		listenerActivity.setCursor(this.cursor);
		this.cursor.moveToFirst();
		this.setDataSource(this.cursor);
	}
	
	public void play(){
		if (mp != null && (this.state.equals(State.ready) || this.state.equals(State.paused))){
			Log.v("XXXY", "Entramos al play");
			mp.start();
			this.state = State.playing;
		}
	}
	
	public void pause(){
		Log.v("XXXY", "service: VAmos a pausar");
		Log.v("XXXY", this.state.toString());
		if (mp != null && this.state.equals(State.playing)){
			mp.pause();
			this.state = State.paused;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		Log.v("XXXY", "service: onPrepared");
		listenerActivity.setCurrentSong("hola");
		if (this.state.equals(State.wait_on_prepared)){
			Log.v("XXXY", "service: estabamos en wait_on_prepared");
			this.state = State.ready;
			this.play();
		}
		this.state = State.ready;
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		Log.v("XXXY", "service: onCompletion");
//		if (!this.cursor.isLast()){
			//this.cursor.moveToNext();
			Log.v("XXXY", "Funciono el moveTonext");
			this.state = State.wait_on_prepared;
			this.setDataSource(this.cursor);
//		}
	}
	
}
