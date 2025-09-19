import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class EspaceJeu extends JPanel implements Runnable, MouseListener, MouseMotionListener {

    private final int DELAI = 16;
    private final int largeur = 25;
    private final int ATTEND=1, ROULE=2, SORT=3, GAGNE=4;
    private final int SIMPLE=0, NORME=1, RAPIDE=2, BARRE=3, DOUBLE=4;

    private Thread action;
    private boolean fini;
    private int phase;
    private int delai;
    private Barre barre;
    private Mur mur;
    private ArrayList<Boule> boules;

    private final int DUREE_EFFET = 10000;
    private long debutEffet = 0;
    private int effetActif = 0;
    private boolean barreReduite = false;

    private ArrayList<Pouvoir> pouvoirs = new ArrayList<>();

    public EspaceJeu() {
        barre = new Barre();
        boules = new ArrayList<>();
        boules.add(new Boule());
        delai = DELAI;
        phase = ATTEND;

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void initialiseNiveau() {
        fini = true;
        if(action != null) while(action.isAlive());

        if(mur == null) mur = new Mur();
        mur.construit();

        barre.setMiLargeur(largeur);

        boules.clear();
        boules.add(new Boule());
        pouvoirs.clear();

        phase = ATTEND;
        delai = DELAI;

        action = new Thread(this);
        action.start();
    }

    public void run() {
        fini = false;
        while(!fini) {
            switch(phase) {
                case ATTEND:
                    for(Boule b : boules) {
                        b.place(barre.getX(), barre.getY() - b.getRayon());
                    }
                    break;

                case ROULE:
                    ArrayList<Boule> nouvelles = new ArrayList<>();
                    for(Boule b : boules) {
                        b.deplace();

                        if(b.getX() < b.getRayon()) { b.chocH(); b.place(b.getRayon(), b.getY()); }
                        if(b.getX() > getWidth() - b.getRayon()) { b.chocH(); b.place(getWidth() - b.getRayon(), b.getY()); }
                        if(b.getY() < b.getRayon()) { b.chocV(); b.place(b.getX(), b.getRayon()); }

                        if(b.getY() + b.getRayon() >= barre.getY() &&
                           b.getX() + b.getRayon() >= barre.getX() - barre.getMiLargeur() &&
                           b.getX() - b.getRayon() <= barre.getX() + barre.getMiLargeur()) {
                            rebondSurBarre(b.getX() - barre.getX(), b);
                            b.place(b.getX(), barre.getY() - b.getRayon());
                        }

                        int hauteurB = mur.getHauteurBrique();
                        int largeurB = mur.getLargeurBrique();
                        int l1 = (b.getY() - b.getRayon()) / (hauteurB+1);
                        int l2 = (b.getY() + b.getRayon()) / (hauteurB+1);
                        int c1 = (b.getX() - b.getRayon()) / (largeurB+1);
                        int c2 = (b.getX() + b.getRayon()) / (largeurB+1);

                        for(int l=l1; l<=l2; l++) {
                            for(int c=c1; c<=c2; c++) {
                                if(mur.percute(l,c)) {
                                    int consequence = mur.casse(l,c);

                                    if(consequence != SIMPLE && consequence != NORME) {
                                        int xBrique = c * (largeurB+1) + largeurB/2;
                                        int yBrique = l * (hauteurB+1) + hauteurB/2;
                                        pouvoirs.add(new Pouvoir(consequence, xBrique, yBrique));
                                    }

                                    b.chocV();
                                    b.chocH();
                                }
                            }
                        }
                    }

                    boules.addAll(nouvelles);

                    boules.removeIf(bb -> bb.getY() > barre.getY() + 20);
                    if(boules.isEmpty()) phase = SORT;
                    if(mur.getNbBriques() == 0) phase = GAGNE;

                    ArrayList<Pouvoir> attrapes = new ArrayList<>();
                    for(Pouvoir p : pouvoirs) {
                        p.descend();

                        if(p.getY() >= barre.getY() - 10 &&
                           p.getX() >= barre.getX() - barre.getMiLargeur() &&
                           p.getX() <= barre.getX() + barre.getMiLargeur()) {
                            modifJeu(p.getType());
                            attrapes.add(p);
                        }

                        if(p.getY() > getHeight()) attrapes.add(p);
                    }
                    pouvoirs.removeAll(attrapes);

                    break;

                case SORT:
                    JOptionPane.showMessageDialog(this,"C'est perdu !","Casse briques",JOptionPane.INFORMATION_MESSAGE);
                    fini = true;
                    break;

                case GAGNE:
                    JOptionPane.showMessageDialog(this,"Bravo, vous avez gagné !","Casse briques",JOptionPane.INFORMATION_MESSAGE);
                    fini = true;
                    break;
            }

            if(effetActif != 0) {
                long maintenant = System.currentTimeMillis();
                if(maintenant - debutEffet >= DUREE_EFFET) {
                    modifJeu(NORME);
                    effetActif = 0;
                }
            }

            repaint();
            try { Thread.sleep(delai); } catch (InterruptedException e) {}
        }
    }

    void rebondSurBarre(int impact, Boule b) {
        b.chocV();
        if (impact < -(barre.getMiLargeur()*0.6)) b.modifAngle(30);
        else if (impact < -(barre.getMiLargeur()*0.2)) b.modifAngle(15);
        else if (impact > (barre.getMiLargeur()*0.6)) b.modifAngle(-30);
        else if (impact > (barre.getMiLargeur()*0.2)) b.modifAngle(-15);
    }

    public void modifJeu(int action) {
        switch(action) {
            case NORME:
                delai = DELAI;
                barre.resetLargeur();
                barreReduite = false;
                effetActif = 0;
                break;
            case RAPIDE:
                delai = DELAI/2;
                debutEffet = System.currentTimeMillis();
                effetActif = RAPIDE;
                break;
            case BARRE:
                if(!barreReduite) {
                    barre.reduireLargeur();
                    barreReduite = true;
                }
                debutEffet = System.currentTimeMillis();
                effetActif = BARRE;
                break;
            case DOUBLE:
                Boule nb = new Boule();
                nb.place(barre.getX(), barre.getY()-30);
                nb.angleDep((int)(Math.random()*120)+30);
                boules.add(nb);
                break;
        }
    }

    void lanceBoule(int angle) {
        if(phase == ATTEND) {
            phase = ROULE;
            for(Boule b : boules) b.angleDep(angle);
        }
    }

    public void paintComponent(Graphics comp) {
        Graphics2D g = (Graphics2D) comp;
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());

        barre.dessine(g);
        for(Boule b : boules) b.dessine(g);
        for(Pouvoir p : pouvoirs) p.dessine(g);

        if(mur != null) mur.dessine(g);
    }

    public void mouseMoved(MouseEvent evt) {
        if(evt.getX() < barre.getMiLargeur()) barre.setX(barre.getMiLargeur());
        else if(evt.getX() > getWidth() - barre.getMiLargeur()) barre.setX(getWidth() - barre.getMiLargeur());
        else barre.setX(evt.getX());
    }
    public void mouseDragged(MouseEvent evt) {}
    public void mouseClicked(MouseEvent evt) { lanceBoule((int)(Math.random()*120)+30); }
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}
    public void mousePressed(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) {}
}

class Pouvoir {
    private int type;
    private int x, y;
    private final int vitesse = 1;

    public Pouvoir(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void descend() { y += vitesse; }

    public void dessine(Graphics2D g) {
        switch(type) {
            case 2: g.setColor(Color.YELLOW); break;
            case 3: g.setColor(Color.BLUE); break;
            case 4: g.setColor(Color.RED); break;
        }
        g.fillOval(x-5, y-5, 10, 10);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
}
