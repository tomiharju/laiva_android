package com.me.Battleships;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Core.NativeFunctions;
import GameLogic.GameLogicHandler;
import GameLogic.Turn;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class NativeFunctionsImplementation implements NativeFunctions, SocketIOClient.Handler {
	private static SocketIOClient client;
	private static GameLogicHandler logicHandler;
	
	public NativeFunctionsImplementation() {
			//if(client == null) {
				client = new SocketIOClient(URI.create("http://198.211.119.249:8080"), this);
				client.connect();
				Log.d("battleships", "connected");
			//}
	}
	
	public void setGameLogicHandler(GameLogicHandler logicHandler) {
		this.logicHandler = logicHandler;
	}
	
	/*
	 * NativeFunctions methods
	 * 
	 */

	public void helloworld() {	
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		
		try {
			json.put("x", 0);
			json.put("y", 0);
			
			jsonArray.put(json);
			client.emit("turn", jsonArray);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * SocketIOClient.Handler methods
	 * 
	 */

	@Override
	public void onConnect() {
		 Log.d("battleships", "Connected!");
	}

	@Override
	public void on(String event, JSONArray arguments) {
		if(event.equals("ready")) {
			// GameLogicHandler.start();
			// GameLogicHandler.wait();
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
		Log.d("battleships", "error: " + error.getMessage());
		error.printStackTrace();
	}

	@Override
	public void setLogicHandler(GameLogic.GameLogicHandler h) {
		// TODO Auto-generated method stub
		
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
				client.emit("turn", jsonArray);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case Turn.TURN_RESULT:
			break;
		}
	}

}
