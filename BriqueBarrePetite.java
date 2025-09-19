//package cassebriques;

import java.awt.Color;

class BriqueBarrePetite extends Brique {
  private final int BARRE=3;

    public BriqueBarrePetite() {
      super();
      couleur=Color.blue;
    }

    public int choc() {
      super.choc();
      return BARRE;
    }

  }
