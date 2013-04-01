package com.me.Battleships;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import GameLogic.GameLogicHandler;
import GameLogic.Turn;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class WebSocketHandler implements SocketIOClient.Handler {
	
	private GameLogicHandler logicHandler;
	
	public void setGameLogicHandler(GameLogicHandler logicHandler) {
		this.logicHandler = logicHandler;
	}
	
	@Override
	public void onConnect() {
		 Log.d("battleships", "Connected!");
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
				turn.x = json.getInt("x");
				turn.y = json.getInt("y");
				turn.weapon = json.getInt("weapon");
				
				Log.d("battleships", "got hit on " + turn.x + " " + turn.y + " by " + turn.weapon);
				
				logicHandler.receiveTurn(turn);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(event.equals("result")) {

			int len = arguments.length();
			JSONArray subArray;

			float[][] hits = new float[len][2];
			try {
				for(int i = 0; i < len; i++) {
					subArray = arguments.getJSONArray(i);
					hits[i][0] = (float) subArray.getDouble(0);
					hits[i][1] = (float) subArray.getDouble(1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Turn t = new Turn(Turn.TURN_RESULT);
			t.hits = hits;
			
			logicHandler.receiveTurn(t);
		}
	}

	@Override
	public void onDisconnect(int code, String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJSON(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception error) {
		Log.e("battleships", "error: " + error.getMessage());
		error.printStackTrace();
	}

}
