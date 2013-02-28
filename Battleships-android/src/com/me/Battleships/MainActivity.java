package com.me.Battleships;

import Core.Main;
import Core.NativeConnector;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements NativeConnector{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useCompass =false;
        cfg.useAccelerometer = false;
        cfg.useWakelock=true;
        initialize(new Main(this), cfg);
    }

	@Override
	public void helloworld() {
		//Android specific function calls
		
	}
	
	
}