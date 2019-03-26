package wubbalubbadubdub.hw1700ransomware.data;


/**
 * Event.java
 * @author Damian, Lane
 * @version 1.0
 *
 * Event DataType for keeping track of events
 */
public class Event implements Comparable<Event> {

    private int id;
    private String date;
    private String name;
    private String creator;
    private String timeslots;

    /**
     * Constructor for an Event. Events will always be constructed this way.
     * @param inputID int ID of the event
     * @param inputDate String date of format MM/DD/YYYY
     * @param inputName String name of the Event Title
     * @param inputCreator String name of the Event Creator
     * @param inputTimeslots String timeslot list of scheduled timeslots
     */
    public Event(int inputID, String inputDate, String inputName, String inputCreator, String inputTimeslots) {
        id = inputID;
        name = inputName;
        date = inputDate;
        creator = inputCreator;
        timeslots = inputTimeslots;
    }

    /**
     * Allows events to be compared to each other.
     * @param otherEvent Event object of event to compare to
     * @return int < 0 if given event is later than current event. int = 0 if given event is same day as current. int > 0 otherwise
     */
    public int compareTo(Event otherEvent) {
        int[] currentDate = HelperMethods.getMonthDayYear(date);
        int[] otherDate = HelperMethods.getMonthDayYear(otherEvent.getDate());

        if (currentDate[2] == otherDate[2]) {
            if (currentDate[0] == otherDate[0]) {
                return currentDate[1] - otherDate[1];
            } else {
                return currentDate[0] - otherDate[0];
            }
        } else {
            return currentDate[2] - otherDate[2];
        }
    }

    // We will probably not need setters.

    // Getters

    /**
     * Getter for the Event ID
     * @return int ID
     */
    public int getID() { return id; }

    /**
     * Getter for the Date String
     * @return String date of format MM/DD/YYYY
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for the Event Name String
     * @return String event name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the Event Creator String
     * @return String event creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Getter for the Event Schedule String
     * @return String of all timeslots for event
     */
    public String getTimeslots() {
        return timeslots;
    }

}