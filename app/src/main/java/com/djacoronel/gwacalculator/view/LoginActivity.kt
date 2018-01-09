package com.djacoronel.gwacalculator.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.djacoronel.gwacalculator.R
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupAds()
        fetch_button.setOnClickListener { onFetchButtonPressed() }
        loadPrefs()
    }

    private fun loadPrefs() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val rememberMe = sharedPref.getBoolean("rememberMe", false)
        if (rememberMe) {
            studNo.setText(sharedPref.getString("studNo", ""))
            password.setText(sharedPref.getString("password", ""))
            remember_me.isChecked = rememberMe
        }
    }

    private fun onFetchButtonPressed() {
        if (studNo.text.toString().length == 10 && password.text.isNotEmpty()) {
            val returnIntent = Intent()
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            if (remember_me.isChecked) {
                editor.putBoolean("rememberMe", remember_me.isChecked)
                editor.putString("studNo", studNo.text.toString())
                editor.putString("password", password.text.toString())
                editor.apply()
            } else {
                editor.putBoolean("rememberMe", remember_me.isChecked)
                editor.putString("studNo", "")
                editor.putString("password", "")
                editor.apply()
            }

            returnIntent.putExtra("studNo", studNo.text.toString())
            returnIntent.putExtra("password", password.text.toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            toast("Invalid student number or password")
        }
    }

    private fun setupAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice("CEA54CA528FB019B75536189748EAF7E")
                .addTestDevice("4CCC112819318A806ADC4807B6A0C444")
                .build()
        login_adView.loadAd(adRequest)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    public override fun onPause() {
        super.onPause()
        login_adView.pause()
    }

    public override fun onResume() {
        super.onResume()
        login_adView.resume()

    }

    public override fun onDestroy() {
        super.onDestroy()
        login_adView.destroy()
    }
}


