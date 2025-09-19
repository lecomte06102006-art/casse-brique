//package cassebriques;

import java.awt.*;

class Barre {
  // x es la coordonnée horizontale du centre de la barre
  // et y du haut de la barre
  private int x, y, miLargeur, hauteur;
  private Color couleur;

  public Barre() {
    x=175;
    y=310;
    miLargeur=25;
    hauteur=9;
    couleur = Color.blue;
  }

  public int getX() {
      return x;
  }

  public void setX(int newVal) {
       x=newVal;
  }

  public int getY() {
      return y;
  }

  public int getMiLargeur() {
      return miLargeur;
  }

  public void setMiLargeur(int newVal) {
       miLargeur=newVal;
  }

  public int getHauteur() {
      return hauteur;
  }

  public Color getCouleur() {
      return couleur;
  }

  public void dessine(Graphics2D motif) {
    motif.setColor(couleur);
    motif.fillRect(x-miLargeur,y,miLargeur*2,hauteur);
  }
 }
