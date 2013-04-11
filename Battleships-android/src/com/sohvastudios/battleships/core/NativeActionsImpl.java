package com.sohvastudios.battleships.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sohvastudios.battleships.game.interfaces.CancelListener;
import com.sohvastudios.battleships.game.interfaces.ConfirmListener;
import com.sohvastudios.battleships.game.interfaces.NativeActions;

public class NativeActionsImpl implements NativeActions {
	
	private static ProgressDialog progressDialog;
	private final Context ctx;
	
	private Handler handler;
	
	public NativeActionsImpl(Context ctx) {
		this.ctx = ctx;
		handler = new Handler();
	}
	
	public void launchGameIntent() {
		Intent intent = new Intent(ctx, GameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}
	
	@Override
	public void createProgressDialog(final String title, final String message, final boolean cancelable, final CancelListener listener) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				progressDialog = ProgressDialog.show(ctx, title, message, true, cancelable, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						listener.cancel();
					}
				});
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
	public void createConfirmDialog(final String title, final String message, final String yes, final String no, final ConfirmListener confirmListener) {
		Log.d("battleships", "Creating confirm dialog");
		handler.post(new Runnable() {
			@Override
			public void run() {
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
		});
	}
	
	@Override
	public void createToast(final String text, final int duration) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ctx, text, duration).show();
			}
		});
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
