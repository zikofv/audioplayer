package com.moviles.audioplayer;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.Params;

public class ArtistBioATask extends AsyncTask<Object, Object, String> {

	TextView text;
	String artistName;
	
	public ArtistBioATask(TextView text, String artistName) {
		super();
		this.text = text;
		this.artistName = artistName;
	}
	
	@Override
	protected void onPostExecute(String result) {
		Log.d("ATASK", "Post Executing "+Thread.currentThread().getName());
		text.setText(result);
	}
	@Override
	protected String doInBackground(Object... arg0) {
		String result="No se encontro la biografia para el artista: " + this.artistName;
		try{
			String key = "SWLBABFBVT7A0QIZY";
			EchoNestAPI en = new EchoNestAPI(key);
			Params p = new Params();
			p.add("name", this.artistName);
			p.add("results", 1);

			List<Artist> artists = en.searchArtists(p);
			for (Artist artist : artists) {
				List<Biography> bios = artist.getBiographies();
				for (int i = 0; i < bios.size(); i++) {
					Biography bio = bios.get(i);
					if (bio.getSite().equals("wikipedia")){
						result = bio.getText();
					}
				}	
			}
		}catch (Exception e){}
		return result;
	}
}
	
