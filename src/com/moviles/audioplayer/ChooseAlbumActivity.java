package com.moviles.audioplayer;

import android.os.Bundle;
import android.provider.MediaStore;


public class ChooseAlbumActivity extends CallerChooseActivity {
	
	@Override
	protected void initAtts(Bundle savedInstanceState, Bundle extras) {
		listViewId = R.id.listView;
		layout = R.layout.activity_choose_album;
		ccols = new String[] {
        		MediaStore.Audio.Albums._ID,
        		MediaStore.Audio.Albums.ALBUM,
        		MediaStore.Audio.Albums.ARTIST,
        };
		
        uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        
        from = new String[]{
        		MediaStore.Audio.Albums.ARTIST,
        		MediaStore.Audio.Albums.ALBUM,
       	};
        
       	to = new int[] {
       			R.id.name_entry_1,
       			R.id.name_entry_2,
       	};
	}
	
	@Override
	ChooseActivity getCaller() {
		return this;
	}

}
