package wubbalubbadubdub.hw1700ransomware.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseHelper.java
 * @author Damian, Lane
 * @version 1.0
 * This class contains helper methods that interact with the Database. This replaced the Dataclass
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Default constructor for the databasehelper.
     * @param context Always the entire application context because we want the database to be for the whole application.
     */
    public DatabaseHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    /**
     * Called when the DatabaseHelper class is created. Will create database tables if they do not exist.
     * @param db Current writable database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(DBContract.UserTable.CREATE_TABLE);
        db.execSQL(DBContract.EventTable.CREATE_TABLE);
        db.execSQL(DBContract.SignupTable.CREATE_TABLE);
    }

    /**
     * Called when the database version is changed in DBContract
     * @param db Current writable database
     * @param oldVersion Old version of DB. Set in DBContract
     * @param newVersion New version of DB. Set in DBContract
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete all tables
        db.execSQL(DBContract.UserTable.DROP_TABLE);
        db.execSQL(DBContract.EventTable.DROP_TABLE);
        db.execSQL(DBContract.SignupTable.DROP_TABLE);
        onCreate(db);
    }

    //region User Table Methods

    /**
     *
     * @param name the name of the new user to add
     * @return -1 for failure, otherwise will return the row inserted at.
     */
    public long addUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase(); // is this okay?

        ContentValues values = new ContentValues();
        values.put(DBContract.UserTable.COLUMN_NAME_NAME, name);

        return db.insert(DBContract.UserTable.TABLE_NAME, null, values);
    }

    /**
     * This method queries our User table for all Usernames
     * @return List of strings containing all users
     */
    public List<String> getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Even though we only get one column, SQLiteDatabase.query() requires a string array
        String[] columns = {
                DBContract.UserTable.COLUMN_NAME_NAME
        };
        String sortOrder = DBContract.UserTable.COLUMN_NAME_NAME + " COLLATE NOCASE ASC";

        Cursor query = db.query(
                DBContract.UserTable.TABLE_NAME,
                columns,
                null, null, null, null,
                sortOrder
        );

        List<String> names = new ArrayList<>();
        while (query.moveToNext()) {
            String name = query.getString(query.getColumnIndexOrThrow(DBContract.UserTable.COLUMN_NAME_NAME));
            names.add(name);
        }

        query.close();

        return names;
    }

    //endregion



    //region Event Table Methods

    /**
     * This function will add an event to the event table
     * @param e - Event object passed when the save button is clicked with valid event params
     * @return event ID
     * @since 1.0
     */
    public int addEvent(Event e) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.EventTable.COLUMN_NAME_TITLE, e.getName());
        values.put(DBContract.EventTable.COLUMN_NAME_TIMESLOTS, e.getTimeslots());
        values.put(DBContract.EventTable.COLUMN_NAME_CREATOR, e.getCreator());
        values.put(DBContract.EventTable.COLUMN_NAME_DAY, e.getDate());

        db.insert(DBContract.EventTable.TABLE_NAME, null, values); //Perform insertion

        //Get the ID of the event we just created and return it
        String[] columns = {DBContract.EventTable._ID};
        String sortOrder = DBContract.EventTable._ID + " DESC";


        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columns,
                null, null, null, null,
                sortOrder
        );
        query.moveToNext();
        int eventID = Integer.parseInt(query.getString(query.getColumnIndexOrThrow(DBContract.EventTable._ID)));
        query.close();

        return eventID;
    }

    /**
     * This function will get all events from the event table
     * @return A sorted ArrayList of Events from the Database
     * @since 1.0
     */
    public ArrayList<Event> getAllEvents() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Event> sortedListOfEvents = new ArrayList<>(); // Will be sorted through SQL

        String[] columns = {
                DBContract.EventTable._ID,
                DBContract.EventTable.COLUMN_NAME_TITLE,
                DBContract.EventTable.COLUMN_NAME_TIMESLOTS,
                DBContract.EventTable.COLUMN_NAME_CREATOR,
                DBContract.EventTable.COLUMN_NAME_DAY
        };

        //Sort by day for now. Could hypothetically get weird when multiple years are involved
        String sortOrder = DBContract.EventTable.COLUMN_NAME_DAY + " COLLATE NOCASE ASC";

        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columns,
                null, null, null, null,
                sortOrder
        );

        //Populate event vector
        while (query.moveToNext()) {

            int id;
            String title, timeslots, creator, day;

            id = Integer.parseInt(query.getString(query.getColumnIndexOrThrow(DBContract.EventTable._ID)));
            title = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));
            timeslots = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TIMESLOTS));
            creator = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));
            day = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_DAY));

            //Create Event object from row and add to Vector
            Event e = new Event(id, day, title, creator, timeslots); // LOL This stuff was in the wrong order... Come on guys...
            sortedListOfEvents.add(e);
        }

        query.close();

        return sortedListOfEvents;
    }

    /**
     * Get events from given user.
     * @param user - Username to retrieve events for.
     * @return Sorted Event vector of a given user's created events
     */
    public ArrayList<Event> getUserEvents(String user) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Event> sortedListOfEvents = new ArrayList<>(); // Will be sorted through SQL

        String[] userArr = {user}; //(Needs to be in an array to use as a WHERE argument)

        String[] columns = {
                DBContract.EventTable._ID,
                DBContract.EventTable.COLUMN_NAME_TITLE,
                DBContract.EventTable.COLUMN_NAME_TIMESLOTS,
                DBContract.EventTable.COLUMN_NAME_CREATOR,
                DBContract.EventTable.COLUMN_NAME_DAY
        };

        //Sort by day for now. Could hypothetically get weird when multiple years are involved
        String sortOrder = DBContract.EventTable.COLUMN_NAME_DAY + " COLLATE NOCASE ASC";

        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columns,
                "creator = ?", userArr, null, null,
                sortOrder
        );

        //Populate event List
        while (query.moveToNext()) {

            int id;
            String title, timeslots, creator, day;

            id = Integer.parseInt(query.getString(query.getColumnIndexOrThrow(DBContract.EventTable._ID)));
            title = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));
            timeslots = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TIMESLOTS));
            creator = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));
            day = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_DAY));

            //Create Event object from row and add to Vector
            Event e = new Event(id, day, title, creator, timeslots);
            sortedListOfEvents.add(e);
        }

        query.close();

        return sortedListOfEvents;
    }

    //endregion



    //region Signup Table Methods

    /**
     * @param eventID ID of event in Table
     * @return Event object containing all event info
     */
    public Event getEvent(int eventID) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                DBContract.EventTable.COLUMN_NAME_TIMESLOTS,
                DBContract.EventTable.COLUMN_NAME_CREATOR,
                DBContract.EventTable.COLUMN_NAME_DAY,
                DBContract.EventTable.COLUMN_NAME_TITLE
        };

        String[] where = {Integer.toString(eventID)};

        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columns,
                "_ID = ?", where , null, null,
                null
        );

        query.moveToNext();

        String date = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_DAY));
        String creator = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));
        String name = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));
        String timeslots = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TIMESLOTS));

        Event returnEvent = new Event(eventID, date, name, creator, timeslots);


        query.close();

        return returnEvent;
    }

    /**
     * This method will add a users availability to the signup table
     * @param eventID int ID of event
     * @param user String username that is signing up
     * @param availability Integer List of timeslots
     * @return long value of the row in the table that was created
     */
    public long addSignup(int eventID, String user, List<Integer> availability) {// TODO create entry in signups table
        SQLiteDatabase db = this.getWritableDatabase();

        String avail = HelperMethods.stringifyTimeslotInts(availability);

        ContentValues values = new ContentValues();
        values.put(DBContract.SignupTable.COLUMN_NAME_USER, user);
        values.put(DBContract.SignupTable.COLUMN_NAME_AVAIL, avail);
        values.put(DBContract.SignupTable.COLUMN_NAME_EVENT, eventID);

        return db.insert(DBContract.SignupTable.TABLE_NAME, null, values);
    }

    /**
     * This method will return a Hashmap of users along with availability for a given event
     * @param eventID int ID of event
     * @return Hashmap of user keypairs with availability keyvalues
     */
    public Map<String, String> getSignups(int eventID) { // TODO return list of signed up users(?) for given event
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> userSignup = new HashMap<>();

        String[] columns = {
                DBContract.SignupTable.COLUMN_NAME_USER,
                DBContract.SignupTable.COLUMN_NAME_AVAIL
        };
        String[] where = {Integer.toString(eventID)};

        Cursor query = db.query(
                DBContract.SignupTable.TABLE_NAME,
                columns,
                "eid = ?", where, null, null,
                null
        );

        while (query.moveToNext()) {
            userSignup.put(query.getString(query.getColumnIndexOrThrow(DBContract.SignupTable.COLUMN_NAME_USER)),
                    query.getString(query.getColumnIndexOrThrow(DBContract.SignupTable.COLUMN_NAME_AVAIL)));
        }

        return userSignup;
    }

    /**
     * This method will update a given user's availability for an event.
     * @param eventID int ID of event
     * @param user String username that is changing availability
     * @param availability Integer List of timeslots
     * @return int value of the row in the table that was updated
     */
    public int updateSignup(int eventID, String user, List<Integer> availability) {
        SQLiteDatabase db = this.getWritableDatabase();

        String avail = HelperMethods.stringifyTimeslotInts(availability);

        ContentValues values = new ContentValues();
        values.put(DBContract.SignupTable.COLUMN_NAME_AVAIL, avail);

        String selection = DBContract.SignupTable.COLUMN_NAME_USER + " = ? AND " + DBContract.SignupTable.COLUMN_NAME_EVENT + " = ?";
        String[] selectionArgs = {user, Integer.toString(eventID)};

        return db.update(
                DBContract.SignupTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }



    //endregion
}
