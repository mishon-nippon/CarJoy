package com.example.micai.carjoy

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(){

    private var m_bluetoothAdapter: BluetoothAdapter?=null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val MAC_ADRESS: String = "Device_adress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (m_bluetoothAdapter == null) {
            toast("Bluetooth не поддерживается")

        } else if (!m_bluetoothAdapter!!.isEnabled) {
            val btIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(btIntent, REQUEST_ENABLE_BLUETOOTH)
        }

    select_device_refresh.setOnClickListener{ pairedDeviceList()}


    }


    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                Log.i("device", "" + device)
            }
        } else {
            toast("Устройств не найдено")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlsActivity::class.java)
            intent.putExtra(MAC_ADRESS, address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (resultCode == Activity.RESULT_OK){
                if (m_bluetoothAdapter!!.isEnabled){
                    toast("Bluetooth включен")
                }else {
                    toast("Bluetooth выключен")
                }
            }else if (resultCode == Activity.RESULT_CANCELED){
                toast("Активация bluetooth была закрыта")
            }
        }
    }
}