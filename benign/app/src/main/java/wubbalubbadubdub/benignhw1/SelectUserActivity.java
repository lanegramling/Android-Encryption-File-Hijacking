package wubbalubbadubdub.benignhw1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import wubbalubbadubdub.benignhw1.data.DatabaseHelper;

/**
 * This is the starting activity for our application. Here the user will select or add users
 * @author Damian, Lane
 * @version 1.0
 */
public class SelectUserActivity extends Activity {
    private DatabaseHelper dbHelper;
    private Toast statusMessage;
    private ListView userList;

    /**
     * This function is called when the activity is first created
     * @param savedInstanceState Every oncreate needs this. Allows to revert to previous state
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        dbHelper = new DatabaseHelper(getApplicationContext());
        statusMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        userList = (ListView) findViewById(R.id.lvUsers);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = userList.getItemAtPosition(position).toString();

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("currentUser", selectedUser);
                startActivity(intent);
            }
        });
        populateUsers();
    }

    /**
     * This function utilizes the DatabaseHelper class to populate a listview with all users
     * in the database
     * @since 1.0
     */
    private void populateUsers() {
        List<String> users = dbHelper.getUsers();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                users );

        userList.setAdapter(arrayAdapter);
    }

    /**
     * This function will utilize the DatabaseHelper to add a new user
     * @param v The View that fired the addUser() function. In this case it is the addUserButton
     * @since 1.0
     */
    public void addUser(View v) {
        EditText textbox = (EditText) findViewById(R.id.newUsername);
        String name = textbox.getText().toString().trim(); //removes any lead/trailing spaces as well


        if (isValidName(name)) statusMessage.setText(name + " was added to the list of users");
        statusMessage.show();
        populateUsers();
    }

    /**
     *
     * @param name the string to check the validity of
     * @return false if the name contains invalid characters
     * @since 1.0
     */
    private boolean isValidName(String name) {

        //Conditions for valid username creation
        if (TextUtils.isEmpty(name)){                    //Empty name
            statusMessage.setText("ERROR: Please input a name for the new user");
            return false;
        } else if (!name.matches("[a-zA-Z\\s]+")) {    //Non a-z characters in name
            statusMessage.setText("ERROR: Name contains invalid characters");
            return false;
        } else if (dbHelper.addUser(name) == -1) {       //name already exists
            statusMessage.setText("ERROR: That Username already exists!");
            return false;
        }else return true;
    }
}
