//package cassebriques;

import java.awt.Color;

class BriqueRetourNorme extends Brique {
  private final int NORME=1;

    public BriqueRetourNorme() {
      super();
      couleur=Color.pink;
    }

    public int choc() {
      super.choc();
      return NORME;
    }

  }
