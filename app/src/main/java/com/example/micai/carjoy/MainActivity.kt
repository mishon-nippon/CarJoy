package com.example.micai.carjoy

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*



class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ENABLE_BT = 0
        const val MacAddress = "20:17:09:28:45:55"

    }

    var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var LOG_TAG = "myLOGS"
    var btAdapter: BluetoothAdapter? = null
    var btSocket: BluetoothSocket? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.btAdapter = BluetoothAdapter.getDefaultAdapter()

        if (btAdapter != null) {
            if (btAdapter!!.isEnabled()) {
                val toast = Toast.makeText(applicationContext,
                        "Bluetooth включен", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                val btintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(btintent, REQUEST_ENABLE_BT)
            }

        } else {
            val toast = Toast.makeText(applicationContext,
                    "Bluetooth отсутствует", Toast.LENGTH_SHORT)
            toast.show()
        }




        button_for.setOnClickListener {
            val thread = MyThread()
            val code = thread.sendData("1")
        }

        button_back.setOnClickListener {
            val thread = MyThread()
            val code = thread.sendData("2")
        }


        button_left.setOnClickListener {
            val thread = MyThread()
            val code = thread.sendData("3")

        }
    }

    /*      button_right.setOnClickListener {
            fun onClick(v: View) {
                MyThread.sendData("4")
            }
        }*/

    override fun onResume() {
        super.onResume();
        if (btAdapter != null) {
            if (btAdapter!!.isEnabled()) {
                var device = btAdapter!!.getRemoteDevice(MacAddress);
                Log.d(LOG_TAG, "***Получили удаленный Device***" + device.getName());
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.d(LOG_TAG, "...Создали сокет...");
                btAdapter!!.cancelDiscovery();
                Log.d(LOG_TAG, "***Отменили поиск других устройств***")
                Log.d(LOG_TAG, "***Соединяемся...***")
                btSocket!!.connect()
                Log.d(LOG_TAG, "***Соединение успешно установлено***")

                val thread = MyThread()
                val connect = thread.ConnectedThread(btSocket!!)

            }
        }
    }

    override fun onPause() {
        super.onPause();

        Log.d(LOG_TAG, "...In onPause()...");

        if (btAdapter != null) {
            if (btAdapter!!.isEnabled()) {
                btSocket!!.close()

            }
        }//OnPause


    }
}







