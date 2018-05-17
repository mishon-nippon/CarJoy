package com.example.micai.carjoy

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.controls_activity.*
import kotlinx.android.synthetic.main.controls_activity.view.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*
import android.widget.Toast
import android.view.View.OnTouchListener
import android.R.attr.button





class ControlsActivity : AppCompatActivity() {
    companion object {
        lateinit var MacAddress: String
        var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        lateinit var m_progress: ProgressDialog
        var m_bluetoothSocket: BluetoothSocket? = null
        var m_Connected: Boolean = false
    }

    private var motorLeft = 0
    private var motorRight = 0
    private var pwmBtnMotorLeft: Int = 0
    private var pwmBtnMotorRight: Int = 0
    private var commandLeft: String? = null
    private var commandRight: String? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.controls_activity)

        MacAddress = intent.getStringExtra(MainActivity.MAC_ADRESS)

        ConnectToDevice(this).execute()

        this.pwmBtnMotorLeft = Integer.parseInt(resources.getText(R.string.default_pwmBtnMotorLeft) as String)
        this.pwmBtnMotorRight = Integer.parseInt(resources.getText(R.string.default_pwmBtnMotorRight) as String)
        this.commandLeft = resources.getText(R.string.default_commandLeft) as String
        this.commandRight = resources.getText(R.string.default_commandRight) as String



        button_for.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                        motorLeft = pwmBtnMotorLeft
                        motorRight = pwmBtnMotorRight
                        sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                    }

            if (event.action == MotionEvent.ACTION_UP){
                        motorLeft = 0
                        motorRight = 0
                        sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                    }
            false
                })



            button_back.setOnTouchListener(OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    motorLeft = -pwmBtnMotorLeft
                    motorRight = -pwmBtnMotorRight
                    sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                }

                if (event.action == MotionEvent.ACTION_UP){
                    motorLeft = 0
                    motorRight = 0
                    sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                }
                false
            })

                   button_left.setOnTouchListener(OnTouchListener { v, event ->
                       if (event.action == MotionEvent.ACTION_DOWN) {
                           motorLeft = pwmBtnMotorLeft
                           motorRight = 0
                           sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                       }

                       if (event.action == MotionEvent.ACTION_UP){
                           motorLeft = 0
                           motorRight = 0
                           sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                       }
                       false
                   })

                   button_right.setOnTouchListener(OnTouchListener { v, event ->
                       if (event.action == MotionEvent.ACTION_DOWN) {
                           motorLeft = 0
                           motorRight = pwmBtnMotorRight
                           sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                       }

                       if (event.action == MotionEvent.ACTION_UP){
                           motorLeft = 0
                           motorRight = 0
                           sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                       }
                       false
                   })
       }





    fun sendData(input: String) {
        if (m_bluetoothSocket != null) { try {
            m_bluetoothSocket!!.outputStream.write(input.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        }
    }

    fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_Connected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSucess: Boolean = true
        private val context: Context
        init {
            this.context = c
        }
        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Соединение", "Пожалуйста, подождите")
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                if (m_bluetoothSocket == null || m_Connected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(MacAddress)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSucess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSucess) {
                Log.i("data", "Не могу соедениться")
            } else {
                m_Connected = true
            }
            m_progress.dismiss()
        }


    }


}








