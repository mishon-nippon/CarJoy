package com.example.micai.carjoy

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

 class MyThread {
    //Отдельный поток для передачи данных

        private var copyBtSocket: BluetoothSocket? = null
        private var OutStream: OutputStream? = null
        private var InStream: InputStream? = null

     fun ConnectedThread(socket:BluetoothSocket){
         copyBtSocket = socket
            try{
                OutStream = socket.outputStream
                InStream = socket.inputStream
            } catch (e:IOException){}

        }


        fun sendData(message: String) {
           val msgBuffer = message.toByteArray();


            try {
                OutStream?.write(msgBuffer);
            } catch (e:IOException) {}
        }

        fun cancel(){
            try {
                copyBtSocket?.close();
            }catch(e:IOException){}
        }


}






