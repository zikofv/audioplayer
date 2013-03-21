package com.moviles.audioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseArtistAlbumActivity extends CallerChooseActivity { 
	@Override
	protected void initAtts(Bundle savedInstanceState, Bundle extras) {
		listViewId = R.id.listView;
		layout = R.layout.activity_choose_artist_album;
		ccols = null;

		if (extras != null && extras.containsKey("artist_id")){
			long artist_id = extras.getLong("artist_id");
			uri = MediaStore.Audio.Artists.Albums.getContentUri("external", artist_id);
			Log.v("XXX", "El uri es: " + uri.toString());
		}
		else{
			uri = MediaStore.Audio.Artists.Albums.getContentUri("external", 1);//Si no recibimos un id de artista, usamos el id 1.
		}

		from = new String[]{
				MediaStore.Audio.Artists.Albums.ALBUM,
		};

		to = new int[] {
				R.id.name_entry_1
		};

	}

	@Override
	ChooseActivity getCaller() {
		return this;
	}

}
