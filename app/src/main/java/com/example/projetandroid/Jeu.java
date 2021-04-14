package com.example.projetandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class Jeu extends AppCompatActivity implements SensorEventListener {

    private Sensor accelerometre;
    private SensorManager manager;
    private ViewDessin viewDessin;
    public static int nbPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewDessin = new ViewDessin(this);
        setContentView(viewDessin);

        manager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        accelerometre = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Jeu.nbPoints = 0;
    }

    //Méthode qui s'appelle quand l'appli est redemarré
    public void onResume()
    {
        manager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    //Méthode qui s'appelle quand l'appli passe en pause
    public void onPause()
    {
        manager.unregisterListener(this);
        super.onPause();
    }

    //Méthode qui s'appelle quand l'accéléromètre à un changement de valeur
    public void onSensorChanged(SensorEvent event) {
        float mSensorX = event.values[0];
        float mSensorY = event.values[1];

        this.viewDessin.deplacer(mSensorX, mSensorY);
    }

    //Méthode qui s'appelle quand l'accéléromètre à un changement de précision
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

}

//Classe qui gère la view où se passe le jeu
class ViewDessin extends View
{
    private Paint dessinCercle = new Paint();
    private ArrayList<Cercle> alCercle = new ArrayList<Cercle>();
    private int hauteur, longueur;
    public static final int RAYON = 25;
    private Activity context;

    //Le constructeur
    public ViewDessin(Context context)
    {
        super(context);
        setFocusable(true);
        this.context = (Activity) context;
        this.dessinCercle.setAntiAlias(true);
        this.dessinCercle.setStrokeWidth(5);

        //Récupère le nombre de pixel en hauteur et en longueur du téléphone
        this.hauteur  = (int) context.getResources().getDisplayMetrics().heightPixels;
        this.longueur = (int) context.getResources().getDisplayMetrics().widthPixels;

        //Dessine 2 premiers cercle, 1 bleu et 1 vert
        this.nouveauCercle(Color.BLUE);
        this.nouveauCercle(Color.GREEN);
    }

    //Ajoute un nouveau cercle avec une position aléatoire
    // et une couleur en paramètre
    public void nouveauCercle(int color)
    {
        int y = (int) ( Math.random() * this.hauteur);
        int x = (int) ( Math.random() * this.longueur);

            for(int i=0; i<this.alCercle.size(); i++)
                if(this.alCercle.get(i).isInside(x, y))
                {
                    this.nouveauCercle(color);
                    return ;
                }

        if(x < ViewDessin.RAYON  *2 ) x += ViewDessin.RAYON;
        if(y < ViewDessin.RAYON *2  ) y += ViewDessin.RAYON;
        if(x > this.longueur        ) x -= ViewDessin.RAYON;
        if(y > this.hauteur - 250   ) y -= 250;

        System.out.println("nbPoint " + Jeu.nbPoints);
        this.alCercle.add(new Cercle(x, y, color));
    }

    //Déplace le joueur qui correspond au premier cercle selon les données récupérer
    //et met à jour la view
    public void deplacer(float x, float y)
    {
        this.alCercle.get(0).deplacer(x, y, this.hauteur, this.longueur);
        this.isInside(this.alCercle.get(0).getX(), this.alCercle.get(0).getY());
        this.postInvalidate();
        this.invalidate();
    }

    //Méthode qui permet de savoir si les coordonnées sont dans un cercle
    //et fais certaine actions si c'est le cas
    public void isInside(float x, float y)
    {
        for(int i=1; i< this.alCercle.size(); i++)
        {
            if(this.alCercle.get(i).isInside(x, y))
            {
                //Si le cercle est un l'enlève donc de la liste et on ajoute
                //2 nouveaux cercles, 1 vert et 1 rouge et on incrémente le nombre de points
                if(this.alCercle.get(i).getColor() == Color.GREEN)
                {
                    this.alCercle.remove(i);
                    this.nouveauCercle(Color.GREEN);
                    this.nouveauCercle(Color.RED);
                    Jeu.nbPoints++;
                }
                //Si le cercle est rouge on renvoi à l'activité principale le nombre de points
                // et on ferme l'activité
                else if(this.alCercle.get(i).getColor() == Color.RED)
                {
                    Intent intent = new Intent();
                    intent.putExtra("myResult", Jeu.nbPoints);
                    this.context.setResult(1, intent);
                    this.context.finish();
                }
            }
        }
    }

    //Méthode qui permet de dessiner sur la view
    public void onDraw(Canvas canvas)
    {
        for(Cercle c : this.alCercle)
        {
            this.dessinCercle.setColor(c.getColor());
            canvas.drawCircle(c.getX(), c.getY(), ViewDessin.RAYON, this.dessinCercle);
        }
    }
}

//Classe qui gère les cercle
class Cercle
{
    float xCentre, yCentre;
    int color;

    //Constructeur du cercle qui prend 2 float et 1 couleur
    public Cercle(float x, float y, int color)
    {
        this.xCentre = x;
        this.yCentre = y;
        this.color = color;
    }

    //Getter
    public float getX()     { return this.xCentre; }
    public float getY()     { return this.yCentre; }
    public int   getColor() { return this.color;   }

    //Méthode qui permet de déplacer le cercle selon les coordonnées en paramètres
    //et selon les dimensions du téléphone
    public void deplacer(float x, float y, int hauteur, int longueur)
    {
        this.xCentre += x*2;
        this.yCentre += y*2;

        if(xCentre < ViewDessin.RAYON) xCentre += ViewDessin.RAYON;
        if(yCentre < ViewDessin.RAYON) yCentre += ViewDessin.RAYON;
        if(xCentre > longueur        ) xCentre -= ViewDessin.RAYON;
        if(yCentre > hauteur - 250   ) yCentre -= ViewDessin.RAYON;
    }

    ///Méthode qui permet de savoir si les coordonnées en paramètre sont dans ce cerle
    public boolean isInside(float x, float y)
    {
        if( x <= (xCentre + ViewDessin.RAYON) &&
            y <= (yCentre + ViewDessin.RAYON) &&
            x >= (xCentre - ViewDessin.RAYON) &&
            y >= (yCentre - ViewDessin.RAYON))
            return true;

        return false;
    }
}
