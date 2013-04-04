package com.me.Battleships;

import Core.Main;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	
	private WebSocketHandler socketHandler;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        socketHandler = LobbyActivity.socketOutputHandler;
       
        startGame();
    }
	
	public void startGame() {
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
     
        initialize(new Main(socketHandler), cfg);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("battleships", "ondestroy");
		socketHandler.leave();
	}
	
	
}