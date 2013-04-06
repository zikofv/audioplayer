package com.moviles.audioplayer;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class AudioPlayerLocalService extends Service implements OnPreparedListener, OnCompletionListener {

	private Cursor cursor;
	private MediaPlayer mp;
    private final IBinder mBinder = new AudioPlayerLocalBinder();// El Binder para los clientes
    private BoundListener listenerActivity;// Instancia de la actividad cliente
	private enum State {playing, paused, ready, not_ready, wait_on_prepared};
	private State state;
	private int notificationId;
	private Notification notification;
	private NotificationManager notificationManager;
	private String currentSong;
	private String currentArtist;
	private boolean infoUpToDate = false;
	
	/*
	 * TEST!!!!
	 */
	public static final String PLAY = "com.moviles.audioplayer.playcommand";
	
	private BroadcastReceiver mIntentBR = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.v("XXXZ", "Nos llego algo al onReceive");
		}
		
	};
	/*
	 * TEST!!!!
	 */
	
	/**
	 * Subclase de Binder que se entrega a los clientes de este servicio al momento que se asocian.
	 *
	 */
	public class AudioPlayerLocalBinder extends Binder {
		AudioPlayerLocalService getService(){
			return AudioPlayerLocalService.this;//El binder devuelve la instancia de esta clase
		}
		
		public void setListener(BoundListener listener){
			listenerActivity = listener;//Seteamos la actividad que va a escuchar
		}
	}
	
	/**
	 * Este metodo es llamado cuando una actividad cliente se asocia a este servicio.
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		Log.v("XXXZ", "service: onBind");
		return mBinder;//We return the binder to the client
	}
	
//	public int onStartCommand(Intent intent, int flags, int startId){
//		int ret = super.onStartCommand(intent, flags, startId);
//		Log.v("XXXZ", "service: onStartCommand intent: " + intent.getExtras().getString("hola"));
//		return ret;
//		
//	}
	
	/**
	 * Este método se llama al crear el servicio.
	 */
	@Override
	public void onCreate() {
		Log.v("XXXZ", "service: onCreate");
		super.onCreate();

		/*
		 * TEST!!!!
		 */
		IntentFilter intFil = new IntentFilter();
		intFil.addAction(PLAY);
		registerReceiver(mIntentBR, intFil);
		/*
		 * TEST!!!!
		 */
		changeState(State.not_ready);
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnPreparedListener(this);
		mp.setOnCompletionListener(this);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.headphones)
		        .setContentTitle("AudioPlayer")
		        .setContentText("El reproductor esta ejecutandose.");
		
		Intent i = new Intent(this, AudioControlActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
		mBuilder.setContentIntent(pi);
		// notificationId allows you to update the notification later on.
		this.notificationId = 1;
		this.notification = mBuilder.build();
		startForeground(1337, notification);
		
	}
	
	
	/**
	 * Carga el archivo indicado por el cursor en la instancia del MediaPlayer.
	 * @param cursor el cursor de donde obtener el archivo.
	 */
	private void setDataSource(Cursor c){
		try {
			if (mp != null){
				if (mp.isPlaying()){
					mp.stop();
				}
				mp.reset();
				mp.setDataSource(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
				this.currentSong = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)); 
				this.currentArtist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)); 
				infoUpToDate = false;
				mp.prepareAsync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Establece el valor del album actual.
	 * @param idAlbum long con el id del album.
	 */
	public void setAlbum(long idAlbum){
		CursorLoader cl = new CursorLoader(getApplicationContext());
		cl.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		cl.setSelection("album_id = " + Long.toString(idAlbum));
		this.cursor = cl.loadInBackground();
		this.cursor.moveToFirst();
		this.setDataSource(this.cursor);
	}
	
	/**
	 * Indica el nombre del artista y tema actuales a la actvidad cliente de este servicio y al manejador de notificaciones.
	 * @param currArtist el artista del tema actual 
	 * @param currSong el nombre del tema actual 
	 */
	private void updateCurrentInfo(String currentArtist2, String currentSong2) {
		if (!infoUpToDate){
			listenerActivity.setCurrentSong(this.currentArtist + "-" + this.currentSong);
			listenerActivity.setCurrentArtist(this.currentArtist);
			updateNotification(this.currentArtist, this.currentSong);
			infoUpToDate = true;
		}
	}

	/**
	 * Elimina las notificaciones previas y emite una nueva indicando el artista y titulo del archivo que se esta
	 * reproduciendo. 
	 * @param currArtist el artista del tema actual 
	 * @param currSong el nombre del tema actual 
	 */
	private void updateNotification(String currArtist, String currSong){
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
		cancelNotification();
		notificationManager.notify(
				notificationId,
				new NotificationCompat.Builder(this)
				.setContentTitle("AudioPlayer")
				.setContentText("Reproduciendo:" +  currArtist + "-" + currSong)
				.setSmallIcon(R.drawable.headphones)
				.build()
		);
	}
	
	/**
	 * Elimina las notificaciones existentes.
	 */
	private void cancelNotification(){
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
		notificationManager.cancelAll();
	}

	/**
	 * Reproduce el tema actual.
	 */
	public void play(){
		if (mp != null && (this.state.equals(State.ready) || this.state.equals(State.paused))){
			changeState(State.playing);
			updateCurrentInfo(this.currentArtist, this.currentSong);
			mp.start();
		}
	}
	
	/**
	 * Detiene la reproduccion del tema actual.
	 */
	public void pause(){
		if (mp != null && this.state.equals(State.playing)){
			changeState(State.paused);
			mp.pause();
		}
	}
	
	/**
	 * Retrocede al tema anterior si no se esta reproduciendo el primero.
	 */
	public void next(){
		if (!this.cursor.isLast()){
			this.cursor.moveToNext();
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}
	
	/**
	 * Avanza al siguiente tema si no se esta reproduciendo el ultimo.
	 */
	public void prev() {
		if (!this.cursor.isFirst()){
			this.cursor.moveToPrevious();
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}

	/**
	 * Este método es llamado cuando el media player esta listo para reproducir un nuevo tema. 
	 */
	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		if (this.state.equals(State.wait_on_prepared)){
			changeState(State.ready);
			this.play();
		}
		else
			changeState(State.ready);
	}

	/**
	 * Este método es llamado cuando se termina de reproducir un tema.
	 */
	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		cancelNotification();
		if (!this.cursor.isLast()){
			this.cursor.moveToNext();
			changeState(State.wait_on_prepared);
			this.setDataSource(this.cursor);
		}
	}

	private void changeState(State newState) {
		this.state = newState;
	}
	
	/**
	 * Retorna verdadero si se esta reproduciendo un archivo.
	 * @return True si se esta reproduciendo un archivo.
	 */
	public boolean isPlaying(){
		return this.state.equals(State.playing);
	}
	
	/**
	 * Retorna verdadero si se esta en el estado pausa. 
	 * @return True si se esta en el estado pausa. 
	 */
	public boolean isPaused(){
		return this.state.equals(State.paused);
	}
	
	/**
	 * Retorna el nombre de la cancion que se esta reproduciendo.
	 * @return String con el nombre de la cancion que se esta reproduciendo.
	 */
	public String getCurrentSong(){
		return this.currentSong;
	}
	
	/**
	 * Retorna el nombre del artista de la cancion que se esta reproduciendo.
	 * @return String con el nombre nombre del artista de la cancion que se esta reproduciendo.
	 */
	public String getCurrentArtist(){
		return this.currentArtist;
	}
}
