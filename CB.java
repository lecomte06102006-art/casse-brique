import java.awt.event.*;
import javax.swing.*;

public class CB extends JFrame implements ActionListener{
    EspaceJeu espace;
    private JMenuBar barreDeMenus;
    private JMenu menuJeu;
    private JMenuItem jeuNouveau;
    private JMenuItem jeuQuitter;
    private JMenuItem traitSeparation;

    public CB() {
        super("Casse briques");
        setSize(370, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Création du panneau de jeu
        espace = new EspaceJeu();
        getContentPane().add(espace);

        // Création barre de menus
        barreDeMenus = new JMenuBar();
        menuJeu = new JMenu("Jeu");
        jeuNouveau = new JMenuItem("Nouveau");
        jeuQuitter = new JMenuItem("Quitter");
        traitSeparation = new JMenuItem("--------------");

        jeuNouveau.addActionListener(this);
        jeuQuitter.addActionListener(this);

        menuJeu.add(jeuNouveau);
        menuJeu.add(traitSeparation);
        menuJeu.add(jeuQuitter);
        barreDeMenus.add(menuJeu);
        setJMenuBar(barreDeMenus);
    }

    public static void main(String[] args) {
        CB frame = new CB();
        frame.setVisible(true);
        frame.espace.initialiseNiveau(); // initialisation du niveau au démarrage
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jeuNouveau) {
            espace.initialiseNiveau();
        }
        if (e.getSource() == jeuQuitter) {
            System.exit(0);
        }
    }
}
