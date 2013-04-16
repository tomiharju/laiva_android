package com.sohvastudios.battleships.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.codebutler.android_websockets.SocketIOClient;
import com.sohvastudios.battleships.game.gamelogic.GameLogicHandler;
import com.sohvastudios.battleships.game.interfaces.ConnectivityListener;


public class SocketListener implements SocketIOClient.Handler {
	
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
	public void onConnect() {
		 Log.d("battleships", "Connected!");
		 connectivityListener.onConnect();;
	}
	
	@Override
	public void onDisconnect(int code, String reason) {
		Log.d("battleships", code + " onDisconnect reason: " + reason);
	}

	@Override
	public void onError(Exception error) {
		Log.e("battleships", "Socket Exception:", error);

		connectivityListener.onError();
	}

	@Override
	public void on(String event, JSONArray arguments) {

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
			
			HashMap<ArrayList<Vector3>, ArrayList<Vector3>> result = new HashMap<ArrayList<Vector3>, ArrayList<Vector3>>();

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
					JSONObject shot = arguments.getJSONObject(i);

                    JSONArray hits = shot.getJSONArray("hits");
                    ArrayList<Vector3> hitList = new ArrayList<Vector3>();
                    for(int i=0; i<hits.length(); i++) {
                        JSONObject hit = hits.getJSONObject(i);
                        hitList.add(
                                new Vector3(
                                        (float) hit.getDouble("x"),
                                        (float) hit.getDouble("y"),
                                        0));
                    }

                    JSONArray paths = shot.getJSONArray("path");
                    ArrayList<Vector3> pathList = new ArrayList<Vector3>();
                    for(int i=0; i<paths.length(); i++) {
                        JSONObject path = paths.getJSONObject(i);
                        pathList.add(
                                new Vector3(
                                        (float) path.getDouble("x"),
                                        (float) path.getDouble("y"),
                                        0));
                    }
					
					Log.d("battleships", vector.toString());
					result.put(pathList, hitList);
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
	
	@Override
	public void onJSON(JSONObject json) { }

	@Override
	public void onMessage(String message) {}
}
