package com.sohvastudios.battleships.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.sohvastudios.battleships.game.core.CancelListener;
import com.sohvastudios.battleships.game.core.ConfirmListener;
import com.sohvastudios.battleships.game.core.NativeActions;

public class NativeActionsImpl implements NativeActions {
	
	private static ProgressDialog progressDialog;
	private final Context ctx;
	
	public NativeActionsImpl(Context ctx) {
		this.ctx = ctx;
	}
	
	public void launchGameIntent() {
		Intent intent = new Intent(ctx, GameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
			Log.d("battleships", "No ProgressDialog to be dismissed.");
			return;
		}
		progressDialog.dismiss();
	}
	
	@Override
	public void createConfirmDialog(String title, String message, String yes, String no, final ConfirmListener confirmListener) {
		new AlertDialog.Builder(ctx)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(yes, new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					confirmListener.yes();				}
			})
			.setNegativeButton(no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					confirmListener.no();
				}
			})
			.show();
	}
}
