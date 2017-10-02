package com.djacoronel.gwacalculator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fetch_button.setOnClickListener { onFetchButtonPressed() }
    }

    private fun onFetchButtonPressed() {
        if (studNo.text.toString().length == 10 && password.text.isNotEmpty()) {
            val returnIntent = Intent()
            returnIntent.putExtra("studNo", studNo.text.toString())
            returnIntent.putExtra("password", password.text.toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            toast("Invalid student number or password")
        }
    }


    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }
}


