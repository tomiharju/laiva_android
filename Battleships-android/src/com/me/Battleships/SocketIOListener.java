package com.me.Battleships;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Core.NativeActions;
import GameLogic.GameLogicHandler;
import Utilities.Turn;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.codebutler.android_websockets.SocketIOClient;


public class SocketIOListener implements SocketIOClient.Handler {
	private final NativeActions nativeActions;
	
	private GameLogicHandler logicHandler;
	
	public SocketIOListener(NativeActions nativeActions) {
		this.nativeActions = nativeActions;
	}
	
	public void setGameLogicHandler(GameLogicHandler logicHandler) {
		this.logicHandler = logicHandler;
	}
	
	@Override
	public void onConnect() {
		 Log.d("battleships", "Connected!");
		 // TODO Notify logicHandler of successful connection
	}

	@Override
	public void on(String event, JSONArray arguments) {

		if(event.equals("start")) {
			Log.d("battleships", "received start");
			logicHandler.receiveTurn(new Turn(Turn.TURN_START));
			
		} else if(event.equals("wait")) {
			Log.d("battleships", "received wait");
			nativeActions.dismissProgressDialog();
			logicHandler.receiveTurn(new Turn(Turn.TURN_WAIT));
			
		} else if(event.equals("shoot")) {
			Log.d("battleships", "received shoot");

			try {
				JSONObject json = arguments.getJSONObject(0);
				Turn turn = new Turn(Turn.TURN_SHOOT);
				turn.x = (float) json.getDouble("x");
				turn.y = (float) json.getDouble("y");
				turn.weapon = json.getInt("weapon");
				
				Log.d("battleships", "got hit on " + turn.x + " " + turn.y + " by " + turn.weapon);
				
				logicHandler.receiveTurn(turn);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(event.equals("result")) {
			// Result after shoot
			
			ArrayList<Vector2> hits = new ArrayList<Vector2>();

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
					hits.add(vector);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Turn turn = new Turn(Turn.TURN_RESULT);
			turn.hits = hits;
			
			logicHandler.receiveTurn(turn);
		} else if(event.equals("launch")) {
			// Launch game
			Log.d("battleships", "Launching");
			
			nativeActions.dismissProgressDialog();
			nativeActions.launchGameIntent();
		}
	}

	@Override
	public void onDisconnect(int code, String reason) {
		// TODO Notify logicHandler of disconnection so it can pause game and attempt to reconnect
		Log.d("battleships", code + " onDisconnect reason: " + reason);
	}

	@Override
	public void onError(Exception error) {
		Log.e("battleships", "error: " + error.getMessage());
		error.printStackTrace();
		// TODO Notify logicHandler of error in connection
	}
	
	@Override
	public void onJSON(JSONObject json) { }

	@Override
	public void onMessage(String message) {}
}
