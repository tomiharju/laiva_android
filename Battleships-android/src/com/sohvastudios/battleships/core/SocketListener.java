package com.sohvastudios.battleships.core;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.SocketIOClient.EventCallback;
import com.sohvastudios.battleships.game.gamelogic.GameLogicHandler;
import com.sohvastudios.battleships.game.interfaces.ConnectivityListener;


public class SocketListener implements EventCallback, CompletedCallback {
	
	private NativeActionsImpl nativeActions;
	private GameLogicHandler logicHandler;
	private ConnectivityListener connectivityListener;
	
	public void setNativeActionsHandler(NativeActionsImpl nativeActions) {
		this.nativeActions = nativeActions;
	}
	
	public void setGameLogicHandler(GameLogicHandler logicHandler) {
		this.logicHandler = logicHandler;
	}
	
	public void setConnectivityListener(ConnectivityListener lobbyHandler) {
		this.connectivityListener = lobbyHandler;
	}
	
	@Override
	public void onCompleted(Exception ex) {
		if(ex != null) {
			connectivityListener.onError();
			return;
		}
		Log.d("battleships", "Disconnected gracefully");
		//connectivityListener.onError();
	}

	@Override
	public void onEvent(String event, JSONArray arguments) {
		if(event.equals("start")) {
			Log.d("battleships", "received start");
			logicHandler.receiveStart();
			
		} else if(event.equals("wait")) {
			Log.d("battleships", "received wait");
			nativeActions.dismissProgressDialog();
			logicHandler.receiveWait();
			
		} else if(event.equals("shoot")) {
			Log.d("battleships", "received shoot");
			
			float x, y;
			int weapon;

			try {
				JSONObject json = arguments.getJSONObject(0);

				x = (float) json.getDouble("x");
				y = (float) json.getDouble("y");
				weapon = json.getInt("weapon");
				
				logicHandler.receiveShoot(x, y, weapon);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(event.equals("result")) {
			// Result after shoot
			
			ArrayList<Vector2> result = new ArrayList<Vector2>();

			try {
				arguments = arguments.getJSONArray(0);

				int len = arguments.length();
				JSONArray subArray;
						
				Log.d("battleships", "Receiving result with " + len + " hits.");

				
				for(int i = 0; i < len; i++) {
					if(arguments.get(i) == null) {
						Log.d("battleships", "index is null. breaking");
						break;
					}
					
					subArray = arguments.getJSONArray(i);
					Vector2 vector = new Vector2();
					vector.x = (float) subArray.getDouble(0);
					vector.y = (float) subArray.getDouble(1);
					
					Log.d("battleships", vector.toString());
					result.add(vector);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			logicHandler.receiveResult(result);
		} else if(event.equals("launch")) {
			// Launch game
			Log.d("battleships", "Launching");
			
			nativeActions.dismissProgressDialog();
			nativeActions.launchGameIntent();
		} else if(event.equals("playerLeft")) {
			Log.d("battleships", "Opponent left");
			
			logicHandler.opponentLeft();
		}
	}
}
