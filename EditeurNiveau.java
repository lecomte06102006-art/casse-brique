// package cassebriques;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class EditeurNiveau extends JPanel implements MouseListener {
    private int[][] layout;
    private final int LIGNES = 10, COLS = 20;
    private final int TAILLE = 20;

    public EditeurNiveau() {
        layout = new int[LIGNES][COLS];
        addMouseListener(this);
        setPreferredSize(new Dimension(COLS * TAILLE, LIGNES * TAILLE + 40));
    }

    public int[][] GetLayout() {
        return layout;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin de la grille
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLS; j++) {
                int val = layout[i][j];
                switch (val) {
                    case 1: g.setColor(Color.green); break;
                    case 2: g.setColor(Color.yellow); break;
                    case 3: g.setColor(Color.pink); break;
                    case 4: g.setColor(Color.darkGray);break;
                    case 5: g.setColor(Color.blue);break;
                    case 6: g.setColor(Color.red);break;
                    default: g.setColor(Color.white); break;
                }
                g.fillRect(j * TAILLE, i * TAILLE, TAILLE, TAILLE);
                g.setColor(Color.black);
                g.drawRect(j * TAILLE, i * TAILLE, TAILLE, TAILLE);
            }
        }

        // légende
        g.setColor(Color.black);
        g.drawString("Cliquez sur une case pour changer son type (0→1→2→3→4→5→6→0)", 10, LIGNES * TAILLE + 20);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = e.getX() / TAILLE;
        int lig = e.getY() / TAILLE;
        if (lig < LIGNES && col < COLS) {
            layout[lig][col] = (layout[lig][col] + 1) % 7;
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    // Sauvegarde simple en CSV
    public void sauvegarder(File f) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
            for (int i = 0; i < LIGNES; i++) {
                for (int j = 0; j < COLS; j++) {
                    w.write(String.valueOf(layout[i][j]));
                    if (j < COLS - 1) w.write(",");
                }
                w.newLine();
            }
        }
    }

    public void charger(File f) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            for (int i = 0; i < LIGNES; i++) {
                String line = r.readLine();
                if (line == null) break;
                String[] parts = line.split(",");
                for (int j = 0; j < Math.min(parts.length, COLS); j++) {
                    layout[i][j] = Integer.parseInt(parts[j].trim());
                }
            }
        }
        repaint();
    }
}
