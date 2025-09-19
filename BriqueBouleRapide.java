//package cassebriques;

import java.awt.Color;

 class BriqueBouleRapide extends Brique{
   private final int RAPIDE=2;

  public BriqueBouleRapide() {
    super();
    couleur=Color.yellow;
  }

  public int choc() {
    super.choc();
    return RAPIDE;
  }

}
