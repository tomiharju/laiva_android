package com.me.Battleships;

import Core.Main;
import Core.NativeFunctions;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements NativeFunctions{
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        //Open socket here
        //Connect to server
        //
        startGame();
        
        //After everything is finished, startGame()
    }

	@Override
	public void helloworld() {
		//Android specific function calls
		
	}
	
	public void startGame(){
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
        initialize(new Main(this), cfg);
	}
	
	
}