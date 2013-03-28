package com.moviles.audioplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class ChooseModeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Log.v("XXX", "CHMODE on create");
        setContentView(R.layout.activity_choose_mode);
        Button chooseArtist = (Button) findViewById(R.id.button_choose_artist);
        Button chooseAlbum = (Button) findViewById(R.id.button_choose_album);
        chooseArtist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//intent iniciar activity seleccionar artista
				Intent i = new Intent(ChooseModeActivity.this, ChooseArtistActivity.class);
				startActivityForResult(i, AudioControlActivity.PICK_ALBUM_REQUEST);
			}
		});
        chooseAlbum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//intent iniciar activity seleccionar album
				Intent i = new Intent(ChooseModeActivity.this, ChooseAlbumActivity.class);
				startActivityForResult(i, AudioControlActivity.PICK_ALBUM_REQUEST);
			}
		});
	}
//	protected void onStart(){super.onStart();Log.v("XXX", "CHMODE on start");}
//	protected void onRestart(){super.onRestart();Log.v("XXX", "CHMODEon restart");}
//	protected void onResume(){super.onResume();Log.v("XXX", "CHMODEon resume");}
//	protected void onPause(){super.onPause();Log.v("XXX", "CHMODEon pause");}
//	protected void onDestroy(){super.onDestroy();Log.v("XXX", "CHMODEon destroy");}
//	protected void onStop(){super.onStop();Log.v("XXX", "CHMODEon stop");}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AudioControlActivity.PICK_ALBUM_REQUEST) {
            if (resultCode == RESULT_OK) {
				setResult(RESULT_OK,data);
				finish();
            }
        }
    }
}
