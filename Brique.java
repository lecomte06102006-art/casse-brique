//package cassebriques;

import java.awt.*;


class Brique {
  private final int SIMPLE=0;
  private int x, y, largeur, hauteur;
  protected Color couleur;
  protected boolean detruite;

  public Brique() {
    largeur=17;
    hauteur=14;
    couleur=Color.green;
    detruite=false;
  }

  public int choc() {
    // Si la brique n'est pas déjà détruite
    if (!detruite) {
      detruite=true;
    }
    return SIMPLE;
  }

public boolean isDetruite() {
  return detruite;
}

public void positionne(int newX, int newY) {
  x=newX;
  y=newY;
}

public int getHauteur() {
  return hauteur;
}

public int getLargeur() {
  return largeur;
}

public void dessine(Graphics2D motif){
  // Dessin de la brique
  motif.setColor(couleur);
  motif.fillRect(x,y,largeur,hauteur);
}

}
