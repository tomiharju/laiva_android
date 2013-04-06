package com.sohvastudios.battleships.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.sohvastudios.battleships.game.core.Main;
import com.sohvastudios.battleships.game.core.NativeActions;

public class GameActivity extends AndroidApplication {
	
	private NativeActions nativeActions;
	private SocketIOHandler socketHandler;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("battleships", "GameActivity launched");
        
        Intent i = getIntent();
        
        nativeActions = (NativeActions) i.getParcelableExtra("NativeActions");
        socketHandler = (SocketIOHandler) i.getParcelableExtra("SocketHandler");
        Log.d("battleships", socketHandler.toString());
       
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
	public void onBackPressed() {
		new AlertDialog.Builder(this)
			.setTitle("Really leave game?")
			.setMessage("This battle is not over yet. Really give up?")
			.setPositiveButton("I'm a coward.", new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					socketHandler.leave();
					GameActivity.this.back();
				}
			})
			.setNegativeButton("Back to the batle!.", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Cancel
				}
			})
			.show();
	}
	
	private void back() {
		super.onBackPressed();
	}
	
	
}