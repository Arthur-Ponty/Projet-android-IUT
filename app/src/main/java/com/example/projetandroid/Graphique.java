package com.example.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Graphique extends AppCompatActivity {

    private ViewGraphique graphique;
    private TextView txtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphique);

        Intent i = getIntent();
        ArrayList<String> alFichier = i.getStringArrayListExtra("fichier");
        this.graphique = new ViewGraphique(this, alFichier);

        this.txtDate = findViewById(R.id.date);
        this.txtDate.setText(this.graphique.chargerDate());

        //La ViewGraphique est ajoutée dynamiquement pour pouvoir lui passer des données en paramètre
        this.addContentView(this.graphique, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    //Méthode qui permet de revenir à la date précédente
    public void datePrecedente(View view) {
        this.txtDate.setText(this.graphique.datePrecedente());
    }

    //Méthode qui permet de revenir à la date suivante
    public void dateSuivante(View view) {
        this.txtDate.setText(this.graphique.dateSuivante());
    }
}
