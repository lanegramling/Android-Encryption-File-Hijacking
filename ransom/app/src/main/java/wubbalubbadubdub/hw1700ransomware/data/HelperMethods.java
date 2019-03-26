package wubbalubbadubdub.hw1700ransomware.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * HelperMethods.java
 * @author Lane, Damian
 * @version 1.0
 * Contains methods used to assist functionality of activities
 */
public class HelperMethods {

    /**
     * Private constructor so that this class is never initialized
     */
    private HelperMethods(){}

    /**
     * @param timeslot - int from 0-47, represents the 48 30min timeslots from 00:00-23:30
     * @param format - True: 24h format | False: 12h format
     * @return - A time formatted in either 12h or 24h format.
     * @since 1.0
     */
    public static String toTime(int timeslot, boolean format) {

        int hour;
        String min;

        //Convert 0-47 integer format to 12h or 24h format
        if (format) hour = timeslot / 2;
        else hour = (timeslot >= 26) ? ((timeslot - 24) / 2) : (timeslot / 2);
        min = ((timeslot % 2) == 0) ? "00" : "30";
        if (!format && (hour == 0)) hour = 12; //Special case: 0(int)->12:00(12h)

        String time = hour + ":" + min; //Build time string
        time = (format && (hour < 10)) ? ("0" + time) : time; //Add leading zero if needed in 24h format
        if (!format) time = (timeslot < 24) ? time + "am" : time + "pm"; // AM/PM for 12h format

        return time;
    }

    /**
     * @param month - month of a given date
     * @param day - day of a given date
     * @param year - year of a given date
     * @return a string in MM/DD/YYYY format
     * @since 1.0
     */
    public static String dateToString(int month, int day, int year)
    {return (month + "/" + day + "/" + year);}

    /**
     * @param date - A date in the string form as created by dateToString()
     * @return [0]: month | [1]: day | [2]: year (all ints)
     */
   /* public static int[] dateSplitString(String date) {
        int[] splitDate = {Integer.parseInt(date.substring(0,2)),
                            Integer.parseInt(date.substring(3,5)),
                            Integer.parseInt(date.substring(6))};
        return splitDate;
    }*/

    /**
     * @return a string in MM/DD/YYYY format of the current year
     * @since 1.0
     */
    public static int[] getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int[] date = {month, day, year};
        return date;
    }

    /**
     * Converts List of timeslots to a readable integer string
     * @param timeslots - list of timeslots in integer form
     * @param format - 12h/24h format boolean
     * @return concatenated string of timeslot list
     */
    public static String getTimeString(List<Integer> timeslots, boolean format) {
        // Sort it
        Collections.sort(timeslots);

        String timestring = "";

        int prevTime = -1;
        int workingTimeslot = -1;
        for(Integer slot : timeslots) {
            if (workingTimeslot == -1) {// First iteration
                workingTimeslot = slot;
            } else if (slot != prevTime + 1) {
                // Make time with workingtimeslot and prevTime
                timestring = timestring + toTime(workingTimeslot, format) + "-" + toTime(prevTime + 1, format) + ", ";
                workingTimeslot = slot;
            }
            prevTime = slot;
        }
        if (workingTimeslot != -1) {
            // At the end finish out the working slot.
            timestring = timestring + toTime(workingTimeslot, format) + "-" + toTime((prevTime + 1) % 48, format);
        }
        if (workingTimeslot == 0 && prevTime == 47) timestring = "ALL DAY LONG";

        if (timestring.isEmpty()) timestring = "NOT AVAILABLE AT ALL";

        return timestring;
    }


    /**
     * Counterpart to listifyTimeslotInts (list->String)
     * @param timeslotInts - List of timeslots in integer form
     * @return comma separated list of timeslots as a String for storage in db
     */
    public static String stringifyTimeslotInts(List<Integer> timeslotInts) {
        //Build string -- will look like "0,1,2,4,5" for example
        String stringList = "";
        for (Integer slot : timeslotInts)
            stringList += (timeslotInts.indexOf(slot) != (timeslotInts.size() - 1)) ? (slot + ",") : slot;
        return stringList;
    }

    /**
     * Counterpart to stringifyTimeslotInts (String->list)
     * @param timeslotString - comma separated String list formatted for storage in db
     * @return List of timeslots in integer form
     */
    public static List<Integer> listifyTimeslotInts(String timeslotString) {
        List<String> timeslotStrs = new ArrayList<>();
        List<Integer> timeslotInts = new ArrayList<>();

        timeslotStrs = Arrays.asList(timeslotString.split("\\s*,\\s*")); //Regex to interpret CSVs
        if (!timeslotString.isEmpty()) {
            for (String slot : timeslotStrs) timeslotInts.add(Integer.parseInt(slot));
        }

        return timeslotInts;
    }

    /**
     * Conversion of event strings to integer values
     * @param eventDateString String date of format MM/DD/YYYY
     * @return Int array with index 0 = month, 1 = day, 2 = year
     */
    public static int[] getMonthDayYear(String eventDateString) {
        String[] mmddyyyy = eventDateString.split("/");

        int[] returnArray = {Integer.parseInt(mmddyyyy[0]), Integer.parseInt(mmddyyyy[1]), Integer.parseInt(mmddyyyy[2])};

        return returnArray;
    }

}
