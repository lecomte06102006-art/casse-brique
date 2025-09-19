//package cassebriques;

import javax.swing.*;

import java.awt.BorderLayout;
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
  private JMenu menuEditeur;
  private JMenuItem editeurOuvrir;

  public CB() {
      // Paramétrage du cadre
      super("Casse briques");
      setSize(370,425);

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
      
      //construction niveau
      menuEditeur = new JMenu();
      menuEditeur.setText("Éditeur");
      editeurOuvrir = new JMenuItem("Créer un niveau");
      editeurOuvrir.addActionListener(this);
      menuEditeur.add(editeurOuvrir);
      barreDeMenus.add(menuEditeur);

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
    if (e.getSource() == editeurOuvrir) {
        JFrame fen = new JFrame("Éditeur de niveau");
        EditeurNiveau editeur = new EditeurNiveau();

        JButton tester = new JButton("Tester");
        JButton sauvegarder = new JButton("Sauvegarder");
        JButton charger = new JButton("Charger");

        JPanel boutons = new JPanel();
        boutons.add(tester);
        boutons.add(sauvegarder);
        boutons.add(charger);

        fen.getContentPane().add(editeur, BorderLayout.CENTER);
        fen.getContentPane().add(boutons, BorderLayout.SOUTH);
        fen.pack();
        fen.setVisible(true);

        // actions boutons
        tester.addActionListener(ev -> {
            espace.initialiseNiveauAvecLayout(editeur.GetLayout());
            fen.dispose(); // fermer l'éditeur
        });

        sauvegarder.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(fen) == JFileChooser.APPROVE_OPTION) {
                try {
                    editeur.sauvegarder(fc.getSelectedFile());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        charger.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(fen) == JFileChooser.APPROVE_OPTION) {
                try {
                    editeur.charger(fc.getSelectedFile());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    if(e.getSource()==jeuQuitter) {
      System.exit(0);
    }


  }

}
