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


public class SocketListener implements EventCallback, CompletedCallback {
	
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
	public void onCompleted(Exception ex) {
		if(ex != null) {
			connectivityListener.onError();
			return;
		}
		Log.d("battleships", "Disconnected gracefully");
		//connectivityListener.onError();
	}

	@Override
	public void onEvent(String event, JSONArray arguments) {
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

				for(int j = 0; j < len; j++) {
					if(arguments.get(j) == null) {
						Log.d("battleships", "index is null. breaking");
						break;
					}
					JSONObject shot = arguments.getJSONObject(j);

                    JSONArray hits = shot.getJSONArray("hits");
                    ArrayList<Vector3> hitList = new ArrayList<Vector3>();
                    for(int i1=0; i1<hits.length(); i1++) {
                        JSONObject hit = hits.getJSONObject(i1);
                        hitList.add(
                                new Vector3(
                                        (float) hit.getDouble("x"),
                                        (float) hit.getDouble("y"),
                                        0));
                    }

                    JSONArray paths = shot.getJSONArray("path");
                    ArrayList<Vector3> pathList = new ArrayList<Vector3>();
                    for(int i1=0; i1<paths.length(); i1++) {
                        JSONObject path = paths.getJSONObject(i1);
                        pathList.add(
                                new Vector3(
                                        (float) path.getDouble("x"),
                                        (float) path.getDouble("y"),
                                        0));
                    }
					
					//Log.d("battleships", vector.toString());
					result.put(pathList, hitList);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		//	logicHandler.receiveResult(result);
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
}
