package com.moviles.audioplayer;

public interface BoundListener {

	public void nextSong();
	public void noMoreSongs();
	public void setCurrentSong(String string);
	public void setCurrentArtist(String currentArtist);
}
