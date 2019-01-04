package app.bqlab.vestband;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class UserService extends Service {
    public static String id;
    public static int degree = 0;
    public static boolean isConnected = false;
    public static BluetoothDevice device;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isConnected) {
                        Thread.sleep(2000);
                        Log.d("Degree", Integer.toString(degree));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_NOT_STICKY ;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
