package com.moviles.audioplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class ChooseModeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);
        Button chooseArtist = (Button) findViewById(R.id.button_choose_artist);
        Button chooseAlbum = (Button) findViewById(R.id.button_choose_album);
        chooseArtist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//intent iniciar activity seleccionar artista
				Intent i = new Intent(ChooseModeActivity.this, ChooseArtistActivity.class);
				startActivity(i);
			}
		});
        chooseAlbum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//intent iniciar activity seleccionar album
				Intent i = new Intent(ChooseModeActivity.this, ChooseAlbumActivity.class);
				startActivity(i);
			}
		});
	}
}
