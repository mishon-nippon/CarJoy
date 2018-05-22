package com.example.micai.carjoy

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import kotlinx.android.synthetic.main.controls_activity.*
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class ControlsActivity : AppCompatActivity() {
    companion object {
        lateinit var MacAddress: String
        var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")!!
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        lateinit var m_progress: ProgressDialog
        var m_bluetoothSocket: BluetoothSocket? = null
        var m_Connected: Boolean = false
    }

    private var motorLeft = 0
    private var motorRight = 0
    private var pwmMax: Int = 0
    private var commandLeft: String? = null
    private var commandRight: String? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.controls_activity)

        MacAddress = intent.getStringExtra(MainActivity.MAC_ADRESS)

        ConnectToDevice(this).execute()

        this.pwmMax = Integer.parseInt(resources.getText(R.string.default_pwmMax) as String)
        this.commandLeft = resources.getText(R.string.default_commandLeft) as String
        this.commandRight = resources.getText(R.string.default_commandRight) as String

        loadPref()

        button_for.setOnTouchListener({ _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                        motorLeft = -pwmMax
                        motorRight = -pwmMax
                        sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                    }

            if (event.action == MotionEvent.ACTION_UP){
                        motorLeft = 0
                        motorRight = 0
                        sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                    }
            false
                })



            button_back.setOnTouchListener({ _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    motorLeft = pwmMax
                    motorRight = pwmMax
                    sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                }

                if (event.action == MotionEvent.ACTION_UP){
                    motorLeft = 0
                    motorRight = 0
                    sendData (commandLeft + motorLeft + "\r" + commandRight + motorRight + "\r")
                }
                false
            })

                   button_left.setOnTouchListener({ _, event ->
                       if (event.action == MotionEvent.ACTION_DOWN) {
                           motorLeft = -pwmMax
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

                   button_right.setOnTouchListener({ _, event ->
                       if (event.action == MotionEvent.ACTION_DOWN) {
                           motorLeft = 0
                           motorRight = -pwmMax
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



    private fun sendData(input: String) {
        if (m_bluetoothSocket != null) { try {
            m_bluetoothSocket!!.outputStream.write(input.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        }
    }

    private fun disconnect() {
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

    @Suppress("DEPRECATION")
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        @SuppressLint("StaticFieldLeak")
        private val context: Context = c

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
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "Не могу соедениться")
            } else {
                m_Connected = true
            }
            m_progress.dismiss()
        }


    }

    override fun onPause() {
        super.onPause()
        disconnect()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadPref()
    }

    private fun loadPref(){
        val mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        pwmMax=Integer.parseInt(mySharedPreferences.getString("max_speed", pwmMax.toString()))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected (item: MenuItem): Boolean {
        val intent = Intent(this@ControlsActivity, SettingsActivity::class.java)
        startActivityForResult(intent, 0)
        return true
    }


}










