package com.sohvastudios.battleships.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sohvastudios.battleships.game.interfaces.CancelListener;

import com.sohvastudios.battleships.game.interfaces.ConfirmListener;
import com.sohvastudios.battleships.game.interfaces.ConnectivityListener;


public class LobbyActivity extends Activity implements ConnectivityListener {

    public SocketHandler socketHandler;
    public NativeActionsImpl nativeActions;

    private TextView status;
    private Handler handler;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        
        handler = new Handler();
        
        // Create nativeActions for creating progress dialogs
        nativeActions = new NativeActionsImpl(this);
        
        nativeActions.createProgressDialog("Connecting", "Connecting, please wait", true, new CancelListener() {
			@Override
			public void cancel() {
				// Cancel
			}      	
        });
    
        bindService(new Intent(this, SocketService.class), serviceConnection, BIND_AUTO_CREATE);
        
        if(socketHandler == null) {
        	Log.d("battleships", "state is null");
        }
        
        status = (TextView) findViewById(R.id.status);
        
        setButtonListeners();
       
    }
	
	public void setButtonListeners() {
		Button matchmakeBtn = (Button) findViewById(R.id.matchmakeBtn);
        Button joinBtn = (Button) findViewById(R.id.joinBtn);
        
        matchmakeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nativeActions.createProgressDialog("Matchmaking", "Looking for opponent", true, new CancelListener() {
					@Override
					public void cancel() {
						socketHandler.leave();
					}
				});
				socketHandler.matchMake();
			}
		});
        
        joinBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(LobbyActivity.this);
				input.setText("test");
				
				new AlertDialog.Builder(LobbyActivity.this)
					.setTitle("Custom game")
					.setMessage("Enter game name")
					.setView(input)
					.setPositiveButton("Join", new DialogInterface.OnClickListener() {			
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Hide keyboard
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
							
							CharSequence room = input.getText();
							
							nativeActions.createProgressDialog("Game " + room, "Waiting for opponent", true, new CancelListener() {
								@Override
								public void cancel() {
									socketHandler.leave();
								}
							});
							
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
			Log.d("battleships", "Pressed disconnect");
			socketHandler.disconnect();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		
		socketHandler.disconnect();
		
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
			socketHandler = ((SocketHandler) binder);
			socketHandler.setConnectivityListener(LobbyActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			socketHandler = null;
		}
	};

	@Override
	public void onConnect() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				nativeActions.dismissProgressDialog();
				status.setText("Connected");
			}
		});
	}
	
	@Override
	public void onError() {
		nativeActions.createConfirmDialog(
				"Disconnected", 
				"Connection to server was lost",
				"Attempt reconnect", 
				"Exit", 
				new ConfirmListener() {
					@Override
					public void yes() {
						socketHandler.connect();
					}

					@Override
					public void no() {
						finish();
					}
		});
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				status.setText("Connection error");
			}
		});
	}
	
}
