package com.example.malix.takeabreak.ui.customAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NotificationHandlerService extends NotificationListenerService {


    int check;
    static String[] packages ;
    int size = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        size = intent.getIntExtra("size",0);
        packages = new String[size];
        packages = intent.getStringArrayExtra("packs");
        check = intent.getIntExtra("check",0);

        if(check == 0){

            Toast.makeText(getApplicationContext(),"deactivated",Toast.LENGTH_SHORT).show();
        }else if(check == 1){

            Toast.makeText(getApplicationContext(),"activated",Toast.LENGTH_SHORT).show();
        }

        Log.i("extra","intent Recieved");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String notification = sbn.getKey();
        if (check == 1) {
            for (int i = 0; i < packages.length; i++) {
                if (sbn.getPackageName().equals(packages[i])) {
                    cancelNotification(notification);
                    Toast.makeText(getApplicationContext(), "Notificaion canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
