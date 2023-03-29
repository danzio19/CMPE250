import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Project1 {
    public static void main(String[] args) throws IOException {



        File inputFile = new File(args[0]);
        Scanner scan = new Scanner(inputFile);

        File outputFile = new File(args[1]);
        FileWriter fw = new FileWriter(outputFile);

        FactoryImpl factory = new FactoryImpl(0);

        while (scan.hasNextLine()) {
            String[] line = scan.nextLine().split(" ");

            switch (line[0]){
                case "AF": {
                    int productId = Integer.valueOf(line[1]);
                    Integer productValue = Integer.valueOf(line[2]);
                    factory.addFirst(new Product(productId, productValue));
                    break;
                }
                case "AL": {
                    int productId = Integer.valueOf(line[1]);
                    Integer productValue = Integer.valueOf(line[2]);
                    factory.addLast(new Product(productId, productValue));
                    break;
                }
                case "A": {
                    int index = Integer.valueOf(line[1]);
                    int productId = Integer.valueOf(line[2]);
                    int productValue = Integer.valueOf(line[3]);
                    try {
                        factory.add(index, new Product(productId, productValue));
                    }
                    catch (IndexOutOfBoundsException e) {
                        fw.write("Index out of bounds.\n");
                    }
                    break;
                }
                case "RF": {
                    try {
                        Product removedProduct = factory.removeFirst();
                        fw.write(removedProduct.toString() + "\n");
                    } catch (NoSuchElementException e) {
                        fw.write("Factory is empty.\n");
                    }
                    break;
                }
                case "RL": {
                    try {
                        Product removedProduct = factory.removeLast();
                        fw.write(removedProduct.toString() + "\n");
                    } catch (NoSuchElementException e) {
                        fw.write("Factory is empty.\n");
                    }
                    break;
                }
                case "RI": {
                    int index = Integer.valueOf(line[1]);
                    try {
                        Product removedProduct = factory.removeIndex(index);
                        fw.write(removedProduct.toString() + "\n");
                    } catch (IndexOutOfBoundsException e) {
                        fw.write("Index out of bounds.\n");
                    }
                    break;
                }
                case "RP": {
                    Integer removedProductValue = Integer.valueOf(line[1]);
                    try {
                        Product removedProductWithValue = factory.removeProduct(removedProductValue);
                        fw.write(removedProductWithValue.toString() + "\n");
                    } catch (NoSuchElementException e) {
                        fw.write("Product not found.\n");
                    }
                    break;
                }
                case "F": {
                    int productId = Integer.valueOf(line[1]);
                    try {
                        Product foundProduct = factory.find(productId);
                        fw.write(foundProduct.toString() + "\n");
                    } catch (NoSuchElementException e) {
                        fw.write("Product not found.\n");
                    }
                    break;
                }
                case "G": {
                    int index = Integer.valueOf(line[1]);
                    try {
                        Product product = factory.get(index);
                        fw.write(product.toString() + "\n");
                    }
                    catch (IndexOutOfBoundsException e) {
                        fw.write("Index out of bounds.\n");
                    }
                    break;
                    }
                case "U": {
                    int newProductId = Integer.valueOf(line[1]);
                    Integer newProductValue = Integer.valueOf(line[2]);
                    try {
                        Product beforeUpdate = factory.update(newProductId, newProductValue);
                        fw.write(beforeUpdate.toString() + "\n");
                    }
                    catch(NoSuchElementException e){
                        fw.write("Product not found.\n");
                    }
                    break;
                }
                case "FD": {
                    int numRemoved = factory.filterDuplicates();
                    fw.write(numRemoved + "\n");
                    break;
                }
                case "R": {
                    factory.reverse();
                    fw.write(factory.toString() + "\n");
                    break;
                }
                case "P": {

                    fw.write(factory.toString() + "\n");
                }

            }

        }
        scan.close();
        fw.close();
    }
}
