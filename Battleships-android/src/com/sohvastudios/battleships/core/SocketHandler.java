package com.sohvastudios.battleships.core;

import java.util.ArrayList;

import com.koushikdutta.async.callback.CompletedCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Binder;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;
import com.koushikdutta.async.http.SocketIOClient.SocketIOConnectCallback;
import com.sohvastudios.battleships.game.gamelogic.GameLogicHandler;
import com.sohvastudios.battleships.game.interfaces.ConnectionHandler;
import com.sohvastudios.battleships.game.interfaces.ConnectivityListener;

public class SocketHandler extends Binder implements ConnectionHandler {
	
	private SocketIOClient client;
	private final SocketListener socketListener;
	private ConnectivityListener connectivityListener;
	
	public SocketHandler(SocketListener socketListener) {
		Log.d("battleships", "SocketHandler created");
		this.socketListener = socketListener;
		
		SocketIOClient.connect(
				AsyncHttpClient.getDefaultInstance(),
				"http://198.211.119.249:8081", 
				new SocketIOConnectCallback() {		
					@Override
					public void onConnectCompleted(Exception ex, SocketIOClient client) {
						//connectivityListener.onConnect();
						
						SocketHandler.this.client = client;
						
						client.setEventCallback(SocketHandler.this.socketListener);
						client.setClosedCallback(new CompletedCallback() {
							
							@Override
							public void onCompleted(Exception ex) {
								if(ex != null) {
                                    Log.e("battleships", "Socket error", ex);
									connectivityListener.onError();
									return;
								}
								Log.d("battleships", "Disconnected gracefully");
								//connectivityListener.onError();								
							}
						});
						
						//client.disconnect();
					}
				});
	}
	
	@Override
	public void connect() {
		
	}
	
	@Override
	public void setLogicHandler(GameLogicHandler logicHandler) {
		Log.d("battleships", "Setting GameLogicHandler");
		socketListener.setGameLogicHandler(logicHandler);
	}
	
	public void setConnectivityListener(ConnectivityListener lobbyHandler) {
		this.connectivityListener = lobbyHandler;
		socketListener.setConnectivityListener(lobbyHandler);
	}

	@Override
	public void disconnect() {
		Log.d("battleships", "Disconnecting");
		try {
			//client.disconnect();
			client.emit("askDisconnect", null);
		} catch (Exception e) {
			Log.d("battleships", "Error disconnecting.");
			e.printStackTrace();
		} finally {
            client = null;
        }
	}

	
	@Override
	public void matchMake() {
		try {
			client.emit("matchmake", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void join(CharSequence room) {
		JSONObject json = new JSONObject();
		
		try {
			client.emit("matchMake", null);
			
			json.put("room", room);

			client.emit("join", new JSONArray().put(json));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void leave() {
		try {
			client.emit("leave", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendReady() {
		Log.d("battleships", "emitting ready");
		try {
			client.emit("ready", null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void sendShoot(float x, float y, int weapon) {
		Log.d("battleships", "emitting turn");

		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		
		try {
			json.put("x", x);
			json.put("y", y);
			json.put("weapon", weapon);
			
			jsonArray.put(json);
			client.emit("shoot", jsonArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendResult(ArrayList<Vector2> results) {

		try {
			JSONArray array = new JSONArray();

			for(Vector2	hit : results) {
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
}
