package wubbalubbadubdub.hw1700ransomware;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import wubbalubbadubdub.hw1700ransomware.data.DatabaseHelper;
import wubbalubbadubdub.hw1700ransomware.data.Event;
import wubbalubbadubdub.hw1700ransomware.data.HelperMethods;

/**
 * This page list all the activities created by other users.
 * Also this page allows the user to add an event which brings them to the AddEvent page
 * @author Dustin, Lane, Damian
 * @version 1.0
 */
public class ListActivity extends Activity {

    private DatabaseHelper dbHelper;
    private String currentUser;
    private boolean format = false; //Time formatting boolean

    final int DARK_CYAN = Color.rgb(0, 151, 167);
    final int BLUE_MAT = Color.rgb(2,136,209); //For dolling up the event titles
    final int LIGHT_BG = Color.rgb(201, 221, 255);
    final int DARK_BG = Color.rgb(153, 192, 255);

    private boolean allEvents = true; //Determines whether to show all events or own events

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        currentUser = intent.getStringExtra("currentUser");
        TextView welcomeText = (TextView) findViewById(R.id.tvWelcome);
        welcomeText.setText("Hello " + currentUser + "!");

        populateEventTable();

    }

    /**
     * Handles All Events toggle button
     * @param v - (Given view)
     */
    public void toggleEventList(View v) {
        allEvents = !allEvents;
        populateEventTable();
    }

    /**
     * Toggles time formatting in table
     * @param v - (Given view)
     */
    public void toggleFormat(View v) {
        format = !format;
        populateEventTable();
    }

    /**
     * Populates event table
     */
    private void populateEventTable() {
        TableLayout layout = (TableLayout) findViewById(R.id.eventTableLayout);
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        //Clears the table before (re)populating
        for (int i = 0; i < layout.getChildCount(); i++) {
            View row = layout.getChildAt(i);
            if (row instanceof TableRow) ((ViewGroup) row).removeAllViews();
            layout.removeAllViews();
        }

        tableRowParams.setMargins(10, 2, 10, 2);
        ArrayList<Event> events = (allEvents) ? dbHelper.getAllEvents() : dbHelper.getUserEvents(currentUser);
        Collections.sort(events);

        int numberOfEvents = events.size();
        int rowBG = 1;

        //Title row creation
        TableRow titleRow = new TableRow(this);
        titleRow.setBackgroundColor(DARK_CYAN);

        TableRow.LayoutParams titleLayout = new TableRow.LayoutParams();
        titleLayout.setMargins(0, 10, 10, 15);

        TextView titleName = new TextView(this);
        TextView titleCreator = new TextView(this);
        TextView titleDay = new TextView(this);
        TextView titleTimeslots = new TextView(this);

        titleCreator.setLayoutParams(titleLayout);
        titleName.setLayoutParams(titleLayout);
        titleDay.setLayoutParams(titleLayout);
        titleTimeslots.setLayoutParams(titleLayout);

        titleName.setPadding(3, 10, 0, 10);
        titleCreator.setPadding(0, 10, 0, 10);
        titleDay.setPadding(0, 10, 0, 10);
        titleTimeslots.setPadding(0, 10, 0, 10);

        titleName.setText("Event Name");
        titleCreator.setText("Creator");
        titleDay.setText("Date");
        titleTimeslots.setText("Scheduled Times");

        titleName.setTextSize(18);
        titleCreator.setTextSize(18);
        titleDay.setTextSize(18);
        titleDay.setTextSize(18);
        titleTimeslots.setTextSize(18);

        titleName.setTypeface(null, Typeface.BOLD);
        titleCreator.setTypeface(null, Typeface.BOLD);
        titleDay.setTypeface(null, Typeface.BOLD);
        titleTimeslots.setTypeface(null, Typeface.BOLD);

        titleRow.addView(titleName);
        titleRow.addView(titleCreator);
        titleRow.addView(titleDay);
        titleRow.addView(titleTimeslots);

        layout.addView(titleRow, tableRowParams);


        //Generate rest of event rows
        for (int i = 0; i < numberOfEvents; i++) {
            TableRow row = new TableRow(this);

            row.setBackgroundColor((rowBG % 2 == 1) ? LIGHT_BG : DARK_BG);

            Event workingEvent = events.get(i);

            TableRow.LayoutParams tvLayout = new TableRow.LayoutParams();
            tvLayout.setMargins(0, 10, 10, 15);

            TextView eventName = new TextView(this);
            TextView eventCreator = new TextView(this);
            TextView eventDay = new TextView(this);
            TextView eventTimeslots = new TextView(this);

            eventName.setLayoutParams(tvLayout);
            eventCreator.setLayoutParams(tvLayout);
            eventDay.setLayoutParams(tvLayout);
            eventTimeslots.setLayoutParams(tvLayout);

            eventName.setPadding(3, 10, 0, 10);
            eventCreator.setPadding(0, 10, 0, 10);
            eventDay.setPadding(0, 10, 0, 10);
            eventTimeslots.setPadding(10, 10, 0, 10);

            eventName.setTextSize(20);
            eventName.setTypeface(null, Typeface.BOLD);
            eventName.setTextColor(BLUE_MAT);
            eventCreator.setTextSize(20);
            eventDay.setTextSize(20);
            eventTimeslots.setTextSize(20);

            //Prettier formatting, convert timeslots into timestring through series of parsing methods
            eventCreator.setText(workingEvent.getCreator());
            eventDay.setText(workingEvent.getDate());
            eventName.setText(workingEvent.getName());
            eventTimeslots.setText(HelperMethods.getTimeString(HelperMethods.listifyTimeslotInts(workingEvent.getTimeslots()), format));

            row.addView(eventName);
            row.addView(eventCreator);
            row.addView(eventDay);
            row.addView(eventTimeslots);

            row.setLayoutParams(tableRowParams);

            final int workingEventID = workingEvent.getID();

            row.setOnClickListener(new View.OnClickListener() {
                int id = workingEventID;

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("eventID", id);

                    startActivity(intent);
                }
            });


            layout.addView(row, tableRowParams);
            rowBG++;

        }
    }

    /**
     * Handles Add new Event button
     * @param v - (given view)
     */
    public void newEvent(View v) {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.putExtra("currentUser", currentUser);
        finish();
        startActivity(intent);
    }


}
