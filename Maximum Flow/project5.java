import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class project5 {
    static int vertices;
    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        Scanner scan = new Scanner(inputFile);
        int citiesNum = scan.nextInt();
        vertices = 8 + citiesNum;
        scan.skip("\n");
        int[] troops = new int[6];
        int[] troopsSent = new int[6];
        for (int i = 0; i < 6; i++)
            troops[i] = scan.nextInt();
        //scan.skip("\n");
        //System.out.println(scan.next());
        int[][] graph = new int[1 + 6 + citiesNum + 1][1 + 6 + citiesNum + 1]; // store the graph as an adjacency matrix
        // there is an extra source node before the regions, indexed as 0
        // there are imaginary roads to the regions with capacities equal to the troops at those regions
        // regions are stored from 1 to 7
        // cities are stored according to their numbers starting from 7 to the end
        // KL is the last indexed vertex
        // we calculate the maximum flow from the source index to the KL
        for (int i = 0; i < troops.length; i++) { // imaginary roads from source to regions
            graph[0][i + 1] = troops[i];
        }
        for (int i = 1; i < 7; i++) { // scan the lines with the regions
            String[] line = scan.nextLine().split(" ");
            //graph[i] = new int[(line.length - 1) / 2];
            if (line.length == 0 || line[0].equals("")) { // avoid misreading
                i--;
                continue;
            }
            for (int j = 1; j < line.length; j += 2) {
                if (line[j].equals("KL")) {
                    graph[i][7 + citiesNum] = Integer.parseInt(line[j + 1]);
                }
                else {
                    String city = line[j];
                    city = city.substring(1);
                    graph[i][7 + Integer.parseInt(city)] = Integer.parseInt(line[j + 1]);
                }
            }
        }
        for (int i = 0; i < citiesNum; i++) { // scan the lines with the cities
            String[] line = scan.nextLine().split(" ");
            //graph[i + citiesNum] = new int[(line.length - 1) / 2];
            for (int j = 1; j < line.length; j += 2) {
                if (line[j].equals("KL")) {
                    graph[i+ 7][7 + citiesNum] = Integer.parseInt(line[j + 1]);
                }
                else {
                    graph[i + 7][7 + line[j].charAt(1) - '0'] = Integer.parseInt(line[j + 1]);
                }
            }
        }
        int[][] graphCopy = new int[1 + 6 + citiesNum + 1][1 + 6 + citiesNum + 1]; // a copy of the original graph
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++)
                graphCopy[i][j] = graph[i][j];
        }
        FileWriter fw = new FileWriter(args[1]);
        int maxFlow = 0;
//        for (int i = 0; i < 6; i++) {
//            maxFlow += Math.min(fordFulkerson(i, 6 + citiesNum, graph,citiesNum), troops[i]);
//        }
        maxFlow = fordFulkerson(0, 7 + citiesNum, graph, citiesNum, troopsSent);
        fw.write(String.valueOf(maxFlow) + "\n");
        for (int i = 0; i < troopsSent.length; i++) {
            if (troopsSent[i] == troops[i])
                fw.write("r" + i + "\n");
        }
//        for (int i = 1; i < vertices; i++) {
//            for (int j = 2; j < vertices; j++) {
//                if (graphCopy[i][j] != 0 && graph[i][j] == 0) {
//                    if (i < 7) {
//                        fw.write("r" + (i - 1) + " " + "c" + (j - 7) + "\n");
//                    }
//                    else if (j == 7 + citiesNum) {
//                        fw.write("c" + (i - 7) + " " + "KL\n");
//                    }
//                    else {
//                        fw.write("c" + (i - 7) + " " + "c" + (j - 7) + "\n");
//                    }
//                }
//
//            }
//        }

        fw.close();



    }
    // bfs algorithm, calculates the path from source to the KL, returns an array containing the parent nodes
    // when tracked from KL index to the source it gives the full path
    public static int[] breadthFirstSearch(int source, int sink, int[][] graph) {
        int[] parent = new int[vertices]; // stores the path information, every index points to the parent's index
        LinkedList<Integer> queue = new LinkedList<>(); // a linked list implementation of the queue
        queue.add(source);
        int[] visitedVertices = new int[vertices]; // 1 if visited
        visitedVertices[source] = 1;
        parent[source] = -1;
        while (!queue.isEmpty()) { // start from the source, visit the neighbours and add them to the queue, continue with the next vertex
            int i = queue.poll();
            for (int j = 0; j < vertices; j++) {
                if (graph[i][j] == 0 || visitedVertices[j] == 1)
                    continue;
                //parent[j] = i;
                if (j == sink) { // sink is reached, return path
                    parent[j] = i;
                    return parent;
                }
                parent[j] = i; // update the parent node and visitedVertices array
                queue.add(j);
                visitedVertices[j] = 1;
            }
        }
        return new int[0]; // sink is never reached
    }
    // uses the Ford-Fulkerson algorithm to find the maximum flow from the imaginary source vertex to the King's Landing
    public static int fordFulkerson(int source, int sink, int[][] graph, int citiesNum, int[] troopsSent) {
        int maxFlow = 0;
        //int[][] residualGraph = new int[7 + citiesNum + 1][7 + citiesNum + 1];
        int[][] residualGraph = graph;

        while (true)  { // search for an augmented path
            int[] parent = breadthFirstSearch(source,sink, residualGraph);
            if (Arrays.equals(parent, new int[0])) // while there is a path
                break;
            int currentFlow = Integer.MAX_VALUE;
            int currentVertex = sink; // start from the last vertex-KL
            while (currentVertex != source) { // find the flow of the current path-the smallest capacity of the vertices on the path
                int currentParent = parent[currentVertex];
                currentFlow = Math.min(residualGraph[currentParent][currentVertex], currentFlow);
                currentVertex = currentParent;
            }
            currentVertex = sink;
            int region = -1;
            while (currentVertex != source) { // augment the flow - update the direct and residual capacities
                int currentParent = parent[currentVertex];
                residualGraph[currentParent][currentVertex] -= currentFlow;
                residualGraph[currentVertex][currentParent] += currentFlow;
                if (currentVertex < 7 && currentVertex > 0) { // if the current vertex is a region, store the troops sent from that region
                    region = currentVertex;
                    troopsSent[region - 1] += currentFlow;
                }
                currentVertex = currentParent;
            }
            maxFlow += currentFlow; // update the flow with every path
        }
        return maxFlow;
    }

}
