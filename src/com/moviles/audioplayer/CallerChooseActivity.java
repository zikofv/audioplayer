package com.moviles.audioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class CallerChooseActivity extends ChooseActivity {

	abstract ChooseActivity getCaller();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("XXX", "on Create");
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long album_id){
				//Intent i = new Intent(getCaller(), AudioControlActivity.class);
				Intent i = new Intent();
				i.putExtra("idAlbum", album_id);
				setResult(RESULT_OK,i);
				finish();
				//startActivity(i);
			}
		});
	}
//	protected void onStart(){super.onStart();Log.v("XXX", "on start");}
//	protected void onRestart(){super.onRestart();Log.v("XXX", "on restart");}
//	protected void onResume(){super.onResume();Log.v("XXX", "on resume");}
//	protected void onPause(){super.onPause();Log.v("XXX", "on pause");}
//	protected void onDestroy(){super.onDestroy();Log.v("XXX", "on destroy");}
//	protected void onStop(){super.onStop();Log.v("XXX", "on stop");}
}
