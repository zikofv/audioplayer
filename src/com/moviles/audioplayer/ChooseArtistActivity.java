package com.moviles.audioplayer;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ChooseArtistActivity extends ChooseActivity { 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Cuando el usuario hace click en uno de los elementos de la lista, se llama al intent que se encarga de elegir album del artista.
       	listView.setOnItemClickListener(new OnItemClickListener() {
       		@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//       			Log.v("XXX", "CLick en el id: " + Long.toString(id));
       			Intent i = new Intent(ChooseArtistActivity.this, ChooseArtistAlbumActivity.class);
       			i.putExtra("artist_id", id);
       			startActivity(i);
        	}
		});
        	
    }

	@Override
	protected void initAtts(Bundle savedInstanceState, Bundle extras) {
		listViewId = R.id.listView;
		layout = R.layout.activity_choose_artist;
		ccols = new String[] {
        		MediaStore.Audio.Artists._ID,
        		MediaStore.Audio.Artists.ARTIST,
        		MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };
        
        uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        
        from = new String[]{
        		MediaStore.Audio.Artists.ARTIST,
        };
        
        to = new int[] {
        		R.id.name_entry_1,
        };

	}
}
