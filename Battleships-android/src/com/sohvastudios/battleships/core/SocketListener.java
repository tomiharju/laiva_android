package com.sohvastudios.battleships.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.SocketIOClient.EventCallback;
import com.sohvastudios.battleships.game.gamelogic.GameLogicHandler;
import com.sohvastudios.battleships.game.interfaces.ConnectivityListener;


public class SocketListener implements EventCallback {
	
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
	public void onEvent(String event, JSONArray arguments) {
		if("start".equals(event)) {
			Log.d("battleships", "received start");
			logicHandler.receiveStart();
			
		} else if("wait".equals(event)) {
			Log.d("battleships", "received wait");
			nativeActions.dismissProgressDialog();
			logicHandler.receiveWait();
			
		} else if("shoot".equals(event)) {
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
			
		} else if("result".equals(event)) {
			// Result after shoot
			
			HashMap<ArrayList<Vector3>, ArrayList<Vector3>> result = new HashMap<ArrayList<Vector3>, ArrayList<Vector3>>();

			try {
				arguments = arguments.getJSONArray(0);

				Log.d("battleships", arguments.toString(4));

				int len = arguments.length();

				for(int j = 0; j < len; j++) {
					if(arguments.get(j) == null) {
						Log.d("battleships", "index is null. breaking");
						break;
					}
					JSONObject shot = arguments.getJSONObject(j);

                    JSONArray path = shot.getJSONArray("path");
                    ArrayList<Vector3> pathList = new ArrayList<Vector3>();
                    for(int i=0; i<path.length(); i++) {
                        JSONObject pathPoint = path.getJSONObject(i);
                        pathList.add(
                                new Vector3(
                                        (float) pathPoint.getDouble("x"),
                                        (float) pathPoint.getDouble("y"),
                                        0));
                    }

					JSONArray hits = shot.getJSONArray("hits");
					ArrayList<Vector3> hitList = new ArrayList<Vector3>();
					for(int i=0; i<hits.length(); i++) {
						JSONObject hitPoint = hits.getJSONObject(i);
						hitList.add(
								new Vector3(
										(float) hitPoint.getDouble("x"),
										(float) hitPoint.getDouble("y"),
										(float) hitPoint.getDouble("z")));
					}

					result.put(pathList, hitList);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.d("battleships", result.toString());

			logicHandler.receiveResult(result);
		} else if("launch".equals(event)) {
			// Launch game
			Log.d("battleships", "Launching");
			nativeActions.dismissProgressDialog();
			nativeActions.launchGameIntent();
		} else if("playerLeft".equals(event)) {
			Log.d("battleships", "Opponent left");
			
			logicHandler.opponentLeft();
		}
	}
}
