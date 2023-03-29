public class Flight implements Comparable{

    public long admissionTime;
    public String flightCode;
    public String accCode;
    public String departureAirport;
    public String arrivalAirport;
    public int[] operationTimes;
    public int currentOperation;
    public boolean isNew;

    public Flight() {
        this.operationTimes = new int[21];
        this.isNew = true;
        this.currentOperation = 0;
    }

    public Flight(String flightCode) {
        this.flightCode = flightCode;
        this.operationTimes = new int[21];
        this.isNew = true;
        this.currentOperation = 0;
    }

    public long getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(long admissionTime) {
        this.admissionTime = admissionTime;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getAccCode() {
        return accCode;
    }

    public void setAccCode(String accCode) {
        this.accCode = accCode;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public int[] getOperationTimes() {
        return operationTimes;
    }

    public void setOperationTimes(int[] operationTimes) {
        this.operationTimes = operationTimes;
    }
    public int compareTo(Flight flight) {
        if (this.admissionTime < flight.getAdmissionTime())
            return -1;
        else if (this.admissionTime > flight.getAdmissionTime())
            return 1;
        else {
            if (this.isNew && !flight.isNew)
                return -1;
            else if (!this.isNew && flight.isNew)
                return 1;
            else {
                return flightCode.compareTo(flight.getFlightCode());
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        return compareTo((Flight) o);
    }
}
