package com.sweatdreamer.temp2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // variables for the Bluetooth feature
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    private boolean BLUETOOTH_ADAPTER_WAS_ENABLED = false;

    // variables for the sound feature
    int volume = 0;
    MediaPlayer player;
    AudioManager audio;

    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;

    //
    TextView myLabel;
    EditText myTextbox;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    List<Integer> sensorData = new ArrayList<Integer>();

    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public List<ArrayList<Integer>> heartBeats = new ArrayList<ArrayList<Integer>>();
    boolean beatHigh = false;
    View storeView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Start of the heartBtn */
        Button heartBtn = findViewById(R.id.heartBeatBtn);
        heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heartBeatIntent = new Intent(getApplicationContext(), Heartbeat.class);
                startActivity(heartBeatIntent);
            }
        });
        /* End of the heartBtn */

        /* Start of the playBtn */
        audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        /* End of the PlayBtn */

        /*Bluetooth*/
        Button sessionBtn = findViewById(R.id.startBtn);
        sessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent blackscreen = new Intent(getApplicationContext(), BlackscreenActivity.class);
                startActivity(blackscreen);
                connectToDevice();
            }
        });
    }

    public void connectToDevice(){
        //findBT();
//        try {
//            openBT();
//        } catch (IOException ex) { }


//        try {
//            closeBT();
//        } catch (IOException ex) { }
    }


    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
             //myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-06")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        //myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final int data = encodedBytes[0];
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            sensorData.add(data);
                                            int average = 0;
                                            int maxCount = 0;
                                            for (int i = sensorData.size(); i >= 0 && i > sensorData.size()-10; i-- ) {
                                                maxCount += 1;
                                                average += sensorData.get(i);
                                            }
                                            if (sensorData.get(sensorData.size()) > average) {
                                                beatHigh = true;
                                            } else if (sensorData.get(sensorData.size()) < average) {
                                                beatHigh = false;
                                            }

                                            //activate it now
                                            Intent redscreen = new Intent(getApplicationContext(), RedscreenActivity.class);
                                            redscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(redscreen);
                                            startMusic();
                                            finish();
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    /* Start of the playBtn */
    public void startMusic() {
        volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
        if(player == null) {
            player = MediaPlayer.create(getApplicationContext(), R.raw.piano_music);
        }
        player.start();
    }

    public void pauseMusic() {
        if(player != null) {
            player.pause();
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        }
    }

    public void stopMusic() {
        stopMusicPlaying();
    }

    private void stopMusicPlaying() {
        if(player != null) {
            player.release();
            player = null;
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        }
    }
    /* End of the PlayBtn */

}