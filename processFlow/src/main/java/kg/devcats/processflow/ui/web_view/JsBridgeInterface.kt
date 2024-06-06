package kg.devcats.processflow.ui.web_view

import android.webkit.JavascriptInterface

interface JsBridgeInterface {

    @JavascriptInterface
    fun setStringResultAndClose(result: String)

    @JavascriptInterface
    fun isThemeLight(): String

    @JavascriptInterface
    fun getLocale(): String
}