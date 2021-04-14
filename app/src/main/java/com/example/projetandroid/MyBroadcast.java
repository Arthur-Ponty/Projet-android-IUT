package com.example.projetandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/** Classe recevant des données sur la batterie puis les écrit dans un fichier **/

public class MyBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Affiche si le portable est en charge ou non
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        //Booléen à true si le portable est en charge ou/et est à 100%

        MainActivity.modifTexte(isCharging);

        //Affiche le % de batterie du téléphone
        int level =  intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale =  intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level / (float)scale)*100;

        MainActivity.ecrireTexte2(""+(int)(batteryPct)+"%");

        //Si la batterie vient d'être modifiée (A modifier)
        if (intent.getAction() == Intent.ACTION_BATTERY_CHANGED) {

            String ligneInfos = ""+(int)(batteryPct);

            //Enregistre la date actuelle et la formate dans un String
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm;dd.MM.yyyy", Locale.getDefault());
            String txtDate = format.format(date);

            ligneInfos += "|" + txtDate + "\n";

            //Ecrit le % et la date dans un fichier
            try {
                FileOutputStream f = context.openFileOutput("test.txt", Context.MODE_APPEND);
                f.write(ligneInfos.getBytes());
                f.close();
            } catch (IOException ioe) { ioe.printStackTrace(); }
        }
    }
}
