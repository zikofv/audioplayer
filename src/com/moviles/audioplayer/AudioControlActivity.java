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
		setContentView(R.layout.activity_audio_control);
		
		Intent i = new Intent(this, AudioPlayerLocalService.class);
		startService(i);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		
		Button b = (Button) findViewById(R.id.button_play);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.setAlbum(AudioControlActivity.this.idAlbum);
				}
			}
		});
	}
//	@Override
//	protected void onStart(){
//		super.onStart();
//	}
	
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
				}

				@Override
				public void noMoreSongs() {
				}
			});
		}
	};
}
