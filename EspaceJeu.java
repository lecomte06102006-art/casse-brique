import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class EspaceJeu extends JPanel implements Runnable, MouseListener, MouseMotionListener {

    // Delai entre 2 déplacements
    private final int DELAI = 16;
    private final int largeur = 25;

    // Constantes rattachées aux phases de jeu
    private final int ATTEND = 1;
    private final int ROULE = 2;
    private final int SORT = 3;
    private final int GAGNE = 4;

    // Constantes rattachées aux types de briques
    private final int SIMPLE = 0; // brique verte
    private final int NORME = 1;  // brique rose
    private final int RAPIDE = 2; //brique jaune
    private final int RESIST = 3; //brique noire
    private final int DOUBLE = 4; //brique rouge
    private final int BARRE = 5; //brique bleue

    // Champs d'instance
    private Thread action;
    private boolean fini;
    private int phase;
    private int delai;
    private Barre barre;
    private ArrayList<Boule> boules;
    private Mur mur;
    private int nbVie = 3;

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

    // Initialise un niveau classique
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

    // Initialise un niveau depuis un layout
    public void initialiseNiveauAvecLayout(int[][] layout) {
        fini = true;
        if(action != null) while(action.isAlive());

        if(mur == null) mur = new Mur();
        mur.construit(layout);

        barre.setMiLargeur(largeur);
        boules.clear();
        boules.add(new Boule());
        pouvoirs.clear();

        phase = ATTEND;
        delai = DELAI;

        action = new Thread(this);
        action.start();
    }

    // Boucle principale du jeu
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

                        // Collisions avec les bords
                        if(b.getX() < b.getRayon()) { b.chocH(); b.place(b.getRayon(), b.getY()); }
                        if(b.getX() > getWidth() - b.getRayon()) { b.chocH(); b.place(getWidth() - b.getRayon(), b.getY()); }
                        if(b.getY() < b.getRayon()) { b.chocV(); b.place(b.getX(), b.getRayon()); }

                        // Collision avec la barre
                        if(b.getY() + b.getRayon() >= barre.getY() &&
                           b.getX() + b.getRayon() >= barre.getX() - barre.getMiLargeur() &&
                           b.getX() - b.getRayon() <= barre.getX() + barre.getMiLargeur()) {
                            rebondSurBarre(b.getX() - barre.getX(), b);
                            b.place(b.getX(), barre.getY() - b.getRayon());
                        }

                        // Collision avec les briques
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

                                    // Si c'est la brique rose, on annule tous les effets
                                    if(consequence == NORME) {
                                        delai = DELAI;
                                        barre.resetLargeur();
                                        barreReduite = false;
                                        effetActif = 0;
                                    } else if(consequence != SIMPLE && consequence != RESIST) {
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

                    // Gestion des boules perdues
                    boules.removeIf(bb -> bb.getY() > barre.getY() + 20);
                    if(boules.isEmpty()) {
                        nbVie--;
                        if(nbVie > 0) {
                            boules.clear();
                            boules.add(new Boule());
                            phase = ATTEND;
                        } else {
                            phase = SORT;
                        }
                    }

                    // Gestion des pouvoirs
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

                    // Vérification victoire
                    if(mur.getNbBriques() == 0) phase = GAGNE;

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

            // Gestion de la durée des effets
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

    // Rebond sur la barre
    void rebondSurBarre(int impact, Boule b) {
        b.chocV();
        if(impact < -(barre.getMiLargeur()*0.6)) b.modifAngle(30);
        else if(impact < -(barre.getMiLargeur()*0.2)) b.modifAngle(15);
        else if(impact > (barre.getMiLargeur()*0.6)) b.modifAngle(-30);
        else if(impact > (barre.getMiLargeur()*0.2)) b.modifAngle(-15);
    }

    // Modifications liées aux pouvoirs
    public void modifJeu(int action) {
        switch(action) {
            case NORME:
                // Retour aux valeurs de base
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

    // Lancement de la boule
    void lanceBoule(int angle) {
        if(phase == ATTEND) {
            phase = ROULE;
            for(Boule b : boules) b.angleDep(angle);
        }
    }

    // Dessin de l'espace de jeu
    public void paintComponent(Graphics comp) {
        Graphics2D g = (Graphics2D) comp;
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());

        barre.dessine(g);
        for(Boule b : boules) b.dessine(g);
        for(Pouvoir p : pouvoirs) p.dessine(g);

        if(mur != null) mur.dessine(g);

        // Affichage des vies
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Vies : " + nbVie, 10, 350);
    }

    // Gestion souris
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

// Classe Pouvoir inchangée
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
            case 1: g.setColor(Color.PINK); break;
            case 2: g.setColor(Color.YELLOW); break;
            case 5: g.setColor(Color.BLUE); break;
            case 4: g.setColor(Color.RED); break;
            
        }
        g.fillOval(x-5, y-5, 10, 10);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
}
