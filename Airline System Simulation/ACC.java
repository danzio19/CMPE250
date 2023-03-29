import java.util.*;

public class ACC {

    private String code;
    // String[] airports;
    private ATC[] atcTable;
    private PriorityQueue<Flight> readyQueue;
    private int totalTime;
    public ArrayList<String> log;


    public ACC(String code) {
        this.code = code;
        // this.airports = new String[1000];
        this.atcTable = new ATC[1000];
        this.readyQueue = new PriorityQueue<>();
        this.log = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ATC[] getAtcTable() {
        return atcTable;
    }

    public void setAtcTable(ATC[] atcTable) {
        this.atcTable = atcTable;
    }

    public PriorityQueue<Flight> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue(PriorityQueue<Flight> readyQueue) {
        this.readyQueue = readyQueue;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int hashAirport(String airport) {
        int initialSlot = 0;
        for (int i = 0; i < airport.length(); i++) {
            initialSlot += airport.charAt(i) * Math.pow(31, i);
        }
        initialSlot = initialSlot % 1000;
        while (atcTable[initialSlot] != null) {
            initialSlot++;
            if (initialSlot == 1000)
                initialSlot = 0;
        }
        return initialSlot;
    }
    public void addAirport(String airport) {
        int initialSlot = hashAirport(airport);
        atcTable[initialSlot] = new ATC(airport + String.format("%03d", initialSlot));
    }
    public int findAirport(String airport) {
        int initialSlot = 0;
        for (int i = 0; i < airport.length(); i++) {
            initialSlot += airport.charAt(i) * Math.pow(31, i);
        }
        initialSlot = initialSlot % 1000;
        while (!atcTable[initialSlot].code.contains(airport)) {
            initialSlot++;
        }
        return initialSlot;
    }
    public void addFlight(Flight flight) {
        readyQueue.add(flight);
    }

    public int operate() {
        this.totalTime = 0;
        List<Integer> operations = Arrays.asList(0,2,10,12,20);
        HashSet<Integer> accOperations = new HashSet<>(operations);
        List<Integer> waiting = Arrays.asList(1,11);
        HashSet<Integer> waitingOperations = new HashSet<>(waiting);
        List<Integer> waitingATC = Arrays.asList(4,6,8,14,16,18);
        HashSet<Integer> waitingOperationsATC = new HashSet<>(waitingATC);
        int accCounter = 0;
        ArrayList<ATC> operatingATCs = new ArrayList<>();

        while (!readyQueue.isEmpty() || operatingATCs.size() != 0) {
            boolean operationDone = false;

            Flight currentFlight = readyQueue.peek();
            boolean accWaiting = true;
            boolean bypassATC = false;
            if (currentFlight != null) {
                if (totalTime >= currentFlight.admissionTime) {
                    accWaiting = false;
                    log.add(totalTime + " acc " + currentFlight.flightCode);
                    if (currentFlight.operationTimes[currentFlight.currentOperation] == 0) { // next operation
                        //bypassATC = true;
                        currentFlight.currentOperation++;
                        accCounter = 0;
                        if (waitingOperations.contains(currentFlight.currentOperation)) { // waiting-requeue with adjusted admission time
                            currentFlight.admissionTime = totalTime + currentFlight.operationTimes[currentFlight.currentOperation];
                            log.add(totalTime + " acc " + currentFlight.flightCode + " waiting until " + currentFlight.admissionTime);
                            currentFlight.operationTimes[currentFlight.currentOperation] = 0;
                            currentFlight.currentOperation++;
                            readyQueue.poll();
                            currentFlight.isNew = false;
                            readyQueue.add(currentFlight);
                        } else if (currentFlight.currentOperation == 3) { // move to departure atc
                            ATC departureATC = atcTable[findAirport(currentFlight.departureAirport)];
                            currentFlight.admissionTime = totalTime;
                            currentFlight.isNew = true;
                            log.add(totalTime + " acc " + currentFlight.flightCode + " to departure atc " + currentFlight.admissionTime);
                            departureATC.readyQueue.add(currentFlight);
                            if (!operatingATCs.contains(departureATC))
                                operatingATCs.add(departureATC);
                            readyQueue.poll();
                        } else if (currentFlight.currentOperation == 13) { // move to arrival atc
                            ATC arrivalATC = atcTable[findAirport(currentFlight.arrivalAirport)];
                            currentFlight.admissionTime = totalTime;
                            log.add(totalTime + " acc " + currentFlight.flightCode + " to arrival atc " + currentFlight.admissionTime);
                            currentFlight.isNew = true;
                            arrivalATC.readyQueue.add(currentFlight);
                            if (!operatingATCs.contains(arrivalATC))
                                operatingATCs.add(arrivalATC);
                            readyQueue.poll();
                        } else if (currentFlight.currentOperation == 21) { // terminate flight
                            readyQueue.poll();
                            log.add(totalTime + " acc " + currentFlight.flightCode + " terminated");
                        }

                    } else if (accOperations.contains(currentFlight.currentOperation)) { // running
                        if (accCounter == 30 && currentFlight.operationTimes[currentFlight.currentOperation] != 0) { // 30 operations-to the back of the queue
                            readyQueue.poll();
                            currentFlight.isNew = false;
                            currentFlight.admissionTime = totalTime;
                            readyQueue.add(currentFlight);
                            accCounter = 0;
                            bypassATC = true;
                        }
                        else {
                            currentFlight.operationTimes[currentFlight.currentOperation]--;
                            operationDone = true;
                            // totalTime++;
                            accCounter++;
                        }

                    }
                }
            }
            //if (bypassATC)
                //continue;
            // ATC operations
            boolean allATCsWaiting = true;
            ArrayList<Flight> atcOperations = new ArrayList<>();
            for (int i = 0; i < operatingATCs.size(); i++) {
                ATC currentATC = operatingATCs.get(i);
                Flight currentFlightATC = currentATC.readyQueue.peek();
                if (currentFlightATC == currentFlight)
                    continue;
                if (currentFlightATC != null) {
                    if (totalTime >= currentFlightATC.admissionTime) {
                        allATCsWaiting = false;
                        if (currentFlightATC.operationTimes[currentFlightATC.currentOperation] == 0) { // next operation
                            currentFlightATC.currentOperation++;
                            bypassATC = true;
                            if (waitingOperationsATC.contains(currentFlightATC.currentOperation)) {
                                currentFlightATC.admissionTime = totalTime + currentFlightATC.operationTimes[currentFlightATC.currentOperation];
                                log.add(totalTime + " " + currentATC.code + " " +currentFlightATC.flightCode + " waiting until " + currentFlightATC.admissionTime);
                                currentFlightATC.operationTimes[currentFlightATC.currentOperation] = 0;
                                currentATC.readyQueue.poll();
                                currentFlightATC.currentOperation++;
                                currentFlightATC.isNew = true;
                                currentATC.readyQueue.add(currentFlightATC);
                            } else if (currentFlightATC.currentOperation == 10 || currentFlightATC.currentOperation == 20) { // move to acc
                                currentATC.readyQueue.poll();
                                if (currentATC.readyQueue.isEmpty())
                                    operatingATCs.remove(currentATC);
                                currentFlightATC.admissionTime = totalTime;
                                log.add(totalTime + " " + currentATC.code + " " + currentFlightATC.flightCode + " to acc " + currentFlightATC.admissionTime);
                                currentFlightATC.isNew = true;
                                this.readyQueue.add(currentFlightATC);
                            }
                        } else if (!bypassATC) { // running
                            //atcOperations.add(currentFlightATC);
                            currentFlightATC.operationTimes[currentFlightATC.currentOperation]--;
                            log.add(totalTime + " atc " + currentFlightATC.flightCode);
                            operationDone = true;
                        }
                    }

                }
            }
            /*if (!bypassATC) {
                operationDone = true;
                for (Flight flight : atcOperations)
                    flight.operationTimes[flight.currentOperation]--;
            }*/
            if (operationDone || (accWaiting && allATCsWaiting))
                totalTime++;
        }




        return totalTime;
    }

}



