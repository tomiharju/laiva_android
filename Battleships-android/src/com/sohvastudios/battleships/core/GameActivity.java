package com.sohvastudios.battleships.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.sohvastudios.battleships.game.core.Main;
import com.sohvastudios.battleships.game.nativeinterface.ConfirmListener;
import com.sohvastudios.battleships.game.nativeinterface.ConnectionHandler;

public class GameActivity extends AndroidApplication {
	
	private NativeActionsImpl nativeActions;
	private ConnectionHandler socketHandler;
	private Main game;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("battleships", "GameActivity launched");
        
        nativeActions = new NativeActionsImpl(this);
        Handler handler = new Handler();
        nativeActions.setHandler(handler);

        bindService(new Intent(this.getApplicationContext(), SocketService.class), serviceConnection, BIND_AUTO_CREATE);
        
        startGame();
    }
	
	public void startGame() {
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
        
        game = new Main(nativeActions);
     
        initialize(game, cfg);
	}

	@Override
	public void onBackPressed() {
		nativeActions.createConfirmDialog(
				"Really leave game?", 
				"This battle is not over yet. Are you going to give up?", 
				"I'm a coward",
				"Return to battle", 
				new ConfirmListener() {
					@Override
					public void yes() {
						socketHandler.leave();
						GameActivity.this.back();
					}
					@Override
					public void no() {
						// Cancel
					}	
				});
	}
	
	private void back() {
		super.onBackPressed();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			Log.d("battleships", "Service connected.");
			socketHandler = ((SocketHandler) binder);
			game.loadGame(socketHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			socketHandler = null;
		}
	};
	
}