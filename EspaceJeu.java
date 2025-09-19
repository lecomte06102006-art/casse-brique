//package cassebriques;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class EspaceJeu extends JPanel implements Runnable, MouseListener,
                                          MouseMotionListener {

  // Delai entre 2 déplacements
  private final int DELAI=16;

  // Constantes rattachées aux phases de jeu
  private final int ATTEND=1;
  private final int ROULE=2;
  private final int SORT=3;
  private final int GAGNE=4;

  // Constantes rattachées aux types de briques
  private final int SIMPLE=0;
  private final int NORME=1;
  private final int RAPIDE=2;
  

  // Champs d'instance
  private Thread action;
  private boolean fini;
  private int phase;
  private int delai;
  private Barre barre;
  private Boule boule;
  private Mur mur;
  private int nbVie=3;
  private String vieDit;



  public EspaceJeu() {

    // Création de la barre
    barre=new Barre();
    // Création de la boule
    boule=new Boule();

    // Délai entre 2 déplacements
    delai=DELAI;

    // phase d'attente
    phase=ATTEND;

    // Gestion des évenement liés à la souris
      addMouseMotionListener(this);
      addMouseListener(this);


  }

  public void initialiseNiveau() {

    // Arrêt du thread action s'il est en cours d'exécution.
    fini=true;
    if(action != null) {
      while(action.isAlive());
    }

    // Création du mur de brique
    if (mur==null) {
      mur=new Mur();
    }
    mur.construit();
    // Premiére phase du jeu
    phase= ATTEND;
    delai = DELAI;

    // Lancement de l'exécution du jeu dans un thread
    action = new Thread(this);
    action.start();
  }
  
  public void initialiseNiveauAvecLayout(int[][] layout) {
	    fini = true;
	    if (action != null) {
	        while (action.isAlive());
	    }
	    if (mur == null) {
	        mur = new Mur();
	    }
	    mur.construit(layout);
	    phase = ATTEND;
	    delai = DELAI;
	    action = new Thread(this);
	    action.start();
	}


  // Traitement central exécuté avec une périodicité précise
  public void run() {
	this.nbVie=3;
    fini=false;
    while (!fini) {
      // Selon la phase du jeu ...
      switch (phase) {
        // Attente de lancement de la boule
        case ATTEND:
          // Placement de la boule au milieu de la barre
          boule.place(barre.getX(), barre.getY() - boule.getRayon());
          break;

          // La boule roule
        case ROULE:
          // Déplacement de la boule
          boule.deplace();
          // Rebond sur le bord gauche ?
          if (boule.getX() < boule.getRayon()) {
            boule.chocH();
            boule.place(boule.getRayon(), boule.getY());
          }
          else {
            // Rebond sur le bord droit ?
            if (boule.getX() > getSize().width - boule.getRayon()) {
              boule.chocH();
              boule.place(getSize().width - boule.getRayon(), boule.getY());
            }
          }

          // Rebond sur le haut ?
          if (boule.getY() < boule.getRayon()) {
            boule.chocV();
            boule.place(boule.getX(), boule.getRayon());
          }
          else {
            // Rebond (ou non) sur la barre ?
            if (boule.getY() > 310 - boule.getRayon()) {
              if ((boule.getX() - boule.getRayon() < barre.getX() + barre.getMiLargeur())
              &&
                (boule.getX() + boule.getRayon() > barre.getX() - barre.getMiLargeur())) {
                // Rebond sur la barre
                // Le rebond dépend de la zone de la barre touchée
                rebondSurBarre(boule.getX() - barre.getX());

                boule.place(boule.getX(), 310-boule.getRayon());
              }
              else {
                // Si la boule touche le fond ...
                if (boule.getY() > 310 + barre.getHauteur() - boule.getRayon()) {
                  // Loupé !!
                	nbVie-=1;                	
                	if(nbVie>0) {
                		boule.place(barre.getX(), barre.getY() - boule.getRayon());
                	}
                	else {
                        phase = SORT;        
                	}
				}
              }

            }
          }

          // Gestion du choc avec une brique
          // Récupération de la hauteur d'une brique
          int hauteur = mur.getHauteurBrique();
          // Récupération de la largeur d'une brique
          int largeur = mur.getLargeurBrique();
          // Si la boule se trouve dans la zone du mur de briques ...
          if (boule.getY()-boule.getRayon()<10*(hauteur+1)){
            // l1, c1 sont les coordonnées du coin supérieur gauche de la boule
            // l2, c2 sont les coordonnées du coin inférieur droit de la boule
            int l1, l2, c1, c2;
            l1=(int)((boule.getY()-boule.getRayon())/(hauteur+1));
            l2=(int)((boule.getY()+boule.getRayon())/(hauteur+1));
            c1=(int)((boule.getX()-boule.getRayon())/(largeur+1));
            c2=(int)((boule.getX()+boule.getRayon())/(largeur+1));

            // Le rebond dépend des coins (1 ou 2) en contact avec une brique
            // Coin supérieur gauche ...
            if (mur.percute(l1,c1)) {
              // et coin supérieur droit
              if (mur.percute(l1,c2)) {
                // Choc vertical
                boule.chocV();
              }
              else {
                // et coin inférieur gauche
                if (mur.percute(l2,c1)) {
                  // Choc horizontal
                  boule.chocH();
                }
                else {
                  // Double choc
                  boule.chocV();
                  boule.chocH();
                }
              }
            }
            else {
              // Coin supérieur droit ...
              if (mur.percute(l1,c2)) {
                // et coin inférieur droit
                if (mur.percute(l2,c2)) {
                  // Choc horizontal
                  boule.chocH();
                }
                else {
                  // Double choc
                  boule.chocV();
                  boule.chocH();
                }
              }
              else {
                // Coin inférieur gauche ...
                if (mur.percute(l2,c1)) {
                  // et coin inférieur droit
                  if (mur.percute(l2,c2)) {
                    // Choc vertical
                    boule.chocV();
                  }
                  else {
                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }
                else {
                  // Coin inférieur droit
                  if (mur.percute(l2,c2)) {
                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }
              }
            }
            // Casse effective des brique du mur
            //(et mise en place des conséquences)
            modifJeu(mur.casse(l1,c1));
            modifJeu(mur.casse(l1,c2));
            modifJeu(mur.casse(l2,c1));
            modifJeu(mur.casse(l2,c2));

            // Si toutes les briques sont cassées ...
            if (mur.getNbBriques()==0) {
              // Le joueur à gagné
              phase=GAGNE;
            }
          }
          break;

        case SORT :
        	JOptionPane.showMessageDialog(this,"C'est perdu !","Casse briques",
        			JOptionPane.INFORMATION_MESSAGE);
	        fini=true;
	        break;

        case GAGNE :
          JOptionPane.showMessageDialog(this,"Bravo, vous avez gagné !",
                         "Casse briques",JOptionPane.INFORMATION_MESSAGE);
          fini=true;
          break;
      }

      // on redessine l'espace de jeu
      repaint();

      try {
        Thread.sleep(delai);
      } catch (InterruptedException e) {}
    }
  }

  void rebondSurBarre(int impact) {
    // Rebond sur la barre
    boule.chocV();
    // La barre est divisée en 5 parties. Chaque partie provoque un rebond différent
    // Partie extréme gauche : Augmentation de l'angle de 30 degrés
    if (impact<-(barre.getMiLargeur()*0.6))
      boule.modifAngle(30);
    else
      // Partie suivante : Augmentation de l'angle de 15 degrés
      if (impact<-(barre.getMiLargeur()*0.2))
        boule.modifAngle(15);

    // Partie extréme droite : Diminution de l'angle de 30 degrés
    if (impact>(barre.getMiLargeur()*0.6))
      boule.modifAngle(-30);
    else
      // Partie précédante : Diminution de l'angle de 15 degrés
      if (impact>(barre.getMiLargeur()*0.2))
        boule.modifAngle(-15);

    // La partie centrale de la barre provoque un rebond normal

    }

    public void modifJeu(int action) {
      switch (action) {
        case NORME :
          // Retour aux valeurs de base
          delai=DELAI;
          break;

        case RAPIDE :
          // Accélération du traitement
          delai=(int)(DELAI/2);
          break;
      }
    }

    void lanceBoule(int angle) {
          if (phase==ATTEND) {
            phase=ROULE;
            boule.angleDep(angle);
          }
    }

  public void paintComponent(Graphics comp) {
    Graphics2D comp2D = (Graphics2D)comp;
    // Effacement de l'espace de jeu
    comp2D.setColor(getBackground());
    comp2D.fillRect(0,0,getSize().width,getSize().height);
    // Dessin de la barre
    barre.dessine(comp2D);
    // Dessin de la boule
    boule.dessine(comp2D);
    // Dessin du mur de brique
    // Au tout départ le mur n'existe pas
    if(mur!=null)
      mur.dessine(comp2D);
    //affichage de la vie 
    comp2D.setColor(Color.BLACK);
    comp2D.setFont(new Font("Arial", Font.BOLD, 16));
    comp2D.drawString("Vies : " + nbVie, 10, 350);
	
  }

  // Méthodes de l'interface MouseMotionListener
  public void mouseMoved(MouseEvent evt) {
    // Si le pointeur est trop à gauche ...
    if (evt.getX()<barre.getMiLargeur())
      // barre contre le bord gauche
      barre.setX(barre.getMiLargeur());
    else
      // Si de pointeur est trop à droite ...
      if (evt.getX()>getSize().width-barre.getMiLargeur())
        // barre contre le bord droit
        barre.setX(getSize().width-barre.getMiLargeur());
      else
        // barre centrée sur le pointeur
        barre.setX(evt.getX());
  }
  public void mouseDragged(MouseEvent evt) {}

  // Méthodes de l'interface MouseListener
  public void mouseClicked(MouseEvent evt) {
    lanceBoule((int)(Math.random()*120)+30);
  }

  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {}
  public void mousePressed(MouseEvent evt) {}
  public void mouseReleased(MouseEvent evt) {}
}
