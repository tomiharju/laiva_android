package com.sohvastudios.battleships.core;

import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.codebutler.android_websockets.SocketIOClient;
import com.sohvastudios.battleships.game.core.ConnectionHandler;
import com.sohvastudios.battleships.game.gamelogic.GameLogicHandler;
import com.sohvastudios.battleships.game.utilities.Turn;

public class SocketIOHandler implements ConnectionHandler {
	
	private static SocketIOClient client;
	private static SocketIOListener socketListener;
	
	public SocketIOHandler(SocketIOListener handler) {
		this.socketListener = handler;
	}

	@Override
	public void setLogicHandler(GameLogicHandler logicHandler) {
		Log.d("battleships", "Assigning logicHandler to socketListener");
		if(socketListener == null) {
			Log.d("battleships", "is null :(");
		}
		socketListener.setGameLogicHandler(logicHandler);		
	}
	
	@Override
	public void connect() {
		client = new SocketIOClient(URI.create("http://198.211.119.249:8081"), socketListener);
		client.connect();
	}
	
	@Override
	public void disconnect() {
		Log.d("battleships", "Disconnecting");
		try {
			client.disconnect();
			//client.emit("askDisconnect", null);
			client = null;
		} catch (Exception e) {
			Log.d("battleships", "Error disconnecting.");
			e.printStackTrace();
		} finally {
			client = null;
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
		ArrayList<Vector2> hits = t.hits;
		try {
			JSONArray array = new JSONArray();

			for(Vector2	hit : hits) {
				JSONArray subArray = new JSONArray();

				subArray.put(hit.x);
				subArray.put(hit.y);
				array.put(subArray);
			}
			client.emit("result", new JSONArray().put(array));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void matchMake() {
		try {
			client.emit("matchmake", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void join(CharSequence room) {
		JSONObject json = new JSONObject();
		
		try {
			client.emit("matchMake", null);
			
			json.put("room", room);

			client.emit("join", new JSONArray().put(json));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void leave() {
		try {
			client.emit("leave", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
