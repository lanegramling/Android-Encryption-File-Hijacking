package wubbalubbadubdub.hw1700ransomware.data;


import android.provider.BaseColumns;

/**
 * DBContract.java
 * @author Damian
 * @version 1.0
 * This class contains the contract work that will help our database class
 */
public final class DBContract {

    // Version should be changed IF any schemas are MODIFIED
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "database.db";


    /**
     * Constructor is private so the contract can never be somehow initialized
     */
    private DBContract() {}

    /**
     * {Class} Users
     * This defines the columns for our Users table
     * @since 1.0
     */
    public static class UserTable implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_NAME + " TEXT COLLATE NOCASE UNIQUE);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * {Class} Events
     * This defines the columns for our Events table.
     * @since 1.0
     */
    public static class EventTable implements BaseColumns { // If we implement BaseColumns, we can utilize _ID as a default column
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TIMESLOTS = "timeslots";
        public static final String COLUMN_NAME_CREATOR = "creator";
        public static final String COLUMN_NAME_DAY = "day";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_DAY + " TEXT," +
                COLUMN_NAME_TIMESLOTS + " TEXT," +
                COLUMN_NAME_CREATOR + " TEXT);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * {Class} Signups
     * This defines the columns for our Signups table
     * @since 1.0
     */
    public static class SignupTable implements BaseColumns {
        public static final String TABLE_NAME = "signups";
        public static final String COLUMN_NAME_EVENT = "eid";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_AVAIL = "availability";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_EVENT + " INTEGER," +
                COLUMN_NAME_USER + " TEXT," +
                COLUMN_NAME_AVAIL + " TEXT);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
