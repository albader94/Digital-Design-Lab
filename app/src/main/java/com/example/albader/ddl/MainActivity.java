package com.example.albader.ddl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends Activity implements View.OnClickListener {

    //Declare used variables and UI elements
    int count_blue, count_red, count_green = 0;
    Button blueButton, redButton, greenButton, button_btConnect;
    BluetoothSPP bt;
    TextView count_text;
    public ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Setting UI elements
        blueButton = (Button) findViewById(R.id.buttonBlue);
        redButton = (Button) findViewById(R.id.buttonRed);
        greenButton = (Button) findViewById(R.id.buttonGreen);
        button_btConnect = (Button) findViewById(R.id.buttonBT);
        count_text = (TextView) findViewById(R.id.count);

        //Setting onClickListeners
        blueButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
        button_btConnect.setOnClickListener(this);

        //Call the connection function
        bluetoothConnection();


        //When bluetooth is enabled
        if(bt.isBluetoothEnabled()) {
            bt.startService(BluetoothState.DEVICE_OTHER);
            clickableTrue();
            msg("Bluetooth Enabled");
        }
        else{
           clickableFalse();
            msg("Not Enabled");
        }

    }

    //When buttons are clicked, case statments.
    @Override
    public void onClick(View v){

        switch(v.getId()){

             /* -------------------------------------------------------------------
              * ------------------------Blue Button--------------------------------
              * ------------------------------------------------------------------*/

            case R.id.buttonBlue:
               blueButton.setBackgroundColor(0x00000000);
                count_blue++;
                count_text.setText("Count: " + count_blue);

                if(count_blue == 1){
                    count_text.setText("Count: " + count_blue);
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();

                        //When button is pressed once change color of button
                        blueButton.setBackgroundColor(0xFF0000FF);

                        //Error Handling
                        try {
                            //Send data to bluetooth device
                            bt.send("blue;", true);
                        }
                        catch (Exception e) {
                            msg("Error Blue");
                        }
                    }
                    else{
                       clickableFalse();
                    }
                }

                else{
                    count_text.setText("Count: " + count_blue);
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();

                        blueButton.setBackgroundColor(0x00000000);
                        count_blue = 0;
                    }

                    else{
                     clickableFalse();
                    }
                }

                break;

             /* ------------------------------------------------------------------
              * ------------------------Red Button--------------------------------
              * ------------------------------------------------------------------*/

            case R.id.buttonRed:
                redButton.setBackgroundColor(0x00000000);
                count_red++;

                if(count_red == 1) {
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();
                        //When button is pressed once change color of button
                        redButton.setBackgroundColor(0xFFFF0000);

                        //Send data to bluetooth device
                        //Error Handling
                        try {
                            bt.send("red;", true);
                        } catch (Exception e) {
                            msg("Error Red");
                        }
                    }
                    else{
                        clickableFalse();
                    }
                }

                else {
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();
                        redButton.setBackgroundColor(0x00000000);
                        count_red = 0;
                    }
                    else{
                        clickableFalse();
                    }
                }

                break;

             /* -------------------------------------------------------------------
              * ------------------------Green Button-------------------------------
              * -------------------------------------------------------------------*/

            case R.id.buttonGreen:
                greenButton.setBackgroundColor(0x00000000);
                count_green++;

                if(count_green == 1){
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();
                        //When button is pressed once change color of button
                        greenButton.setBackgroundColor(0xFF00FF00);

                        //Send data to bluetooth device
                        //Error Handling
                        try {
                            bt.send("green;", true);
                        } catch (Exception e) {
                            msg("Error Green");
                        }
                    }
                    else{
                        clickableFalse();
                    }

                }
                else{
                    if(bt.isBluetoothEnabled()) {
                        clickableTrue();
                        greenButton.setBackgroundColor(0x00000000);
                        count_green = 0;
                    }
                    else{
                        clickableFalse();
                    }
                }

                break;


            /*--------------------------------------------------------------------
              -----------------------Bluetooth Button-----------------------------
            * -------------------------------------------------------------------*/
            case R.id.buttonBT:

                //When bluetooth is on/off set elements on/off
                if(bt.isBluetoothEnabled()){
                    clickableTrue();
                }
                else{
                    clickableFalse();
                }

                //Intent that jumps to DeviceList class
                Intent intent = new Intent(getApplicationContext(), com.example.albader.ddl.DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                //onActivityResult(BluetoothState.REQUEST_CONNECT_DEVICE, RESULT_OK, intent);

                break;

            default:
                break;
        }
    }

    /*----------------------------------------------------------------------------------------------
    ---------------------------------Fast way to call Toast-----------------------------------------
    * --------------------------------------------------------------------------------------------*/
    private void msg(String s)
    {
        Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
    }


    /*----------------------------------------------------------------------------------------------
    --------Bluetooth onActivity , setting bluetooth connection and bluetooth listner---------------
    * --------------------------------------------------------------------------------------------*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                bt.connect(data);
                msg("Connected OK");
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                msg("Enabled OK");
            } else {
                // Do something if user doesn't choose any device (Pressed back)
                msg("Back, Please wait..!");
                finish();
            }
        }
    }

    public void bluetoothConnection(){
        //When data is recieved (listening)
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                msg("Data Incoming!");
            }
        });

        //Listen for connection status
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            //When connected successfully
            public void onDeviceConnected(String name, String address) {
                msg("CONNECTED");
            }

            @Override
            //When device is disconnected
            public void onDeviceDisconnected() {
                msg("DISCONNECTED");
            }

            @Override
            //When connection fails
            public void onDeviceConnectionFailed() {
                msg("CONNECTION FAILED");
            }
        });

        //Checks state of bluetooth
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            @Override
            public void onServiceStateChanged(int state) {
                if(state == BluetoothState.STATE_CONNECTED)
                    // Do something when successfully connected
                    msg("CONNECTED");
                else if(state == BluetoothState.STATE_CONNECTING)
                    // Do something while connecting
                    msg("Connecting, In progress!");
                else if(state == BluetoothState.STATE_LISTEN)
                    // Do something when device is waiting for connection
                    msg("Waiting for connection");
                else if(state == BluetoothState.STATE_NONE);
                    // Do something when device don't have any connection
                    msg("No Connection");
            }
        });
    }

    /*----------------------------------------------------------------------------------------------
    * -------------------------Clickable functions that turns on/off--------------------------------
    * -------button clicks for LED's are turned on and off based on the called function-------------
    * --------------------------------------------------------------------------------------------*/

    public void clickableTrue(){
        button_btConnect.setText("ONLINE: Ready to Pair");
        blueButton.setClickable(true);
        redButton.setClickable(true);
        greenButton.setClickable(true);
    }

    public void clickableFalse(){
        button_btConnect.setText("OFFLINE");
        blueButton.setClickable(false);
        redButton.setClickable(false);
        greenButton.setClickable(false);
    }
}
