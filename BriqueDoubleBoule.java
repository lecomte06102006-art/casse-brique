import java.awt.Color;

class BriqueDoubleBoule extends Brique {

    public BriqueDoubleBoule() {
        super();
        couleur = Color.red;
    }

    public int choc() {
        super.choc();
        return 4;
    }
}
