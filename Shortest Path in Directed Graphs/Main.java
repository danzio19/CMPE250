import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        Scanner scan = new Scanner(inputFile);

        int noOfLocations = scan.nextInt();
        //Location[] locations = new Location[];

        int noOfFlags = scan.nextInt();
        String start = scan.next();
        String end = scan.next();
        ArrayList<String> flags = new ArrayList<>();
        scan.skip("\n");
        Collections.addAll(flags, scan.nextLine().split(" "));

        //ArrayList<Location> visitedLocations = new ArrayList<>();
        HashMap<String, Location> allLocations = new HashMap<>();
        PriorityQueue<Location> unvisitedLocations = new PriorityQueue<>(11, new LocationComparator());
        for (int i = 0; i < noOfLocations; i++) {
            String[] line = scan.nextLine().split(" ");
            String locationId = line[0];
            Location location = new Location(locationId, (i == 0) ? 0 : Integer.MAX_VALUE, flags.contains(locationId));
            for (int j = 1; j < line.length - 1; j += 2)
                location.getAdjacency().put(line[j], Integer.valueOf(line[j+1]));
            allLocations.put(locationId, location);
            unvisitedLocations.add(location);
        }
        for (Map.Entry<String, Location> locationSet: allLocations.entrySet()) {
            Location currentLocation = locationSet.getValue();
            for (Map.Entry<String, Integer> neighbourSet: currentLocation.getAdjacency().entrySet()) {
                Location neighbour = allLocations.get(neighbourSet.getKey());
                neighbour.getAdjacency().put(currentLocation.getId(), neighbourSet.getValue());
            }
        }

        FileWriter fw = new FileWriter(args[1]);
        fw.write(shortestPathLength(unvisitedLocations, allLocations, start, end) + "\n");


        for (Map.Entry<String, Location> entry : allLocations.entrySet()) {
            entry.getValue().setDistance(Integer.MAX_VALUE);
        }
        allLocations.get(flags.get(0)).setDistance(0);

        fw.write(String.valueOf(flagsDistance(allLocations, flags)));
        fw.close();







    }
    public static int shortestPathLength(PriorityQueue unvisitedLocations, HashMap allLocations, String start, String end) {
        HashSet<Location> visitedLocations = new HashSet<>();

        while(!unvisitedLocations.isEmpty()) {
            Location currentLocation = (Location) unvisitedLocations.poll();
            visitedLocations.add(currentLocation);
            for (Map.Entry<String, Integer> set: currentLocation.getAdjacency().entrySet()) {
                Location neighbour = (Location) allLocations.get(set.getKey());
                if (visitedLocations.contains(neighbour))
                    continue;
                int currentDistance = set.getValue();
                if (neighbour.getDistance() > currentLocation.getDistance() + currentDistance) {
                    unvisitedLocations.remove(neighbour);
                    neighbour.setDistance(currentLocation.getDistance() + currentDistance);
                    neighbour.setPrevious(currentLocation);
                    unvisitedLocations.add(neighbour);
                }
            }
            //unvisitedLocations.poll();
        }
        Location endLocation = (Location) allLocations.get(end);
        Location startLocation = (Location) allLocations.get(start);
        Location currentLocation = endLocation;
//        int totalDistance = 0;
//        while(currentLocation.getId() != startLocation.getId()) {
//            System.out.println(currentLocation.getId());
//            currentLocation = currentLocation.getPrevious();
//        }
        if (endLocation.getDistance() == Integer.MAX_VALUE && visitedLocations.contains(endLocation))
            return -1;

        return endLocation.getDistance();

    }
    public static int flagsDistance(HashMap<String,Location> allLocations, ArrayList<String> flags) {
        //PriorityQueue<Integer> totalDistances = new PriorityQueue<>();
        PriorityQueue<Location> unvisitedLocations = new PriorityQueue<>(11, new LocationComparator());
        unvisitedLocations.addAll(allLocations.values());
        HashSet<Location> visitedLocations = new HashSet<>();
        HashSet<Location> visitedFlags = new HashSet<>();
        int flagCount = 0;
        int totalDistance = 0;
        //Location currentLocation = unvisitedLocations.peek();
        while (flagCount < flags.size()) {
            Location currentLocation = unvisitedLocations.poll();
            visitedLocations.add(currentLocation);
            if (currentLocation.hasFlag()) {
                if (!visitedFlags.contains(currentLocation)) {
                    flagCount++;
                    totalDistance += currentLocation.getDistance();
                }
                visitedFlags.add(currentLocation);
                visitedLocations.clear();
            }
            for (Map.Entry<String, Integer> set : currentLocation.getAdjacency().entrySet()) {
                Location neighbour = allLocations.get(set.getKey());
                if (visitedLocations.contains(neighbour))
                    continue;
//                if (currentLocation.hasFlag() && neighbour.equals(currentLocation.getPrevious())) {
//                    visitedLocations.add(neighbour);
//                    continue;
//                }
                int currentDistance = set.getValue();
                if (neighbour.getDistance() >= currentLocation.getDistance() + currentDistance) {
                    unvisitedLocations.remove(neighbour);
                    neighbour.setDistance((currentLocation.hasFlag()) ? currentDistance :
                            currentLocation.getDistance() + currentDistance);
                    neighbour.setPrevious(currentLocation);
                    unvisitedLocations.add(neighbour);
                }
            }
        }
        if (totalDistance == Integer.MAX_VALUE)
            return -1;



        return totalDistance;
    }
    static class LocationComparator implements Comparator<Location> {

        @Override
        public int compare(Location l1, Location l2) {
            if (l1.getDistance() < l2.getDistance())
                return -1;
            else if (l1.getDistance() > l2.getDistance())
                return 1;
            else
                return 0;
        }
    }
}
