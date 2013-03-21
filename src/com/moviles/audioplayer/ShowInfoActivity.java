package com.moviles.audioplayer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

public class ShowInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_info);
		TextView textView = (TextView) findViewById(R.id.textView1);
		ArtistBioATask aTask = new ArtistBioATask(textView, "Frank Zappa");
		aTask.execute(textView, "Frank Zappa");
//		WebView wb = (WebView)findViewById(R.id.webView1);
//		String summary = "<html><body>You scored <b>192</b> points.</body></html>";
//		wb.loadData(summary, "text/html", null);
	}

}
