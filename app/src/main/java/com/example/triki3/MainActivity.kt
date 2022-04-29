package com.example.triki3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()
        startTimer()
    }

    private fun startTimer() {
        object : CountDownTimer(2000,1000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                val i = Intent(this@MainActivity,GameActivity::class.java)
                startActivity(i)
                finish()
            }
        }.start()
    }
}