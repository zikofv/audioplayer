package com.moviles.audioplayer;

import com.moviles.audioplayer.AudioPlayerLocalService.AudioPlayerLocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class AudioControlActivity extends Activity {
	private AudioPlayerLocalService mService;
	private boolean mBound = false;
	private long idAlbum;
	private Button play;
	private Button pause;
	private Button setAlbum;
	ListView lview;
	SimpleCursorAdapter lViewAdapter;
	
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
		
		
		setAlbum = (Button) findViewById(R.id.button_set_album);
		setAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound){
					mService.setAlbum(AudioControlActivity.this.idAlbum);
				}
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
		
		lViewAdapter = new SimpleCursorAdapter(
				getApplicationContext(),
				R.layout.list_entry,
				null,//Inicialmente el cursor es null
				new String[]{MediaStore.Audio.Media.TITLE},//from
				new int[] {R.id.name_entry_1,},//to
				0
		);        
		lview = (ListView) findViewById(R.id.listView_AudioControlActivity);
		lview.setAdapter(lViewAdapter);
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
				}

				@Override
				public void noMoreSongs() {
				}

				@Override
				public void setCurrentSong(String string) {
				}

				@Override
				public void setCursor(Cursor cursor) {
					AudioControlActivity.this.lViewAdapter.swapCursor(cursor);
				}
			});
		}
	};
}
