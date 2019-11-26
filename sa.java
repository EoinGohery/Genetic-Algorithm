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
    int P=0; int G=0; int Cr=0; int Mu =0; int cuttingPoint =3;
    JTextField populationField = new JTextField(3);
    JTextField generationsField = new JTextField(3);
    JTextField crossoverField = new JTextField(3);
    JTextField mutationField = new JTextField(3);
    ArrayList<Integer> first_Ordering_Duplicates = new ArrayList<Integer>();
    ArrayList<Integer> second_Ordering_Duplicates = new ArrayList<Integer>();
    int temp;
    boolean duplicates = false;
    int crossover_ordering [];

    // Read input data from file,
    // convert edge list to adjacency matrix,
    // set v to number of nodes
    try {
	    adj = convertEdgeListToMatrix(fillEdgeListFromFile());
	    printMatrix(adj);
	    v = adj[0].length;
      chunk = (2*Math.PI)/v;
    } catch(IOException exception) {
      System.out.println(exception);
	    //TODO: create procedure for when the file is not present (create default data?)
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
    // Get user input
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
      } catch (NumberFormatException e) {
        //TODO: make a better error output.
        System.out.println(e);
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
      current_Population[i]=current_ordering;
    }

    int firstIndex = r.nextInt(v);
    int secondIndex = r.nextInt(v);

    // Setup completed:
    // Start Genetic Algorithm: ------------------------------------------------

    for (int i =0; i<G; i++) {
      current_Population=selectionProcess(current_Population, P);
      for (int j =0; j<P; j++) {
        Pr = r.nextInt(101);
        current_ordering=current_Population[j];

        if (Cr>=Pr && P!=(j+1)) {
          //Crossover
          crossover_ordering=current_Population[j+1];
          cuttingPoint = r.nextInt(current_Population[0].length - 3);
          cuttingPoint++;
          for(int k=cuttingPoint; k<v; k++) {
            temp = current_ordering[k];
            current_ordering[k] = crossover_ordering[k];
            crossover_ordering[k] = temp;
          }
          // check for duplicate Values
          for (int k=0; k<v; k++) {
            for (int l=0; l<v; l++) {
              if(current_ordering[k] == current_ordering[l] && k != l) {
                duplicates = true;
                first_Ordering_Duplicates.add(current_ordering[k]);
              }
              if(crossover_ordering[k] == crossover_ordering[l] && k != l) {
                duplicates = true;
                second_Ordering_Duplicates.add(crossover_ordering[k]);
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
            for (int k=0; k<first_Ordering_Duplicates.size(); k++ ) {
              for (int l=0; l<v; l++) {
                if(first_Ordering_Duplicates.get(k) == current_ordering[l]) {
                  current_ordering[l] = second_Ordering_Duplicates.get(k);
                  break;
                }
              }
              for (int l=0; l<v; l++) {
                if(second_Ordering_Duplicates.get(k) == crossover_ordering[l]) {
                  crossover_ordering[l] = first_Ordering_Duplicates.get(k);
                  break;
                }
              }
            }
          }
          next_Population[j] = current_ordering;
          next_Population[j + 1] = crossover_ordering[j+1];
          j++; // we have added two elements to next population.
        } else if (Cr<=Pr && Pr<=(Cr+Mu)) {
          //Mutation
          temp = current_ordering[firstIndex];
          current_ordering[firstIndex] = current_ordering[secondIndex];
          current_ordering[secondIndex] = temp;
          next_Population[j]=current_ordering;
        } else if ((Cr+Mu)<=Pr) {
          //Reproduction
          next_Population[j]=current_ordering;
        } else {
          i--;
        }
      }
      current_Population=next_Population;
    }
    current_Population = selectionProcess(current_Population, P);
    current_ordering=current_Population[0];
    sa visualization = new sa();
  }

  public static double getFitnessCost(int[] ordering) {
    double totalEdgeLength =0;
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

  private static int[][] selectionProcess(int[][] current_Population, int P) {
    int[][] sorted_Population = sortByFitness(current_Population, P);

    int third = P/3;
    for (int i=0; i<third; i++ ) {
      sorted_Population[P-1-i] = sorted_Population[third-i];
    }

    return sorted_Population;
  }

  // sort by fitness (lowest first)
  private static int[][] sortByFitness(int[][] current_Population, int P) {
    double[] orderingValues = new double[P];
    	for (int i=0; i<P; i++) {
    		orderingValues[i]=getFitnessCost(current_Population[i]);
    	}
    	int[][] sortedPopulation = current_Population.clone();
      double[] sortedValues = orderingValues.clone();
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
