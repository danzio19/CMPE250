import java.util.HashMap;

public class Location implements Comparable {
    private String id;
    private HashMap<String, Integer> adjacency;
    private int distance;
    private boolean hasFlag;
    //    private boolean visited;
    private Location previous;
    public Location(String id,int distance, boolean hasFlag) {
        this.id = id;
        this.distance = distance;
        this.hasFlag = hasFlag;
//        this.visited = false;
        this.adjacency = new HashMap<String, Integer>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Integer> getAdjacency() {
        return adjacency;
    }

    public void setAdjacency(HashMap<String, Integer> adjacency) {
        this.adjacency = adjacency;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }

//    public boolean isVisited() {
//        return visited;
//    }
//
//    public void setVisited(boolean visited) {
//        this.visited = visited;
//    }

    public Location getPrevious() {
        return previous;
    }

    public void setPrevious(Location previous) {
        this.previous = previous;
    }

    public int compareTo(Location location) {
        return this.getId().compareTo(location.getId());

    }
    public int compareTo(Object o) {
        return compareTo((Location) o);
    }

}
