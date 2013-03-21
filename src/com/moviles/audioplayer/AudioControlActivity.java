package com.moviles.audioplayer;

import com.moviles.audioplayer.AudioPlayerLocalService.AudioPlayerLocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AudioControlActivity extends Activity {
	private AudioPlayerLocalService mService;
	private boolean mBound = false;
	private long idAlbum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("idAlbum"))
			this.idAlbum = extras.getLong("idAlbum");
		else
			this.idAlbum = 1l;
		Log.v("XXX", "El id es: " + Long.toString(idAlbum));
		setContentView(R.layout.activity_audio_control);
		Button b = (Button) findViewById(R.id.button_play);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.setAlbum(AudioControlActivity.this.idAlbum);
					mService.print();
				}
			}
		});
	}
	@Override
	protected void onStart(){
		super.onStart();
		Intent i = new Intent(this, AudioPlayerLocalService.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if (mBound){
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioPlayerLocalBinder binder = (AudioPlayerLocalBinder) service;
			mService = binder.getService();//Obtenemos el AudioPlayerLocalService mediante el binder
			mBound = true;
			binder.setListener(new BoundListener() {
				
				@Override
				public void nextSong() {
					Log.d("XXX", "Cambio el tema, tenemos que actualizar la ui");
				}

				@Override
				public void noMoreSongs() {
					Log.d("XXX", "Llamado a noMoreSongs()");
				}
			});
		}
	};
}
