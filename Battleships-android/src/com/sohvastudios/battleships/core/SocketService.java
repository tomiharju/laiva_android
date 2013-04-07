package com.sohvastudios.battleships.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service {
	
	private SocketBinder socketBinder;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("battleships", "Creating SocketService");
		
		SocketIOListener socketListener = new SocketIOListener();
		socketListener.setNativeActionsHandler(new NativeActionsImpl(this.getApplicationContext()));
		socketBinder = new SocketBinder(socketListener);
		
		socketBinder.connect();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return socketBinder;
	}

}
