package app.bqlab.vestband;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BluetoothService extends Service implements Runnable {
    public static String id;
    public static int degree = 0;
    public static boolean isConnected = false;
    public static BluetoothDevice device;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        while (isConnected) {
            try {
                Thread.sleep(1000);
                Log.d("Data", String.valueOf(degree));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
