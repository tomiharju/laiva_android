package com.me.Battleships;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import GameLogic.GameLogicHandler;
import Utilities.Turn;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.codebutler.android_websockets.SocketIOClient;


public class WebSocketInputHandler implements SocketIOClient.Handler {
	
	private GameLogicHandler logicHandler;
	
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
		}
	}

	@Override
	public void onDisconnect(int code, String reason) {
		// TODO Notify logicHandler of disconnection so it can pause game and attempt to reconnect
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
