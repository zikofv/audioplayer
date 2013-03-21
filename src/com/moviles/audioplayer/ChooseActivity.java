package com.moviles.audioplayer;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public abstract class ChooseActivity extends Activity implements LoaderCallbacks<Cursor> {

	protected ListView listView;
	protected SimpleCursorAdapter listViewAdapter;
	protected String[] ccols;
	protected Uri uri;
	protected String [] from;
	protected int [] to;
	protected int layout;
	protected int listViewId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		initAtts(savedInstanceState, extras);
		
		setContentView(layout);
		
       	listViewAdapter = new SimpleCursorAdapter(
       			getApplicationContext(),
       			R.layout.list_entry,
       			null,
       			from,
       			to,
       			0
       	);
       	
        listView = (ListView) findViewById(listViewId);
       	listView.setAdapter(listViewAdapter);
       	
       	//Iniciamos el Loader
       	getLoaderManager().initLoader(0, null, this);
	}
	
	protected abstract void initAtts(Bundle savedInstanceState, Bundle extras); 

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        CursorLoader retCursor = new CursorLoader(
        		getApplicationContext(),
        		uri,
        		ccols,
        		null,
        		null,
        		null);
		return retCursor;
	}
	
	@Override
	/**
	 * Este metodo se llama cuando el loader termina.
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		listViewAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		listViewAdapter.swapCursor(null);
	}
}
