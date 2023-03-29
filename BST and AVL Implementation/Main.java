import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        Scanner scan = new Scanner(inputFile);

        String root = scan.nextLine(); // scanner for reading input file
        BinarySearchTree bst = new BinarySearchTree(root); // creating both tree instances
        AVLTree avlTree = new AVLTree(root);
        while (scan.hasNextLine()) { // read and operate the commands for bst and avlTree
            String[] line = scan.nextLine().split(" ");
            switch (line[0]) {

                case "ADDNODE": {
                    bst.addNode(line[1]);
                    avlTree.addNode(line[1]);
                    break;
                }
                case "DELETE": {
                    bst.removeNode(line[1]);
                    avlTree.removeNode(line[1]);
                    break;
                }
                case "SEND": {
                    bst.send(line[1], line[2]);
                    avlTree.send(line[1], line[2]);
                    break;
                }
            }
        }
        scan.close();

        FileWriter fw = new FileWriter(args[1].substring(0, args[1].length() - 4) + "_BST.txt"); // writer for bst
        for (int i = 0; i < bst.getLog().size(); i++) { // write bst's log to its output file
            fw.write(bst.getLog().get(i) + "\n");
        }
        fw.close();

        FileWriter fwAVL = new FileWriter(args[1].substring(0, args[1].length() - 4) + "_AVL.txt"); // writer for avlTree
        for (int i = 0; i < avlTree.getLog().size(); i++) { // write avlTree's log to its output file
            fwAVL.write(avlTree.getLog().get(i) + "\n");
        }
        fwAVL.close();

    }
}
