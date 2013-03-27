package com.moviles.audioplayer;

import android.database.Cursor;

public interface BoundListener {

	public void nextSong();
	public void noMoreSongs();
	public void setCurrentSong(String string);
	public void setCursor(Cursor cursor);
}
