//package cassebriques;

import javax.swing.*;
import java.awt.event.*;

public class CB extends JFrame implements ActionListener{
  // Espace de jeu
  private EspaceJeu espace;

  // Barre de menu
  private JMenuBar barreDeMenus;
  private JMenu menuJeu;
  private JMenuItem jeuNouveau;
  private JMenuItem jeuQuitter;
  private JMenuItem traitSeparation;

  public CB() {
      // Paramétrage du cadre
      super("Casse briques");
      setSize(370,400);

      // Gestion de la fermeture du cadre
      ExitWindow exit= new ExitWindow();
      addWindowListener(exit);

      // Création du panneau (l'espace de jeu)
      espace= new EspaceJeu();
      getContentPane().add(espace);

      // Création de la barre de menus, du menu et des options
      barreDeMenus=new JMenuBar();
      menuJeu=new JMenu();
      menuJeu.setText("Jeu");
      jeuNouveau=new JMenuItem();
      jeuNouveau.setText("Nouveau");
      jeuNouveau.addActionListener(this);
      jeuQuitter=new JMenuItem();
      jeuQuitter.setText("Quitter");
      jeuQuitter.addActionListener(this);
      traitSeparation=new JMenuItem();
      traitSeparation.setText("--------------");
      menuJeu.add(jeuNouveau);
      menuJeu.add(traitSeparation);
      menuJeu.add(jeuQuitter);
      barreDeMenus.add(menuJeu);
      this.setJMenuBar(barreDeMenus);
  }

  public static void main(String[] args) {
    CB frame = new CB();
    frame.setVisible(true);
  }

  //Opération Fichier-->Quitter ou Fichier-->Nouveau
  public void actionPerformed(ActionEvent e) {
    if(e.getSource()==jeuNouveau) {
      espace.initialiseNiveau();
    }
    if(e.getSource()==jeuQuitter) {
      System.exit(0);
    }


  }

}
