package com.me.Battleships;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LobbyActivity extends Activity {

    public static WebSocketHandler socketOutputHandler;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        
        WebSocketInputHandler socketInputHandler = new WebSocketInputHandler(this);
        socketOutputHandler = new WebSocketHandler(socketInputHandler);
        socketOutputHandler.connect();
        
        Button matchmake = (Button) findViewById(R.id.matchmake);
        Button join = (Button) findViewById(R.id.join);
        
        matchmake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				socketOutputHandler.matchMake();
			}
		});
        
        join.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(LobbyActivity.this);
				new AlertDialog.Builder(LobbyActivity.this)
					.setTitle("Room name")
					.setMessage("Enter room name")
					.setView(input)
					.setPositiveButton("Join", new DialogInterface.OnClickListener() {			
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CharSequence room = input.getText();
							socketOutputHandler.join(room);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Cancel
						}
					})
					.show();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lobby, menu);
        return true;
    }
    
}
