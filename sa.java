import javax.swing.*;
import java.io.FileNotFoundException;
import java.awt.Graphics;
import  java.util.Random;

public class sa extends JFrame {
  static int adj[][]; // adjacency matric
  static int v=17;//0; // number of nodes
  static int current_ordering [];
  static double current_fitness=0;
  static double min_dis=0;
  static double chunk;

  public sa()
  {
    setTitle("AI");
    setSize(960,960);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  public void paint(Graphics g) {
    int radius =100;
    int mov =200;

    double w = v;

    for (int i=0; i<v; i++) {
      for (int j=i+1; j<v; j++) {
        if(adj[current_ordering[i]][current_ordering[j]]==1) {
          g.drawLine((int)(((double) Math.cos(i*chunk))*radius + mov),
                     (int)(((double) Math.cos(i*chunk))*radius + mov),
                     (int)(((double) Math.cos(i*chunk))*radius + mov),
                     (int)(((double) Math.cos(i*chunk))*radius + mov));
        }
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    Random r = new Random();
    int P=0; int G=0; int Cr=0; int Mu =0;
    JTextField populationField = new JTextField(3);
    JTextField generationsField = new JTextField(3);
    JTextField crossoverField = new JTextField(3);
    JTextField mutationField = new JTextField(3);

    //function to convert edge list to adjacency matrix
    //set v to number of nodes
    //here

    JPanel myPanel = new JPanel();
    myPanel.add(new JLabel("Population:"));
    myPanel.add(populationField);
    myPanel.add(Box.createHorizontalStrut(15));
    myPanel.add(new JLabel("Generation:"));
    myPanel.add(generationsField);
    myPanel.add(Box.createHorizontalStrut(15));
    myPanel.add(new JLabel("Crossover Rate(0-100):"));
    myPanel.add(crossoverField);
    myPanel.add(Box.createHorizontalStrut(15));
    myPanel.add(new JLabel("Mutation Rate:(0-100)"));
    myPanel.add(mutationField);

    boolean valid = false;
    while(!valid) {
      int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Numerical Values Only", JOptionPane.OK_CANCEL_OPTION);
      try {
        if (result == JOptionPane.OK_OPTION) {
          P = Integer.parseInt(populationField.getText());
          G = Integer.parseInt(generationsField.getText());
          Cr = Integer.parseInt(crossoverField.getText());
          Mu = Integer.parseInt(mutationField.getText());
        } else if (result == JOptionPane.CANCEL_OPTION) {
          System.exit(0);
        }
        if (P>0 && G>0 && Cr>=0 && Cr<=100 && Mu>=0 && Mu<=100 && Mu+Cr<=100) {
          valid = true;
        }
      } catch (NumberFormatException e) {}
    }

    int next_Population [][] = new int [v][P];
    int current_Population [][]= new int [v][P];
    int Pr = 0;
    for (int i =0; i<G; i++) {
      //Selection Process goes here AKA sort by fitness
      for (int j =0; j<P; j++) {
        Pr = r.nextInt(101);
        if (Cr>=Pr) {
          //Crossover
        } else if (Cr<=Pr && Pr<=(Cr+Mu)) {
          //Mutation
        } else if ((Cr+Mu)<=Pr) {
          //Reproduction
        } else {
          i--;
        }
      }
    }

    sa visualization = new sa();
  }

  public double getFitnessCost(int[] ordering) {
    double totalEdgeLength =0;
    double chunk = (2*Math.PI)/v;
    double x1, y1, x2, y2;
    for (int i=0;i<v;i++) {
      x1 = Math.cos(i*chunk);
      y1 = Math.sin(i*chunk);
      for (int j=0;j<v;j++) {
        x2 = Math.cos(j*chunk);
        y2 = Math.sin(j*chunk);
        if (adj[i][j]==1) {
          totalEdgeLength=+Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        }
      }
    }
    return totalEdgeLength;
  }
}
