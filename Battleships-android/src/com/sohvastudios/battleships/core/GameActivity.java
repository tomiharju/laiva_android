package com.sohvastudios.battleships.core;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.sohvastudios.battleships.game.core.ConfirmListener;
import com.sohvastudios.battleships.game.core.Main;
import com.sohvastudios.battleships.game.core.NativeActions;

public class GameActivity extends AndroidApplication {
	
	private NativeActions nativeActions;
	private SocketIOHandler socketHandler;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("battleships", "GameActivity launched");
        
        Intent intent = getIntent();
        
        nativeActions = new NativeActionsImpl(this);
       
        startGame();
    }
	
	public void startGame() {
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
     
        initialize(new Main(socketHandler, nativeActions), cfg);
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
	
	
}