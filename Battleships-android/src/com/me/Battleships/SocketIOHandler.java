package com.me.Battleships;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Core.ConnectionHandler;
import Utilities.Turn;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.codebutler.android_websockets.SocketIOClient;

public class SocketIOHandler implements ConnectionHandler, Parcelable {
	
	private static SocketIOClient client;
	private static SocketIOListener socketListener;
	
	public SocketIOHandler(SocketIOListener handler) {
		this.socketListener = handler;
	}

	@Override
	public void setLogicHandler(GameLogic.GameLogicHandler logicHandler) {
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
		} catch (IOException e) {
			Log.d("battleships", "Error disconnecting.");
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
	
	public void leave() {
		try {
			client.emit("leave", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Parcelable stuff
	
	private int mData;
	
	private SocketIOHandler(Parcel in) {
		mData = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mData);
	}
	
	public static final Parcelable.Creator<SocketIOHandler> CREATOR = new Parcelable.Creator<SocketIOHandler>() {
		@Override
		public SocketIOHandler createFromParcel(Parcel source) {
			return new SocketIOHandler(source);
		}
		@Override
		public SocketIOHandler[] newArray(int size) {
			return new SocketIOHandler[size];
		}
	};
}
