import java.util.PriorityQueue;

public class ATC {
    public String code;
    public  PriorityQueue<Flight> readyQueue;
    public boolean isOperating;

    public ATC(String code) {
        this.code = code;
        this.isOperating = false;
        this.readyQueue = new PriorityQueue<>();
    }
}
