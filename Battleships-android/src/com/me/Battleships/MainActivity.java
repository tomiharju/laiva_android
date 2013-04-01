package com.me.Battleships;

import Core.Main;
import Core.NativeFunctions;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication{
	
	private NativeFunctions nativeFunctions;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSocketHandler handler = new WebSocketHandler();

        nativeFunctions = new NativeFunctionsImplementation(handler);
        startGame();
    }

	
	
	public void startGame(){
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
     
        initialize(new Main(nativeFunctions), cfg);
	}


	
	
	
}