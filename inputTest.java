import java.io.*;
import  java.util.*;

public class inputTest {

  public static void main (String [] args) {
    try{
      printMatrix(convertEdgeListToMatrix(fillEdgeListFromFile()));
    }
    catch(IOException exception) {
      System.out.println(exception);
    }

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

    // Initalise matrix
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

}
