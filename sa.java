import javax.swing.*;
import java.util.*;
import java.awt.Graphics;
import java.io.*;

import java.awt.List;

public class sa extends JFrame {
  static int adj[][]; // adjacency matric
  static int v=0; // number of nodes
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
    JFrame frame = new JFrame();
    Random r = new Random();
    int P=0; int G=0; int Cr=0; int Mu =0;
    JTextField populationField = new JTextField(3);
    JTextField generationsField = new JTextField(3);
    JTextField crossoverField = new JTextField(3);
    JTextField mutationField = new JTextField(3);

    //function to convert edge list to adjacency matrix && set v to number of nodes
    try {
	    adj = convertEdgeListToMatrix(fillEdgeListFromFile());
	    printMatrix(adj);
	    v = adj[0].length;
    } catch(IOException exception) {
      System.out.println(exception);
    }

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
      int result = JOptionPane.showConfirmDialog(frame, myPanel, "Please Enter Numerical Values Only", JOptionPane.OK_CANCEL_OPTION);
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

    current_ordering = new int[v];
    int next_Population [][] = new int [P][v];
    int current_Population [][]= new int [P][v];
    int Pr = 0;
    for (int i = 0; i < v; i++) {
      current_ordering[i]=i;
    }
    //list created to shuffle for the intitail random orderings
    ArrayList<Integer> list = new ArrayList<Integer>();
    for(int i=0; i<current_ordering.length; i++) {
      list.add(current_ordering[i]);
    }
    for (int i=0; i<P; i++) {
		  Collections.shuffle(list);
      for (int n = 0; n < v; n++) {
        current_ordering[n] = list.get(n);
      }
      current_Population[i]=current_ordering;
    }
    int crossover_ordering [] = new int[v];
    for (int i =0; i<G; i++) {






      //Selection Process goes here AKA sort by fitness (lowest first)
      for (int j =0; j<P; j++) {
        Pr = r.nextInt(101);
        current_ordering=current_Population[i];
        if (Cr>=Pr) {
          //Crossover
          crossover_ordering = current_Population[i+1];



          i++;
        } else if (Cr<=Pr && Pr<=(Cr+Mu)) {
          //Mutation

        } else if ((Cr+Mu)<=Pr) {
          //Reproduction
          next_Population[i]=current_ordering;
        }
      }
      current_Population=next_Population;
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

  private static ArrayList<ArrayList<Integer>> fillEdgeListFromFile() throws IOException {
    File reader = new File("input.txt");      // <- insert correct path here instead of default input text
    ArrayList<ArrayList<Integer>> edgeList = new ArrayList<ArrayList<Integer>>();

    if(!reader.exists()) {
      System.out.println("Input file not found");
    }
    else {
      Scanner in = new Scanner(reader);
      String[] aLineFromFile;

      // Initalise edgeList:
      edgeList.add(new ArrayList<Integer>());
      edgeList.add(new ArrayList<Integer>());

      // Fill edgeList with data from file
      while(in.hasNext()) {
        aLineFromFile = in.nextLine().split(" ");
        edgeList.get(0).add(Integer.parseInt(aLineFromFile[0]));
        edgeList.get(1).add(Integer.parseInt(aLineFromFile[1]));
      }
    }
    return edgeList;
  }

  private static int findLargestEdgeListElement(ArrayList<ArrayList<Integer>> edgeList) {
    int largestCurrentValue = 0;

    for(int i=0;i<edgeList.get(0).size();i++) {
      if(largestCurrentValue < edgeList.get(0).get(i)) {
        largestCurrentValue = edgeList.get(0).get(i);
      }
      if(largestCurrentValue < edgeList.get(1).get(i)) {
        largestCurrentValue = edgeList.get(1).get(i);
      }
    }

    return largestCurrentValue;
  }

  private static int[][] convertEdgeListToMatrix(ArrayList<ArrayList<Integer>> edgeList) {
    int[][] matrix;
    // edgeList values are index numbers therefore +1 for dimentions
    int dimentions = findLargestEdgeListElement(edgeList) + 1;

    matrix = new int[dimentions][dimentions];

    // Initalize matrix
    for(int i=0;i<dimentions;i++) {
      for(int j=0;j<dimentions;j++) {
        matrix[i][j] = 0;
      }
    }

    // Fill matrix
    for(int i=0;i<edgeList.get(0).size();i++) {
      matrix[edgeList.get(0).get(i)][edgeList.get(1).get(i)] = 1;
      matrix[edgeList.get(1).get(i)][edgeList.get(0).get(i)] = 1;
    }

    return matrix;
  }

  private static void printMatrix(int[][] matrix) {
    for(int i=0;i<matrix[0].length;i++) {
      for(int j=0;j<matrix[0].length;j++) {
          System.out.print(matrix[i][j] + " ");
      }
      System.out.println("");
    }
  }

  private static void swap(int mutationA, int mutationB, int[]mutator) {
	  int swap = mutator[mutationA];
  	mutator[mutationA] = mutator[mutationB];
  	mutator[mutationB] = swap;
  }
}
