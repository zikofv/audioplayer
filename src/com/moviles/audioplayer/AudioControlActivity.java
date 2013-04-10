package com.moviles.audioplayer;

import com.moviles.audioplayer.AudioPlayerLocalService.AudioPlayerLocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AudioControlActivity extends Activity {
	private AudioPlayerLocalService mService;
	private boolean mBound = false;
	private Button play;
	private Button pause;
	private Button next;
	private Button prev;
	private Button setAlbum;
	private TextView nowPlaying;
	private TextView artistBio;
	private boolean mustSetAlbumId;
	private long albumId;
	static final int PICK_ALBUM_REQUEST = 0;

	@Override
	protected void onStart(){
		Log.v("XXXZ", "control: onStart");
		super.onStart();
		Intent i = new Intent(this, AudioPlayerLocalService.class);
		startService(i);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onResume(){
		Log.v("XXXZ", "control: onResume");
		super.onResume();
	}
	
	@Override
	protected void onRestart(){
		Log.v("XXXZ", "control: onRestart");
		super.onRestart();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle){
		Log.v("XXXZ", "control: onSaveInstanceState");
		super.onSaveInstanceState(bundle);
	}
	
	@Override
	protected void onStop(){
		Log.v("XXXZ", "control: onStop");
		super.onStop();
		if (mBound && (mConnection != null)){
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	@Override
	protected void onPause(){
		Log.v("XXXZ", "control: onPause");
		super.onPause();
	}
	
	@Override
	protected void onDestroy(){
		Log.v("XXXZ", "control: onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("XXXZ", "control: onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_control);
		
		setAlbum = (Button) findViewById(R.id.button_set_album);
		setAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AudioControlActivity.this, ChooseModeActivity.class);
				startActivityForResult(i, PICK_ALBUM_REQUEST);
			}
		});
		
		play = (Button) findViewById(R.id.button_play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.play();
				}
			}
		});
		pause = (Button) findViewById(R.id.button_pause);
		pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.pause();
				}
			}
		});
		
		next = (Button) findViewById(R.id.button_next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.next();
				}
			}
		});
		
		prev = (Button) findViewById(R.id.button_prev);
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.prev();
				}
			}
		});
		
		nowPlaying = (TextView) findViewById(R.id.textView_now_playing);
		artistBio = (TextView) findViewById(R.id.textView_artist_bio);
		Log.v("XXXZ", "control: Salimos del onCreate");
		
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("XXXZ", "control: onActivityResult");
        if (requestCode == PICK_ALBUM_REQUEST) {
            if (resultCode == RESULT_OK) {
            	mustSetAlbumId = true;
            	albumId = data.getLongExtra("idAlbum", 1l);
            }
        }
    }
	
	/**
	 * Este método se encarga de actualizar la vista de la actividad con los datos obtenidos del AudioPlayerLocalService.
	 * Este método es llamado por onServiceConnected y por lo tanto se ejecuta cuando la actividad se crea.
	 */
	protected void updateViews() {
		if (this.mBound){
			if (this.mService.isPlaying() || this.mService.isPaused()){
				//pedimos current song y current album al service
				//actualizamos los textView de bio y de now_playing
				String currentArtist = this.mService.getCurrentArtist();
				Log.v("XXXZ", "control: updateViews curartist " + currentArtist);
				this.nowPlaying.setText(currentArtist + "-" + this.mService.getCurrentSong());
				ArtistBioATask aTask = new ArtistBioATask(artistBio, currentArtist);
				aTask.execute(artistBio, currentArtist);
			}
		}
	}
	
	private void setAlbumId() {
		if (this.mBound && this.mustSetAlbumId){
			this.mService.setAlbum(albumId);
			this.mustSetAlbumId = false;
			this.albumId = 0l;
		}
	}
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v("XXXZ", "control: onServiceDisconnected");
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioPlayerLocalBinder binder = (AudioPlayerLocalBinder) service;
			mService = binder.getService();//Obtenemos el AudioPlayerLocalService mediante el binder
			mBound = true;
			Log.v("XXXZ", "control: onServiceConnected");
			updateViews();//Actualizamos las vistas
			setAlbumId();//Si volvemos de llamar al ChooseModeActivity y quedo un albumid pendiente, se lo indicamos al service
			binder.setListener(new BoundListener() {
				
				@Override
				public void setCurrentSong(String song) {
					AudioControlActivity.this.nowPlaying.setText(song);
				}

				@Override
				public void setCurrentArtist(String currentArtist) {
					ArtistBioATask aTask = new ArtistBioATask(artistBio, currentArtist);
					aTask.execute(artistBio, currentArtist);
				}
			});
		}
			
	};

}
