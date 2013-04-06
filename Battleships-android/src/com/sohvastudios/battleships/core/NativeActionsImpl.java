package com.sohvastudios.battleships.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.sohvastudios.battleships.game.core.CancelListener;
import com.sohvastudios.battleships.game.core.NativeActions;

public class NativeActionsImpl implements NativeActions, Parcelable {
	
	private ProgressDialog progressDialog;
	private Context ctx;
	
	private SocketIOHandler socketHandler;
	
	
	public NativeActionsImpl(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void launchGameIntent() {
		Intent intent = new Intent(ctx, GameActivity.class);
		intent.putExtra("NativeActions", this);
		intent.putExtra("SocketHandler", socketHandler);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		ctx.startActivity(intent);
	}

	@Override
	public void createProgressDialog(String title, String message, boolean cancelable) {
		progressDialog = ProgressDialog.show(ctx, title, message, true, cancelable);
	}
	
	@Override
	public void createProgressDialog(String title, String message, boolean cancelable, final CancelListener listener) {
		progressDialog = ProgressDialog.show(ctx, title, message, true, cancelable, new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				listener.cancel();
			}
		});
	}

	@Override
	public void dismissProgressDialog() {
		if(progressDialog == null || !progressDialog.isShowing()) {
			return;
		}
		progressDialog.dismiss();
	}
	
	// Parcelable stuff
	
	private int mData;
	
	public NativeActionsImpl(Parcel in) {
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
	
	public static final Parcelable.Creator<NativeActionsImpl> CREATOR = new Parcelable.Creator<NativeActionsImpl>() {
		@Override
		public NativeActionsImpl createFromParcel(Parcel source) {
			return new NativeActionsImpl(source);
		}
		@Override
		public NativeActionsImpl[] newArray(int size) {
			return new NativeActionsImpl[size];
		}
	};

	public void setParcels(SocketIOHandler socketHandler) {
		this.socketHandler = socketHandler;
	}
}
