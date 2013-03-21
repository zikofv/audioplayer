package com.moviles.audioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class CallerChooseActivity extends ChooseActivity {

	abstract ChooseActivity getCaller();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long album_id){
				Intent i = new Intent(getCaller(), AudioControlActivity.class);
				i.putExtra("idAlbum", album_id);
				startActivity(i);
			}
		});
	}
}
