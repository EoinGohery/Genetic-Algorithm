import java.io.*;
import  java.util.*;

public class inputTest {

  public static void main (String [] args) {
    try{
      fillEdgeListFromFile();
    }
    catch(IOException exception) {
      System.out.println(exception);
    }
  }

  private static ArrayList<ArrayList<Integer>> fillEdgeListFromFile() throws IOException {
    File reader = new File("input.txt"); // <- insert correct path here instead of default input text
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
        // System.out.println(aLineFromFile[1]);

        edgeList.get(0).add(Integer.parseInt(aLineFromFile[0]));
        edgeList.get(1).add(Integer.parseInt(aLineFromFile[1]));
      }

        System.out.println(edgeList.get(0));
        System.out.println(edgeList.get(1));
    }

    return edgeList;
  }

  private static int findLargestEdgeListElement(ArrayList<ArrayList<Integer>> edgeList) {
    // TODO: find largest element
    return 0;
  }

  private static int[][] convertEdgeListToMatrix() {
    int[][] matrix = null; // Temp value for compile.
    // TODO: findLargestEdgeListElement() for matrix size, then fill matrix
    return matrix;
  }
}
