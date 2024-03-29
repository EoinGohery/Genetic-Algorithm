/*  Project Group Members:
*
*   Eoin Gohery - 17206413
*   Cian McInerney - 17232724
*   Jonathan Ryley - 17244501
*   Zeyuan Song - 19096216
*
*/

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
  static double chunk;
  static int radius =100;
  static int mov =200;

  public sa()
  {
    setTitle("AI");
    setSize(960,960);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }
  public void paint(Graphics g) {

    double w = v;

    for (int i=0; i<v; i++) {
      for (int j=0; j<v; j++) {
        if(adj[current_ordering[i]][current_ordering[j]]==1) {
          g.drawLine((int)(((double) Math.cos(i*chunk))*radius + mov),
                  (int)(((double) Math.sin(i*chunk))*radius + mov),
                  (int)(((double) Math.cos(j*chunk))*radius + mov),
                  (int)(((double) Math.sin(j*chunk))*radius + mov));
        }
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    //JFrame frame = new JFrame();
    Random r = new Random();
    int P=0; int G=0; int Cr=0; int Mu =0;
    JTextField populationField = new JTextField(3);
    JTextField generationsField = new JTextField(3);
    JTextField crossoverField = new JTextField(3);
    JTextField mutationField = new JTextField(3);

    // Read input data from file,
    // convert edge list to adjacency matrix,
    // set v to number of nodes
    try {
      adj = convertEdgeListToMatrix(fillEdgeListFromFile());
      printMatrix(adj);
      v = adj[0].length;
    } catch(IOException exception) {
      System.out.println(exception);
      //TODO: create procedure for when the file is not present (create default data?)
    }

    chunk = (2*Math.PI)/v;

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
    // Get user input
    while(!valid) {
      int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Fill in Fields", JOptionPane.OK_CANCEL_OPTION);
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
        } else if (P<1 || G<1) {
          JOptionPane.showMessageDialog(null,"Population and Generation must both be greater than 0","Error", 0);
        } else if (Mu+Cr>100) {
          JOptionPane.showMessageDialog(null, "The Sum of Crossover and Mutation must not exceed 100", "Error", 0);
        }     } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null,"Please Enter Numerical Values Only","Error", 0);
      }
    }

    current_ordering = new int[v];
    int next_Population [][] = new int [P][v];
    int current_Population [][]= new int [P][v];
    int Pr = 0;
    for (int i = 0; i < v; i++) {
      current_ordering[i]=i;
    }

    // fill the inital current_Population with randomized orderings
    ArrayList<Integer> initalList = new ArrayList<Integer>();
    for(int i=0; i<current_ordering.length; i++) {
      initalList.add(current_ordering[i]);
    }
    for (int i=0; i<P; i++) {
      Collections.shuffle(initalList);
      for (int n = 0; n < v; n++) {
        current_ordering[n] = initalList.get(n);
      }
      current_Population[i]=Arrays.copyOf(current_ordering, v);
    }
    // Setup completed:
    // Start Genetic Algorithm: ------------------------------------------------

    for (int i =0; i<G; i++) {
      current_Population=Arrays.copyOf(selectionProcess(current_Population, P), P);
      for (int j =0; j<P; j++) {
        Pr = r.nextInt(101);
        current_ordering=Arrays.copyOf(current_Population[j], v);

        if (Cr>=Pr && P!=(j+1)) {
          //Crossover
          crossoverProcess(current_Population, next_Population, j);
          j++; // we have added two elements to next population.
        } else if (Cr<=Pr && Pr<=(Cr+Mu)) {
          //Mutation
          Mutation();
          next_Population[j]=Arrays.copyOf(current_ordering, v);
        } else if ((Cr+Mu)<=Pr) {
          //Reproduction
          next_Population[j]=Arrays.copyOf(current_ordering, v);
        } else {
          i--;
        }
      }
      current_Population=Arrays.copyOf(next_Population, P);
    }
    current_Population = Arrays.copyOf(selectionProcess(current_Population, P), P);
    current_ordering= Arrays.copyOf(current_Population[0], P);
    sa visualization = new sa();
  }
  public static void Mutation() {
    int temp=0;
    Random r = new Random();
    int firstIndex = r.nextInt(v);
    int secondIndex = r.nextInt(v);

    temp = current_ordering[firstIndex];
    current_ordering[firstIndex] = current_ordering[secondIndex];
    current_ordering[secondIndex] = temp;
  }

  // This method modifies the data at current_Population[population_Index] and current_Population[population_Index + 1]
  // It then adds the two modified int[] to next_Population[population_Index] and next_Population[population_Index + 1]
  private static void crossoverProcess(int[][] current_Population, int[][] next_Population, int population_Index) {
    ArrayList<Integer> first_Ordering_Duplicates = new ArrayList<Integer>();
    ArrayList<Integer> second_Ordering_Duplicates = new ArrayList<Integer>();
    int[][] new_Current_Population = current_Population;
    int orderingLengths = current_Population[population_Index].length;
    int cuttingPoint;
    int temp;
    boolean duplicates = false;

    Random randomGenerator = new Random();
    cuttingPoint = randomGenerator.nextInt(current_Population[0].length - 3);
    cuttingPoint++; // between 1 and |N| - 2

    // apply crossover
    for(int i=cuttingPoint;i<orderingLengths;i++) {
      temp = current_Population[population_Index][i];
      current_Population[population_Index][i] = current_Population[population_Index + 1][i];
      current_Population[population_Index + 1][i] = temp;
    }

    // check for duplicate Values
    for (int i=0;i<orderingLengths;i++) {
      for (int j=0;j<orderingLengths;j++) {
        if(current_Population[population_Index][i] == current_Population[population_Index][j] && i != j) {
          duplicates = true;
          first_Ordering_Duplicates.add(current_Population[population_Index][i]);
        }
        if(current_Population[population_Index + 1][i] == current_Population[population_Index + 1][j] && i != j) {
          duplicates = true;
          second_Ordering_Duplicates.add(current_Population[population_Index + 1][i]);
        }
      }
    }

    if(duplicates) {
      // The sizes of these two should always be the same
      // If they are different then the input data is incorrect
      if(first_Ordering_Duplicates.size() != second_Ordering_Duplicates.size()) {
        System.out.println("Error in toolForCrossoverFunction(), duplicate quantities do not match");
      }
      // find position of each duplicate and replace it with a missing value
      // (one that is now a duplicate in the other set)
      for (int i=0;i<first_Ordering_Duplicates.size();i++ ) {
        for (int j=0;j<orderingLengths;j++) {
          if(first_Ordering_Duplicates.get(i) == current_Population[population_Index][j]) {
            current_Population[population_Index][j] = second_Ordering_Duplicates.get(i);
            break;
          }
        }
        for (int j=0;j<orderingLengths;j++) {
          if(second_Ordering_Duplicates.get(i) == current_Population[population_Index + 1][j]) {
            current_Population[population_Index + 1][j] = first_Ordering_Duplicates.get(i);
            break;
          }
        }
      }
    }

    // Add to next population
    next_Population[population_Index] = current_Population[population_Index];
    next_Population[population_Index + 1] = current_Population[population_Index + 1];
  }

  public static double getFitnessCost(int[] ordering) {
    double totalEdgeLength =0;
    double x1, y1, x2, y2;
    for (int i=0;i<v;i++) {
      for (int j=i+1;j<v;j++) {
        if (adj[i][j]==1) {
          x1 = (((double) Math.cos(i*chunk))*radius + mov);
          y1 = (((double) Math.sin(i*chunk))*radius + mov);
          x2 = (((double) Math.cos(j*chunk))*radius + mov);
          y2 = (((double) Math.sin(j*chunk))*radius + mov);
          totalEdgeLength+=Math.sqrt(((y2 - y1) * (y2 - y1)) + ((x2 - x1) * (x2 - x1)));
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

  private static int[][] selectionProcess(int[][] current_Population, int P) {
    int[][] sorted_Population = Arrays.copyOf(sortByFitness(current_Population, P), P);

    int third = P/3;
    for (int i=0; i<third; i++ ) {
      sorted_Population[P-1-i] = sorted_Population[third-i];
    }
    sorted_Population = Arrays.copyOf(sortByFitness(current_Population, P), P);

    return sorted_Population;
  }

  // sort by fitness (lowest first)
  private static int[][] sortByFitness(int[][] current_Population, int P) {
    double[] orderingValues = new double[P];
    for (int i=0; i<P; i++) {
      orderingValues[i]=getFitnessCost(current_Population[i]);
    }
    int[][] sortedPopulation = Arrays.copyOf(current_Population, P);
    double[] sortedValues = Arrays.copyOf(orderingValues, P);
    Arrays.sort(sortedValues);
    for (int i=0; i<P; i++)
    {
      for (int j=0; j<P; j++) {
        if (orderingValues[j]==sortedValues[i]) {
          sortedPopulation[i]=current_Population[j];
        }
      }
    }
    return sortedPopulation;
  }
}
