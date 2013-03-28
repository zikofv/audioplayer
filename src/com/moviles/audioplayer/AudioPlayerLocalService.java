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
		changeState(State.not_ready);
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnPreparedListener(this);
		mp.setOnCompletionListener(this);
		Log.v("XXXY", "service: onCreate");
	}
	
	private void showCursor(Cursor c){
		Log.v("XXXY", "Mostrando el cursor");
		c.moveToFirst();
		while (!c.isAfterLast()){
			Log.v("XXXY", "service: ID: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
			c.moveToNext();
		}
		c.moveToFirst();
	}
	
	private void setDataSource(Cursor c){
		Log.v("XXXY", "service: En setDataSource");
		Log.v("XXXY", "service: Id acutal: " + c.getInt(c.getColumnIndex(MediaStore.Audio.Media._ID)));
		try {
			if (mp != null){
				if (mp.isPlaying()){
					mp.stop();
				}
				mp.reset();
				mp.setDataSource(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
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
		//listenerActivity.setCursor(this.cursor); Esto te lo avanza!!!
		//showCursor(cursor);
		this.cursor.moveToFirst();
		this.setDataSource(this.cursor);
	}
	
	public void play(){
		if (mp != null && (this.state.equals(State.ready) || this.state.equals(State.paused))){
			Log.v("XXXY", "Entramos al play");
			changeState(State.playing);
			mp.start();
		}
	}
	
	public void pause(){
		Log.v("XXXY", "service: VAmos a pausar");
		Log.v("XXXY", "Estamos en el estado " + this.state.toString());
		if (mp != null && (this.state.equals(State.playing) || this.state.equals(State.ready))){
			changeState(State.paused);
			mp.pause();
		}
	}
	
	public void next(){
		Log.v("XXXY", "service: next");
		if (!this.cursor.isLast()){
			this.cursor.moveToNext();
			Log.v("XXXY", "Funciono el moveTonext");
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}
	
	public void prev() {
		Log.v("XXXY", "service: prev");
		if (!this.cursor.isFirst()){
			this.cursor.moveToPrevious();
			Log.v("XXXY", "Funciono el moveToPrevious");
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		Log.v("XXXY", "service: onPrepared");
		listenerActivity.setCurrentSong("hola");
		if (this.state.equals(State.wait_on_prepared)){
			Log.v("XXXY", "service: estabamos en wait_on_prepared");
			changeState(State.ready);
			this.play();
		}
		changeState(State.ready);
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		Log.v("XXXY", "service: onCompletion");
		if (!this.cursor.isLast()){
			this.cursor.moveToNext();
			Log.v("XXXY", "Funciono el moveTonext");
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}

	private void changeState(State newState) {
//		if (this.state != null)
//			Log.v("XXXY", "Viejo estado: " + this.state.toString());
		this.state = newState;
//		Log.v("XXXY", "Nuevo estado: " + this.state.toString());
		
	}

	
}
