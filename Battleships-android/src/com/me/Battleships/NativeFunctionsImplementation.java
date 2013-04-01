package com.me.Battleships;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Core.NativeFunctions;
import GameLogic.Turn;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class NativeFunctionsImplementation implements NativeFunctions {
	private static SocketIOClient client;
	private final WebSocketHandler socketHandler;
	
	public NativeFunctionsImplementation(WebSocketHandler handler) {
		this.socketHandler = handler;
	}

	@Override
	public void setLogicHandler(GameLogic.GameLogicHandler logicHandler) {
		socketHandler.setGameLogicHandler(logicHandler);
		
		client = new SocketIOClient(URI.create("http://198.211.119.249"), socketHandler);
		client.connect();
		Log.d("battleships", "connected");		
	}

	@Override
	public void sendTurn(Turn t) {
		Log.d("battleships", client.toString());

		int type = t.type;
		
		switch(type) {
		case Turn.TURN_READY:
			Log.d("battleships", "emitting ready");
			try {
				client.emit("ready", null);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case Turn.TURN_SHOOT:
			Log.d("battleships", "emitting turn");

			JSONArray jsonArray = new JSONArray();
			JSONObject json = new JSONObject();
			
			try {
				json.put("x", t.x);
				json.put("y", t.y);
				json.put("weapon", t.weapon);
				
				jsonArray.put(json);
				client.emit("shoot", jsonArray);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case Turn.TURN_RESULT:
			float[][] hits = t.hits;
			try {
				JSONArray array = new JSONArray();
				JSONArray subArray;

				for(float[] hit : hits) {
					subArray = new JSONArray();

					subArray.put(hit[0]);
					subArray.put(hit[1]);
					array.put(subArray);
				}
				client.emit("result", array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

}
