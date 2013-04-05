package com.me.Battleships;

import Core.CancelListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LobbyActivity extends Activity {

    public static SocketIOHandler socketHandler;
    public static NativeActionsImpl nativeActions;
    public ProgressDialog progress;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        
        // Create nativeActions for creating progress dialogs
        // Context required for dialogs
        nativeActions = new NativeActionsImpl(this);
        
        // Socket connection listener
        // NativeActions required for handling dialogs and launching game
        SocketIOListener socketInputHandler = new SocketIOListener(nativeActions);
        
        socketHandler = new SocketIOHandler(socketInputHandler);
        socketHandler.connect();
        
        nativeActions.setParcels(socketHandler);
        
        setButtonListeners();
       
    }
	
	public void setButtonListeners() {
		Button matchmake = (Button) findViewById(R.id.matchmake);
        Button join = (Button) findViewById(R.id.join);
        
        matchmake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nativeActions.createProgressDialog("Waiting..", "Waiting for opponent.", true, new CancelListener() {
					@Override
					public void cancel() {
						socketHandler.leave();
					}
				});
				socketHandler.matchMake();
			}
		});
        
        join.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(LobbyActivity.this);
				input.setText("test");
				new AlertDialog.Builder(LobbyActivity.this)
					.setTitle("Room name")
					.setMessage("Enter room name")
					.setView(input)
					.setPositiveButton("Join", new DialogInterface.OnClickListener() {			
						@Override
						public void onClick(DialogInterface dialog, int which) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
							nativeActions.createProgressDialog("Waiting...", "Waiting for opponent.", true, new CancelListener() {
								@Override
								public void cancel() {
									socketHandler.leave();
								}
							});
							CharSequence room = input.getText();
							socketHandler.join(room);
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
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			socketHandler.disconnect();
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
}
