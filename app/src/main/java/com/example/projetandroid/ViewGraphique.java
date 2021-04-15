package com.example.projetandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/** Classe créant une view qui permet de dessiner le graphique d'historique de la batterie **/

public class ViewGraphique extends View
{
    private Paint style = new Paint(), styleAxes = new Paint();
    private Activity activite;
    private ArrayList<Float>  alPct    = new ArrayList<Float>(),
            alHeure  = new ArrayList<Float>(),
            alMinute = new ArrayList<Float>();
    private ArrayList<String> alDate   = new ArrayList<String>(),
            alListeDates = new ArrayList<String>();
    private String dateSelectionnee;
    private int indiceDateSelect;
    private float largeur, hauteur;
    private int nbHeuresTotal, heureMin, indiceHeureMin;
    //Variables contenant le nombre d'heures à afficher ainsi que la plus petite heure à afficher

    public ViewGraphique(Context context, ArrayList<String> alFichier) {
        super(context);
        setFocusable(true);

        this.activite = (Activity) context;
        this.initArrayLists(alFichier);

        style.setColor(Color.GREEN);
        style.setStyle(Paint.Style.STROKE);
        style.setStrokeWidth(3);

        styleAxes.setColor(Color.BLACK);
        styleAxes.setStyle(Paint.Style.STROKE);
        styleAxes.setStrokeWidth(2);
        styleAxes.setTextSize(20);

        //Permet de connaître la taille du téléphone
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        largeur = metrics.widthPixels;
        hauteur = metrics.heightPixels;

        this.creerListeDates();

        this.indiceDateSelect = this.alListeDates.size()-1; //Au départ, la date selectionnée est la plus récente des dates enregistrées
        this.chargerDate();
    }

    private void initArrayLists(ArrayList<String> alFichier) {
        //Remplit les 3 arraylist grâce aux infos données en paramètres (lues du fichier)
        for (String ligne : alFichier) {
            char[] tabLigne = ligne.toCharArray();
            String valeur = "";

            for (int i=0; i<tabLigne.length; i++) {
                if (tabLigne[i] == '|') {
                    alPct.add(Float.parseFloat(valeur));
                    valeur = "";
                }
                else if (tabLigne[i] == ':') {
                    alHeure.add(Float.parseFloat(valeur));
                    valeur = "";
                }
                else if (tabLigne[i] == ';') {
                    alMinute.add(Float.parseFloat(valeur));
                    valeur = "";
                }
                else
                    valeur += tabLigne[i];
            }
            alDate.add(valeur);
        }
    }

    //Créé une liste contenant en une seule fois chaque date enregistrée
    private void creerListeDates() {
        for (int i=0; i<alDate.size(); i++) {
            if (!this.alListeDates.contains(alDate.get(i)))
                this.alListeDates.add(alDate.get(i));
        }
    }

    //Retourne la date d'aujourd'hui
    public String getDateDuJour() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return format.format(date);
    }

    //Modifie la date sélectionnée selon l'indiceDate
    public String chargerDate() {
        this.dateSelectionnee = this.alListeDates.get(indiceDateSelect);
        this.invalidate(); //Redessine la view

        if (this.dateSelectionnee.equals(this.getDateDuJour()))
            return "Aujourd'hui";

        return this.dateSelectionnee;
    }

    //Méthode qui permet de revenir à la date précédente
    public String datePrecedente() {
        if (indiceDateSelect>0)
            indiceDateSelect--;
        return this.chargerDate();
    }

    //Méthode qui permet de revenir à la date suivante
    public String dateSuivante() {
        if (indiceDateSelect<this.alListeDates.size()-1)
            indiceDateSelect++;
        return this.chargerDate();
    }



    //Calcule le nombre d'heures et la plus petite heure à afficher
    public void calculerNbHeures() {
        float max = 0, min = 0;
        float minutes = 0;
        this.indiceHeureMin = 0;

        //Calcule le nombre d'heures à afficher
        for (int i=0; i<alPct.size()-1; i++) {
            if (alDate.get(i).equals(dateSelectionnee) && alDate.get(i + 1).equals(dateSelectionnee)) {
                minutes = alHeure.get(i)*60 + alMinute.get(i);

                if (min == 0)
                    min = max = minutes;
                else if (minutes < min)
                    min = minutes;
                else if (minutes > max)
                    max = minutes;

                if (this.indiceHeureMin == 0) this.indiceHeureMin = i;
            }
        }

        int heureMax = (int)Math.floor(max/60);
        this.heureMin = (int)Math.floor(min/60);
        this.nbHeuresTotal = heureMax - heureMin + 1;
    }

    //L'échelle du dessin de la courbe est déterminée à partir du nombre d'heures enregistrées pour un seul jour sélectionné
    public void onDraw(Canvas canvas) {
        this.calculerNbHeures();

        //Dessine l'axe des ordonnées puis celui des abscisses
        canvas.drawLine(largeur/4, 10,
                largeur/4, hauteur/2 + 10, styleAxes);
        canvas.drawLine(largeur/4, hauteur/2 + 10,
                largeur/4+largeur/2, hauteur/2 + 10, styleAxes);
        //On a toujours un +10 sur la hauteur pour éviter que les dessins soient dans le bloc en haut de l'appli

        //Dessine la courbe
        for (int i=0; i<alPct.size()-1; i++) {
            if (alDate.get(i).equals(dateSelectionnee) && alDate.get(i+1).equals(dateSelectionnee)) {
                //Change la couleur de la courbe selon s'il y a une perte ou un gain de batterie
                if (alPct.get(i) < alPct.get(i+1))      style.setColor(Color.GREEN);
                else if (alPct.get(i) > alPct.get(i+1)) style.setColor(Color.RED);

                canvas.drawLine(largeur/4 + (largeur/2) * ((alHeure.get(i)-heureMin)  /nbHeuresTotal) + (alMinute.get(i)/60)  * (largeur/2/nbHeuresTotal), (hauteur / 2) - (float)alPct.get(i)     * (hauteur / 2) / 100 + 10,
                        largeur/4 + (largeur/2) * ((alHeure.get(i+1)-heureMin)/nbHeuresTotal) + (alMinute.get(i+1)/60)* (largeur/2/nbHeuresTotal), (hauteur / 2) - (float)alPct.get(i + 1) * (hauteur / 2) / 100 + 10, style);
            }
        }

        //Dessine les tirets pour graduer les heures
        for (int i=0; i<nbHeuresTotal; i++) {
            boolean aDessiner = true;

            //Lorsqu'il y a plus de 12h enregistrées pour la journée, 1h sur 2 est affichée pour problème de lisibilité
            if (nbHeuresTotal>=12 && i%2==1)
                aDessiner = false;

            if (aDessiner) {
                this.dessinerGraduationX(largeur / 4 + (largeur / 2) * ((float) i / nbHeuresTotal),
                        hauteur / 2 + 10,
                        "" + (int) (i + this.alHeure.get(indiceHeureMin)) + "h",
                        canvas);

                //Lorqu'il y a moins de 4 heures à afficher, on affiche les demies-heures
                if (nbHeuresTotal <=4) {
                    this.dessinerGraduationX(largeur / 4 + (largeur / 2) * ((float) i / nbHeuresTotal) + (float)0.5 * (largeur/2/nbHeuresTotal),
                            hauteur / 2 + 10,
                            "" + (int) (i + this.alHeure.get(indiceHeureMin)) + "h30",
                            canvas);
                }

                //Lorqu'il y a moins de 2 heures à afficher, on affiche les quarts d'heures
                if (nbHeuresTotal <=2) {
                    this.dessinerGraduationX(largeur / 4 + (largeur / 2) * ((float) i / nbHeuresTotal) + (float)0.25 * (largeur/2/nbHeuresTotal),
                            hauteur / 2 + 10,
                            "" + (int) (i + this.alHeure.get(indiceHeureMin)) + "h15",
                            canvas);

                    this.dessinerGraduationX(largeur / 4 + (largeur / 2) * ((float) i / nbHeuresTotal) + (float)0.75 * (largeur/2/nbHeuresTotal),
                            hauteur / 2 + 10,
                            "" + (int) (i + this.alHeure.get(indiceHeureMin)) + "h45",
                            canvas);
                }
            }
        }

        //Dessine les 11 tirets pour graduer les pourcentages (unité de l'axe des ordonnées)
        for (int i=0; i<=10; i++) {
            //Dessine le tiret
            styleAxes.setStrokeWidth(3);
            canvas.drawLine(largeur/4,    hauteur/2 * ((float)i/10) + 10,
                    largeur/4-15, hauteur/2 * ((float)i/10) + 10, styleAxes);

            //Dessine le pourcentage correspondant
            styleAxes.setStrokeWidth(2);
            canvas.drawText(""+(100-i*10)+"%", largeur/4-60, hauteur/2 * ((float)i/10) + 10 + 10, styleAxes);
            //un autre +10 pour que les % soient bien alignés avec les tirets
        }
    }

    //Méthode qui dessine une graduation sur l'axe des abscisses
    public void dessinerGraduationX(float x, float y, String valeur, Canvas canvas) {
        //Dessine le tiret
        styleAxes.setStrokeWidth(3);
        canvas.drawLine(x, y, x, y + 15, styleAxes);

        //Dessine le pourcentage correspondant
        styleAxes.setStrokeWidth(2);
        canvas.drawText(valeur, x-10, y+40, styleAxes);
    }
}