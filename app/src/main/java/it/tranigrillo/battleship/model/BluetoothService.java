package it.tranigrillo.battleship.model;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService {
    private static final String NAME = Build.MODEL;
    private static final String TAG = "ConnectionManager";
    private static final UUID DEVICE_UUID = UUID.fromString("e6f21f64-da8c-4764-8e7f-a729e9857996");
    private final BluetoothAdapter bluetoothAdapter;
    private AcceptThread insecureAcceptTread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private Context context;
    private ProgressDialog progressDialog;
    private Intent messages;

    public BluetoothService(Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        messages = new Intent("messagesData");
        start();
    }

    /**
     * Questo thread rimane in ascolto in attesa di connessioni in arrivo
     * (proprio come farebbe un server).
     * Rimane attivo finchè non accetta una connessione oppure finchè non viene terminato.
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;


        //Crea un thread 'server' che ascolta il canale e attende che qualcuno provi a connettersi
        private AcceptThread() {

            BluetoothServerSocket tmp = null;
            //si usa un oggetto temporaneo che poi viene assegnato al serverSocket perchè serverSocket è un oggetto Final
            try {
                // UUID è una stringa identificativa dell'app.
                // Viene usata sia dal client che dal server, serve ad identificare univocamente la connessione che viene creata.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, DEVICE_UUID);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + DEVICE_UUID);
            }
            catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e); //se non riesci a creare il thread server
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: AcceptThread Running...");
            BluetoothSocket socket = null;

            //Continua a restare in ascolto finchè non arriva un eccezione oppure finchè non arriva una richiesta di connessione
            Log.d(TAG, "run: listening:...");
            try {
                Log.d(TAG, "run: RFCOM server socket start.....");
                socket = serverSocket.accept(); //tenta di accettare una connessione in arrivo
            }
            catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if (socket != null) {
                //Una connessione è stata accettata
                //Questo thread verrà terminato
                //La connessione verrà gestita tramite un altro thread che creo adesso

                connected(socket); //crea il nuovo thread
                try {
                    serverSocket.close(); //chiudi questo thread
                } catch (IOException e) {
                    Log.e(TAG, "AcceptedTread: IOException:" + e.getMessage());
                }
            }
            Log.d(TAG, "END insecureAcceptThread ");
        }

        // Termina il thread 'ascoltatore'
        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }


    /**
     * Questo thread rimane attivo finchè non riesci a creare una connessione con un altro dispositivo.
     * Rimane attivo sia se la connessione riesce o fallisce
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice device;

        ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "ConnectThread: started.");
            this.device = device;

            //si usa un oggetto temporaneo che poi viene assegnato al bluetoothSocket perchè serverSocket è un oggetto Final
            BluetoothSocket tmp = null;
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + DEVICE_UUID);
                // Cerca di creare un socket per connettersi con l'altro dispositivo (BluetoothDevice).
                tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
                Log.d(TAG, "ConnectThread: InsecureRfcommSocket created.");
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: IOException: " + e.getMessage());
            }
            bluetoothSocket = tmp;
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery(); //termina la ricerca per non consumare inutilmente le risorse
            try {
                /*
                connettiti all'altro dispositivo tramite una socket
                questa è una chiamata bloccante, l'esecuzione rimane bloccata finchè
                la connnessione non viene effettuata con successo OPPURE viene lanciata un'eccezione.
                */

                bluetoothSocket.connect(); //tenta di connettersi
                Log.d(TAG, "run: ConnectThread connected.");

            }
            catch (IOException connectException) {
                //connessione non riuscita: chiudi il socket e termina il thread
                try {
                    bluetoothSocket.close(); //chiudi il socket
                    Log.d(TAG, "connectThread: run: Closed Socket.");
                }
                catch (IOException closeException) {
                    Log.e(TAG, "connectThread: run: Unable to close connection in socket " + closeException.getMessage());
                }
                Log.d(TAG, "run: connectThread: Could not connect to UUID: " + DEVICE_UUID);

                progressDialog.dismiss(); //chiudi il dialog
                showToastInThread(context, "Could not connect to the device");
                return;
            }
            /*
            connessione riuscita con successo.
            chiudi questo thread (era responsabile solo di creare la connessione)
            apri il thread responsabile di gestire la connessione.
            */
            connected(bluetoothSocket);
        }

        //chiudi il socket client e termina il thread
        void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                bluetoothSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "cancel: close() of bluetoothSocket in ConnectThread failed. " + e.getMessage());
            }
        }
    }


    public void showToastInThread(final Context context,final String str){
        Looper.prepare();
        Toast.makeText(context, str,Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * avvia il thread 'ascoltatore' del server
     */
    public synchronized void start() {
        // cancella ogni altro thread che sta tentando di creare una connessione
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // se non esiste un thread che sta ascoltando, creane uno
        if (insecureAcceptTread == null) {
            insecureAcceptTread = new AcceptThread();
            insecureAcceptTread.start(); //
        }
    }

    public synchronized void stop() {
        // cancella ogni altro thread che sta tentando di creare una connessione
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        start();
    }

    /**
     * AcceptThread rimane in ascolto per una connessione
     * ConnectThread cerca di avviare la connessione
     */

    public void startClient(BluetoothDevice device){
        Log.d(TAG, "startClient: Attempting Connection...");
        //initprogress dialog
        progressDialog = ProgressDialog.show(context,"Connecting Bluetooth","Please Wait...",true);
        connectThread = new ConnectThread(device);
        connectThread.start();
    }


    /**
     *  ConnectedThread è il thread responsabile di gestire la connessione, dopo che ConnectThread l'ha creata.
     *  Inoltre gestisce input e output dei dati tramite stream.
     **/

    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocketSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private byte[] buffer; // buffer per lo stream

        ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting...");
            bluetoothSocketSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //chiudi la progressDialog quando la connessione è stabilita
            try {
                progressDialog.dismiss();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

            //ottieni gli stream di input e di output
            //si usa un oggetto temporaneo perchè gli stream sono Final
            try {
                tmpIn = socket.getInputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        @Override
        public void run() {

            buffer = new byte[1024]; //buffer per memorizzare il contenuto dello stream
            int numBytes; // byte ottenuti dalla read()

            // Continua ad ascoltare lo InputStream finchè non viene lanciata un'eccezione
            while (true) {
                try {
                    // leggi da InputStream
                    numBytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, numBytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    messages.putExtra("message", incomingMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(messages);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        //viene chiamata quando l'utente deve inviare una stringa all'altro dispositivo.
        public void write(byte[] bytes) {

            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                outputStream.write(bytes);
            }
            catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        // chiama questo metodo quando devi chiudere la connessione
        public void cancel() {
            try {
                bluetoothSocketSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void connected(BluetoothSocket bluetoothSocket) {
        Log.d(TAG, "connected: Starting.");

        // avvia il thread per gestire la connessione ed effettuare le trasmissioni
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */

    //NB questo è un metodo di BluetoothService da NON confonderlo con quello all'interno di ConnectedThread
    public void write(byte[] out) {
        ConnectedThread r; //crea un oggetto temporaneo

        Log.d(TAG, "write: Write Called.");

        try {
            connectedThread.write(out); //passa al metodo write di ConnectedThread i byte dati dall'utente.
        }
        catch (NullPointerException e){
            Log.e(TAG, "write:" + e.getMessage());
            Toast.makeText(context, "Connection Error: restart connection", Toast.LENGTH_LONG).show();
        }
    }
}

