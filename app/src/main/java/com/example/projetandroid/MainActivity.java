package com.example.projetandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static TextView tv, tvPct, tvFichier;
    private MyBroadcast mybr;
    private IntentFilter batteryIF;
    private Intent batteryStatus, intentGraphique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        tvPct = findViewById(R.id.tvPct);
        tvFichier = findViewById(R.id.tvFichier);
        this.intentGraphique = new Intent(this, Graphique.class);

        mybr = new MyBroadcast();
        batteryIF = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); //Mettre l'action en paramètre fait pareil que quand on fait addAction
        batteryIF.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        batteryIF.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        //Le BroadCast Receiver fait quelque chose quand le portable est branché, débranché, ou bien que la batterie change

        //Calcule la batterie une 1ère fois lors du lancement de l'application
        batteryStatus = registerReceiver(mybr, batteryIF);
        int level =  batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale =  batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level / (float)scale)*100;

        //Booléen à true si la batterie est en charge et/ou à 100%
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        //Modifie les textfields avec les paramètres correspondant
        ecrireTexte2(""+(int)(batteryPct)+"%");
        modifTexte(isCharging);
    }

    //Méthode qui permet de lancer l'activité qui gère le jeu
    public void lancerJeu(View view)
    {
        Intent intent = new Intent(this, Jeu.class);
        startActivityForResult(intent, 1);
    }

    //Méthode qui récupère le résultat d'une activité
    public void onActivityResult(int code, int returnCode, Intent resultat)
    {
        int res = resultat.getIntExtra("myResult", 1);
        TextView txtView = (TextView) findViewById(R.id.score);
        String txt = "score : ";

        txtView.setText(txt + res);
        super.onActivityResult(code, returnCode, resultat);
    }

    //Méthode qui s'appelle quand l'appli est redemarré
    protected void onResume() {
        super.onResume();
    }

    //Méthode qui s'appelle quand l'appli passe en pause
    protected void onPause() {
        super.onPause();
    }

    //Modifie le textfield 1 selon si le portable charge ou non
    public static void modifTexte(boolean isCharging) {
        if (isCharging) {
            MainActivity.ecrireTexte("Portable en charge ");
        } else { MainActivity.ecrireTexte("Le chargeur n'est pas branché "); }
    }

    //Modifie le textfield 1
    public static void ecrireTexte(String texte) {
        tv.setText(texte);
    }

    //Modifie le textfield 2
    public static void ecrireTexte2(String texte) {
        tvPct.setText(texte);
    }

    public ArrayList<String> lireFichier() {
        //Lis le fichier en entier et le rentre dans une arraylist
        ArrayList<String> lignes = new ArrayList<String>();
        String ligneTempo = "";

        try {
            FileInputStream f = openFileInput("test.txt");
            int c;

            while ((c = f.read()) != -1) {
                if (Character.toString((char) c).equals("\n")) {
                    lignes.add(ligneTempo);
                    ligneTempo = "";
                } else
                    ligneTempo = ligneTempo + (char) c;
            }

            f.close();
        } catch (IOException ioe) {}

        return lignes;
    }

    //Lance l'activité pour voir les statistique de la batterie
    public void visStat(View view) {
        this.intentGraphique.putStringArrayListExtra("fichier", this.lireFichier());
        startActivity(this.intentGraphique, new Bundle());
    }

    //Méthode qui quitte l'appli
    public void fin(View view) { finish(); }
}
