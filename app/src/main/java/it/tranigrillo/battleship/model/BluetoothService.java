package it.tranigrillo.battleship.model;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.UUID;


public class BluetoothService extends Service {
    private static final String NAME = Build.MODEL;
    private static final String TAG = "BluetoothService";
    private static final UUID DEVICE_UUID = UUID.fromString("e6f21f64-da8c-4764-8e7f-a729e9857996");

    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    private static int state = STATE_NONE;
    private final IBinder binder = new LocalBinder();

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothAdapter bluetoothAdapter;

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToastInThread(String message) {
        Looper.prepare();
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    public BluetoothService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setVisibility(int mode){
        try {
            //il metodo è Private, può essere chiamato qui soltanto tramite Reflection
            Method method = BluetoothAdapter.class.getMethod("setScanMode", int.class);
            method.invoke(bluetoothAdapter, mode);
        }
        catch ( NoSuchMethodException |IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, "Failed to turn on bluetooth device discoverability.", e);
        }
    }

    private void setState(int state)
    {
        BluetoothService.state = state;
        Intent status = new Intent("status");
        status.putExtra("status", String.valueOf(state));
        Log.d(TAG, String.valueOf(state));
        getApplication().sendBroadcast(status);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothAdapter.enable();
        setVisibility(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        String service = intent.getStringExtra("service");
        assert service != null;
        Log.d(TAG, "Service called: " + service);
        if (service.equals("listen")) {
            startListening();
        }
        if (service.equals("scan")) {
            startDiscovery();
        }
        if (service.equals("listen_scan")) {
            startListening();
            startDiscovery();
        }
        if (service.equals("stop_scan")) {
            stopDiscovery();
        }

        if (service.equals("connect_device")) {
            String address = intent.getStringExtra("device_address");
            connectToDevice(address);
        }

        if (service.equals("write")) {
            String message = intent.getStringExtra("message");
            sendData(message);
        }

        if (service.equals("get_status")) {
            Intent status = new Intent("status");
            status.putExtra("status", String.valueOf(state));
            Log.d(TAG, String.valueOf(state));
            sendBroadcast(status);
        }

        return START_REDELIVER_INTENT;
    }

    public void startDiscovery(){
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public void stopDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    public synchronized void startListening() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread!= null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        acceptThread = new AcceptThread();
    }

    public synchronized void connectToDevice(String deviceMAC) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceMAC);
        if (state == STATE_CONNECTING) {
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }
        }
        if (state == STATE_CONNECTED) {
            return;
        }
        connectThread = new ConnectThread(device);
        try {
            connectThread.start();
            showToast("Connecting...");
            Log.d(TAG, "connectToDevice: Connecting...");
        } catch (Exception e) {
            showToast("Could not connect to the device");
        }
    }

    public void sendData(String message) {
        if (connectedThread != null) {
            connectedThread.write(message.getBytes());
            showToast("Sent data");
            Log.d(TAG, "Sending data...");
        }
        else {
            Toast.makeText(this, "Failed to send data", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Failed to send data");
        }
    }

    // stoppa il servizio quado si è al difuori di esso
    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);
        if (acceptThread != null){
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        bluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    // stoppa il servizio quado si in esso
    private synchronized void stop() {
        setState(STATE_NONE);
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        if (bluetoothAdapter != null){
            bluetoothAdapter.cancelDiscovery();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        this.stop();
        super.onDestroy();
    }

//    ----------------------------------------------------------------------------

    public class LocalBinder extends Binder {
        public BluetoothService getService(){
            return BluetoothService.this;
        }

    }

    /** Questo thread rimane in ascolto in attesa di connessioni in arrivo
      *  (proprio come farebbe un server).
      *  Rimane attivo finchè non accetta una connessione oppure finchè non viene terminato.
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
            start();
        }

        @Override
        public void run() {
            Log.d(TAG, "run: AcceptThread Running...");
            BluetoothSocket socket = null;

            //Continua a restare in ascolto finchè non arriva un eccezione oppure finchè non arriva una richiesta di connessione
            Log.d(TAG, "run: listening:...");
            setState(STATE_LISTEN);
            try {
                Log.d(TAG, "run: RFCOM server socket start.....");
                socket = serverSocket.accept(); //tenta di accettare una connessione in arrivo
            }
            catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
                setState(STATE_NONE);
            }

            if (socket != null) {

                //Una connessione è stata accettata
                //Questo thread verrà terminato
                //La connessione verrà gestita tramite un altro thread che creo adesso
                connectedThread = new ConnectedThread(socket); //crea il nuovo thread
                try {
                    serverSocket.close(); //chiudi questo thread
                } catch (IOException e) {
                    Log.e(TAG, "AcceptedTread: IOException:" + e.getMessage());
                }
            }
            Log.d(TAG, "END insecureAcceptThread ");
        }

        // Termina il thread 'ascoltatore'
        void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    /** Questo thread rimane attivo finchè non riesci a creare una connessione con un altro dispositivo.
      *  Rimane attivo sia se la connessione riesce o fallisce
      */
    private class ConnectThread extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice device;

        private ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "ConnectThread: started.");
            this.device = device;

            //si usa un oggetto temporaneo che poi viene assegnato al bluetoothSocket perchè serverSocket è un oggetto Final
            BluetoothSocket tmp = null;
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + DEVICE_UUID);
                // Cerca di creare un socket per connettersi con l'altro dispositivo (BluetoothDevice).
                tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
                Log.d(TAG, "ConnectThread: InsecureRfcommSocket created.");
                setState(STATE_CONNECTING);
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: IOException: " + e.getMessage());
                showToastInThread("Could not connect to the device");
                setState(STATE_NONE);
            }
            bluetoothSocket = tmp;
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery(); //termina la ricerca per non consumare inutilmente le risorse
            try {
                // connettiti all'altro dispositivo tramite una socket
                // questa è una chiamata bloccante, l'esecuzione rimane bloccata finchè
                // la connnessione non viene effettuata con successo OPPURE viene lanciata un'eccezione.

                bluetoothSocket.connect(); //tenta di connettersi
                Log.d(TAG, "run: ConnectThread connected.");

            } catch (IOException connectException) {
                //connessione non riuscita: chiudi il socket e termina il thread
                try {
                    bluetoothSocket.close(); //chiudi il socket
                    Log.d(TAG, "connectThread: run: Closed Socket.");
                } catch (IOException closeException) {
                    Log.e(TAG, "connectThread: run: Unable to close connection in socket " + closeException.getMessage());
                }
                Log.d(TAG, "run: connectThread: Could not connect to UUID: " + DEVICE_UUID);
                showToastInThread("Could not connect to the device");
                setState(STATE_NONE);
                return;
            }
            /*
            connessione riuscita con successo.
            chiudi questo thread (era responsabile solo di creare la connessione)
            apri il thread responsabile di gestire la connessione.
            */
            connectedThread = new ConnectedThread(bluetoothSocket);
            setState(STATE_CONNECTED);
        }

        void cancel() {
            try {
                bluetoothSocket.close();
                Log.d(TAG, "connectThread: run: Closed Socket.");
            }
            catch (IOException closeException) {
                Log.e(TAG, "connectThread: run: Unable to close connection in socket " + closeException.getMessage());
            }
        }
    }

    /**
      *  ConnectedThread è il thread responsabile di gestire la connessione, dopo che ConnectThread l'ha creata.
      *  Inoltre gestisce input e output dei dati tramite stream.
      */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocketSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private byte[] buffer; // buffer per lo stream

        private ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting...");
            bluetoothSocketSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

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
            start();
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
                    Intent read = new Intent("read");
                    read.putExtra("message", incomingMessage);
                    sendBroadcast(read);
                    Log.d(TAG, "InputStream: " + incomingMessage);
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
                setState(STATE_NONE);
            }
            catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}

//public class BluetoothService {
//    private static final String NAME = Build.MODEL;
//    private static final String TAG = "ConnectionManager";
//    private static final UUID DEVICE_UUID = UUID.fromString("e6f21f64-da8c-4764-8e7f-a729e9857996");
//    private final BluetoothAdapter bluetoothAdapter;
//    private AcceptThread insecureAcceptTread;
//    private ConnectThread connectThread;
//    private ConnectedThread connectedThread;
//    private Context context;
//    private ProgressDialog progressDialog;
//    private Intent messages;
//
//    public BluetoothService(Context context, BluetoothAdapter bluetoothAdapter) {
//        this.context = context;
//        this.bluetoothAdapter = bluetoothAdapter;
//        messages = new Intent("messagesData");
//        start();
//    }
//
//    /**
//     * Questo thread rimane in ascolto in attesa di connessioni in arrivo
//     * (proprio come farebbe un server).
//     * Rimane attivo finchè non accetta una connessione oppure finchè non viene terminato.
//     */
//    private class AcceptThread extends Thread {
//        private final BluetoothServerSocket serverSocket;
//
//
//        //Crea un thread 'server' che ascolta il canale e attende che qualcuno provi a connettersi
//        private AcceptThread() {
//
//            BluetoothServerSocket tmp = null;
//            //si usa un oggetto temporaneo che poi viene assegnato al serverSocket perchè serverSocket è un oggetto Final
//            try {
//                // UUID è una stringa identificativa dell'app.
//                // Viene usata sia dal client che dal server, serve ad identificare univocamente la connessione che viene creata.
//                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, DEVICE_UUID);
//                Log.d(TAG, "AcceptThread: Setting up Server using: " + DEVICE_UUID);
//            }
//            catch (IOException e) {
//                Log.e(TAG, "Socket's listen() method failed", e); //se non riesci a creare il thread server
//            }
//            serverSocket = tmp;
//        }
//
//        @Override
//        public void run() {
//            Log.d(TAG, "run: AcceptThread Running...");
//            BluetoothSocket socket = null;
//
//            //Continua a restare in ascolto finchè non arriva un eccezione oppure finchè non arriva una richiesta di connessione
//            Log.d(TAG, "run: listening:...");
//            try {
//                Log.d(TAG, "run: RFCOM server socket start.....");
//                socket = serverSocket.accept(); //tenta di accettare una connessione in arrivo
//            }
//            catch (IOException e) {
//                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
//            }
//
//            if (socket != null) {
//                //Una connessione è stata accettata
//                //Questo thread verrà terminato
//                //La connessione verrà gestita tramite un altro thread che creo adesso
//
//                connected(socket); //crea il nuovo thread
//                try {
//                    serverSocket.close(); //chiudi questo thread
//                } catch (IOException e) {
//                    Log.e(TAG, "AcceptedTread: IOException:" + e.getMessage());
//                }
//            }
//            Log.d(TAG, "END insecureAcceptThread ");
//        }
//
//        // Termina il thread 'ascoltatore'
//        public void cancel() {
//            Log.d(TAG, "cancel: Canceling AcceptThread.");
//            try {
//                serverSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
//            }
//        }
//    }
//
//
//    /**
//     * Questo thread rimane attivo finchè non riesci a creare una connessione con un altro dispositivo.
//     * Rimane attivo sia se la connessione riesce o fallisce
//     */
//    private class ConnectThread extends Thread {
//
//        private final BluetoothSocket bluetoothSocket;
//        private final BluetoothDevice device;
//
//        ConnectThread(BluetoothDevice device) {
//            Log.d(TAG, "ConnectThread: started.");
//            this.device = device;
//
//            //si usa un oggetto temporaneo che poi viene assegnato al bluetoothSocket perchè serverSocket è un oggetto Final
//            BluetoothSocket tmp = null;
//            try {
//                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + DEVICE_UUID);
//                // Cerca di creare un socket per connettersi con l'altro dispositivo (BluetoothDevice).
//                tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
//                Log.d(TAG, "ConnectThread: InsecureRfcommSocket created.");
//            } catch (IOException e) {
//                Log.e(TAG, "ConnectedThread: IOException: " + e.getMessage());
//            }
//            bluetoothSocket = tmp;
//        }
//
//        @Override
//        public void run() {
//            bluetoothAdapter.cancelDiscovery(); //termina la ricerca per non consumare inutilmente le risorse
//            try {
//                /*
//                connettiti all'altro dispositivo tramite una socket
//                questa è una chiamata bloccante, l'esecuzione rimane bloccata finchè
//                la connnessione non viene effettuata con successo OPPURE viene lanciata un'eccezione.
//                */
//
//                bluetoothSocket.connect(); //tenta di connettersi
//                Log.d(TAG, "run: ConnectThread connected.");
//
//            }
//            catch (IOException connectException) {
//                //connessione non riuscita: chiudi il socket e termina il thread
//                try {
//                    bluetoothSocket.close(); //chiudi il socket
//                    Log.d(TAG, "connectThread: run: Closed Socket.");
//                }
//                catch (IOException closeException) {
//                    Log.e(TAG, "connectThread: run: Unable to close connection in socket " + closeException.getMessage());
//                }
//                Log.d(TAG, "run: connectThread: Could not connect to UUID: " + DEVICE_UUID);
//
//                progressDialog.dismiss(); //chiudi il dialog
//                showToastInThread(context, "Could not connect to the device");
//                return;
//            }
//            /*
//            connessione riuscita con successo.
//            chiudi questo thread (era responsabile solo di creare la connessione)
//            apri il thread responsabile di gestire la connessione.
//            */
//            connected(bluetoothSocket);
//        }
//
//        //chiudi il socket client e termina il thread
//        void cancel() {
//            try {
//                Log.d(TAG, "cancel: Closing Client Socket.");
//                bluetoothSocket.close();
//            }
//            catch (IOException e) {
//                Log.e(TAG, "cancel: close() of bluetoothSocket in ConnectThread failed. " + e.getMessage());
//            }
//        }
//    }
//
//
//    public void showToastInThread(final Context context,final String str){
//        Looper.prepare();
//        Toast.makeText(context, str,Toast.LENGTH_LONG).show();
//        Looper.loop();
//    }
//
//    /**
//     * avvia il thread 'ascoltatore' del server
//     */
//    public synchronized void start() {
//        // cancella ogni altro thread che sta tentando di creare una connessione
//        if (connectThread != null) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//
//        // se non esiste un thread che sta ascoltando, creane uno
//        if (insecureAcceptTread == null) {
//            insecureAcceptTread = new AcceptThread();
//            insecureAcceptTread.start(); //
//        }
//    }
//
//    public synchronized void stop() {
//        // cancella ogni altro thread che sta tentando di creare una connessione
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//        start();
//    }
//
//    /**
//     * AcceptThread rimane in ascolto per una connessione
//     * ConnectThread cerca di avviare la connessione
//     */
//    public ConnectedThread startClient(BluetoothDevice device){
//        Log.d(TAG, "startClient: Attempting Connection...");
//        //initprogress dialog
//        progressDialog = ProgressDialog.show(context,"Connecting Bluetooth","Please Wait...",true);
//        connectThread = new ConnectThread(device);
//        connectThread.start();
//        return connectedThread;
//    }
//
//
//    /**
//     *  ConnectedThread è il thread responsabile di gestire la connessione, dopo che ConnectThread l'ha creata.
//     *  Inoltre gestisce input e output dei dati tramite stream.
//     **/

//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket bluetoothSocketSocket;
//        private final InputStream inputStream;
//        private final OutputStream outputStream;
//        private byte[] buffer; // buffer per lo stream
//
//        ConnectedThread(BluetoothSocket socket) {
//            Log.d(TAG, "ConnectedThread: Starting...");
//            bluetoothSocketSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            //chiudi la progressDialog quando la connessione è stabilita
//            try {
//                progressDialog.dismiss();
//            }
//            catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//
//            //ottieni gli stream di input e di output
//            //si usa un oggetto temporaneo perchè gli stream sono Final
//            try {
//                tmpIn = socket.getInputStream();
//            }
//            catch (IOException e) {
//                Log.e(TAG, "Error occurred when creating input stream", e);
//            }
//            try {
//                tmpOut = socket.getOutputStream();
//            }
//            catch (IOException e) {
//                Log.e(TAG, "Error occurred when creating output stream", e);
//            }
//            inputStream = tmpIn;
//            outputStream = tmpOut;
//        }
//
//        @Override
//        public void run() {
//
//            buffer = new byte[1024]; //buffer per memorizzare il contenuto dello stream
//            int numBytes; // byte ottenuti dalla read()
//
//            // Continua ad ascoltare lo InputStream finchè non viene lanciata un'eccezione
//            while (true) {
//                try {
//                    // leggi da InputStream
//                    numBytes = inputStream.read(buffer);
//                    String incomingMessage = new String(buffer, 0, numBytes);
//                    Log.d(TAG, "InputStream: " + incomingMessage);
//                    messages.putExtra("message", incomingMessage);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(messages);
//                } catch (IOException e) {
//                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
//                    break;
//                }
//            }
//        }
//
//        //viene chiamata quando l'utente deve inviare una stringa all'altro dispositivo.
//        public void write(byte[] bytes) {
//
//            String text = new String(bytes, Charset.defaultCharset());
//            Log.d(TAG, "write: Writing to outputstream: " + text);
//            try {
//                outputStream.write(bytes);
//            }
//            catch (IOException e) {
//                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
//            }
//        }
//
//        // chiama questo metodo quando devi chiudere la connessione
//        public void cancel() {
//            try {
//                bluetoothSocketSocket.close();
//            }
//            catch (IOException e) {
//                Log.e(TAG, "Could not close the connect socket", e);
//            }
//        }
//    }
//
//    private void connected(BluetoothSocket bluetoothSocket) {
//        Log.d(TAG, "connected: Creating ConnectedThread...");
//
//        // avvia il thread per gestire la connessione ed effettuare le trasmissioni
//        connectedThread = new ConnectedThread(bluetoothSocket);
//        connectedThread.start();
//    }
//
//    /**
//     * Write to the ConnectedThread in an unsynchronized manner
//     *
//     * @param out The bytes to write
//     * @see ConnectedThread#write(byte[])
//     */
//
//    //NB questo è un metodo di BluetoothService da NON confonderlo con quello all'interno di ConnectedThread
//    public void write(byte[] out) {
//        ConnectedThread r; //crea un oggetto temporaneo
//
//        Log.d(TAG, "write: Write Called.");
//
//        try {
//            connectedThread.write(out); //passa al metodo write di ConnectedThread i byte dati dall'utente.
//
//        } catch (NullPointerException e) {
//            Log.e(TAG, "write:" + e.getMessage());
//            Toast.makeText(context, "Connection Error: restart connection", Toast.LENGTH_LONG).show();
//        }
//    }
//}

