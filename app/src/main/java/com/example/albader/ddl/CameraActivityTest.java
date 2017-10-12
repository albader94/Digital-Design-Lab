package com.example.albader.ddl;


import android.app.Activity;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class CameraActivityTest extends Activity{

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;


    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;    //To perform fundemental bluetooth tasks (connect, pair, etc).
    BluetoothSocket btSocket = null;        //To initiate and manage an ongoing bluetooth connection
    private boolean isBtConnected = false;

    //SPP (Serial Port Profile) UUID
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //Converting strings to bytes in order to send over inputStream
    String interruptStr = "Motion";
    byte[] interruptBytes = interruptStr.getBytes();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //view of the ledControl
        setContentView(R.layout.activity_camera_preview);


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        new ConnectBT().execute(); //Call the class to connect


        //Create instance of camera
        mCamera = getCameraInstance();

        //Create our preview and set it as content of our activity
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);


    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public Camera getCameraInstance(){  //MIGHT NEED TO BE STATIC
        Camera c = null;
        try{
            //open the camera
            c = Camera.open();
        }catch(Exception e){
            //Camera not available
            msg("Camera Un-Available");
        }

        return c;   //will return null if camera does not exsists
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if(picFile == null){
                Log.d("tag", "Error creating media file, check storage permissions: ");
                msg("Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(picFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("tag", "File not found: " + e.getMessage());
                msg("File not found");
            } catch (IOException e) {
                Log.d("tag", "Error accessing file: " + e.getMessage());
                msg("Error accessing file");
            }
        }
    };

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                msg("Failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /*-----------------------------------------------------------------------------------------------
     *--AsyncTask = Task that allows runing in background, consists of onPre, onPost, and doInBack---
     *----------------------------------------------------------------------------------------------*/
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            //show a progress dialog
            progress = ProgressDialog.show(CameraActivityTest.this, "Connecting...", "Please wait!!!");
        }

        @Override
        //while the progress dialog is shown, the connection is done in background
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    //get the mobile bluetooth device
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    //connects to the device's address and checks if it's available
                    BluetoothDevice connect = myBluetooth.getRemoteDevice(address);
                    //create a SPP connection
                    btSocket = connect.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection


                }
            }
            catch (IOException e)
            {
                //if the try failed, you can check the exception here
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        //after the doInBackground, it checks if everything went fine
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;

            }
            progress.dismiss();

            // On motion, when connected, take a picture
            if(ConnectSuccess){
                try {
                    btSocket.getInputStream().read(interruptBytes);
                    mCamera.takePicture(null, null, mPicture);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
