package com.teioh08.djcollab.Utils.Player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PreviewService extends Service {

    private final IBinder mBinder = new PlayerBinder();
    private PreviewPlayer mPlayer = new PreviewPlayer();

    public static Intent getIntent(Context context) {
        return new Intent(context, PreviewService.class);
    }

    public class PlayerBinder extends Binder {
        public PreviewPlayer getService() {
            return mPlayer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
        super.onDestroy();
    }
}
