import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main{


    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        Scanner scan = new Scanner(inputFile);
        int noOfAcc = scan.nextInt();
        int noOfFlight = scan.nextInt();

        HashMap<String, ACC> accHashMap = new HashMap<>();
        for (int i = 0; i < noOfAcc; i++) {
            String accName = scan.next();
            ACC newACC = new ACC(accName);
            accHashMap.put(accName, newACC);
            scan.skip(" ");
            String[] airports = scan.nextLine().split(" ");
            for (int j = 0; j < airports.length; j++)
                newACC.addAirport(airports[j]);
        }

        for (int i = 0; i < noOfFlight; i++) {
            Flight newFlight = new Flight();
            newFlight.admissionTime = scan.nextInt();
            newFlight.flightCode = scan.next();
            newFlight.accCode = scan.next();
            newFlight.departureAirport = scan.next();
            newFlight.arrivalAirport = scan.next();
            for (int j = 0; j < 21; j ++)
                newFlight.operationTimes[j] = scan.nextInt();
            accHashMap.get(newFlight.accCode).addFlight(newFlight);
        }
        FileWriter fw = new FileWriter(args[1]);
        ArrayList<ACC> accArrayList = new ArrayList<>(accHashMap.values());
        for (ACC acc:accArrayList) {
            int totalTime = acc.operate();
            fw.write(acc.getCode());
            fw.write(" ");
            fw.write(String.valueOf(totalTime));
            fw.write(" ");
            for (ATC atc:acc.getAtcTable()) {
                if (atc != null)
                    fw.write(atc.code + " ");
            }
            fw.write("\n");
            //for (String line:acc.log)
                //System.out.println(line);

        }
        scan.close();
        fw.close();
    }
}
