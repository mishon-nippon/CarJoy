package com.example.micai.carjoy

import android.app.Activity
import android.os.Bundle


class SettingsActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()

    }
}