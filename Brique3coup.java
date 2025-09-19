//package cassebriques;

import java.awt.Color;

public class Brique3coup extends Brique{
	private final int RESIST=3;

    public Brique3coup() {
      super();
      couleur=Color.darkGray;
      coupRestant = 3;
    }

    public int choc() {
    	coupRestant-=1;
    	super.choc();
    	
    	return RESIST;
    }

}
