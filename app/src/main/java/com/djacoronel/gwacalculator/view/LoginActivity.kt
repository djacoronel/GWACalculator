package com.djacoronel.gwacalculator.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.djacoronel.gwacalculator.R
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupAds()

        webview.loadUrl("https://myuste.ust.edu.ph/student/")
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = LoginWebViewClient()
    }

    fun returnCookie(cookie: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("cookie", cookie)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    inner class LoginWebViewClient : WebViewClient() {
        var cookie = ""

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("studentcontrol")) {
                this@LoginActivity.returnCookie(cookie)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            cookie = CookieManager.getInstance().getCookie(url)
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



