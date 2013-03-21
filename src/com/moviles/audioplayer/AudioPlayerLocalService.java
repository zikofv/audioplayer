package com.moviles.audioplayer;

import android.app.Service;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;


public class AudioPlayerLocalService extends Service {

	Cursor cursor;
	//El uri del album
	Uri uri;	
	
    // El Binder para los clientes
    private final IBinder mBinder = new AudioPlayerLocalBinder();
    
    //Los clientes que van a escuchar
    private BoundListener listenerActivity;
    
    //Binder class
	public class AudioPlayerLocalBinder extends Binder {
		AudioPlayerLocalService getService(){
			//El binder devuelve la instancia de esta clase
			return AudioPlayerLocalService.this;
		}
		//Seteamos la actividad que va a escuchar
		public void setListener(BoundListener listener){
			listenerActivity = listener;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		//We return the binder to the client
		return mBinder;
	}
	
	public void setAlbum(long idAlbum){
		CursorLoader cl = new CursorLoader(getApplicationContext());
		cl.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		cl.setSelection("album_id = " + Long.toString(idAlbum));
		Cursor c = cl.loadInBackground();
		c.moveToFirst();
		Log.v("XXX", "Vamos a loguear que hay en title");
		Log.v("XXX", c.getString(c.getColumnIndex("title")));
		//Estas son las columnas
//		Log.v("XXX", "Vamos a mostrar las columnas!!!!:");
//		for (String s : c.getColumnNames())
//			Log.v("XXX", s);
	}

	/**
	 * Este metodo esta disponible para los clientes
	 */
	void print(){
		Log.d("XXX", "Imprimiendo desde el LocalService");
		listenerActivity.nextSong();
	}

//	@Override
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        CursorLoader retCursor = new CursorLoader(
//        		getApplicationContext(),
//        		uri,
//        		null,//ccols,
//        		null,
//        		null,
//        		null);
//		return retCursor;
//	}
//
//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//		this.cursor = cursor;
//	}
//
//	@Override
//	public void onLoaderReset(Loader<Cursor> arg0) {
//		this.cursor = null;
//	}
}
