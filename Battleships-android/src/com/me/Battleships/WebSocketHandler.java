package com.me.Battleships;

import java.io.IOException;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Core.NativeFunctions;
import GameLogic.Turn;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class WebSocketHandler implements NativeFunctions {
	private static SocketIOClient client;
	private final WebSocketInputHandler socketHandler;
	
	public WebSocketHandler(WebSocketInputHandler handler) {
		this.socketHandler = handler;
	}

	@Override
	public void setLogicHandler(GameLogic.GameLogicHandler logicHandler) {
		socketHandler.setGameLogicHandler(logicHandler);		
	}
	
	@Override
	public void connect() {
		client = new SocketIOClient(URI.create("http://198.211.119.249"), socketHandler);

		client.connect();
	}
	
	@Override
	public void disconnect() {
		try {
			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendReady(Turn t) {
		Log.d("battleships", "emitting ready");
		try {
			client.emit("ready", null);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void sendShoot(Turn t) {
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
			e.printStackTrace();
		}
	}

	@Override
	public void sendResult(Turn t) {
		float[][] hits = t.hits;
		try {
			JSONArray array = new JSONArray();

			for(float[] hit : hits) {
				JSONArray subArray = new JSONArray();

				subArray.put(hit[0]);
				subArray.put(hit[1]);
				array.put(subArray);
			}
			client.emit("result", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
