package kg.devcats.processflow.extension

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kg.devcats.processflow.R
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.base.process.ProcessFlowHolder
import kg.devcats.processflow.ui.camera.PhotoFlowFragment
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Context.getProcessFlowHolder(): ProcessFlowHolder {
    return this as ProcessFlowHolder
}

fun Fragment.getProcessFlowHolder(): ProcessFlowHolder {
    return  requireContext().getProcessFlowHolder()
}

fun Context.getThemeColor(colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return typedValue.data
}

fun <R> Single<R>.defaultSubscribe(
    onSuccess: (R) -> Unit = {},
    onError: (Throwable) -> Unit = {}
): Disposable {
    return this.subscribe(onSuccess, onError)
}

fun View.setMargins(@DimenRes leftMargin: Int, @DimenRes topMargin: Int, @DimenRes rightMargin: Int, @DimenRes bottomMargin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    val marginLeftDp = resources.getDimension(leftMargin).toInt()
    val marginTopDp = resources.getDimension(topMargin).toInt()
    val marginRightDp = resources.getDimension(rightMargin).toInt()
    val marginBottomDp = resources.getDimension(bottomMargin).toInt()
    params.setMargins(marginLeftDp, marginTopDp, marginRightDp, marginBottomDp)
    layoutParams = params
}

fun Context.hideKeyboard() {
    val view = (this as? Activity)?.currentFocus
    if (view != null) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

inline fun <reified T : Activity> Context.startActivity(noinline extra: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
}

fun Context.showWarningDialog(content: CharSequence, onOkListener: () -> Unit = { }): Dialog {
    val builder = AlertDialog.Builder(this, R.style.AppAlertDialog)
    builder.setMessage(content)
        .setCancelable(false)
        .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            onOkListener()
        }
    val dialog = builder.create()
    dialog.show()
    return dialog
}

fun Activity.openApplicationSettings() {
    this.startActivityForResult(Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:" + packageName)), 9000)
}

val Long.toTimeFromMillis: String
    get() {
        val hours = TimeUnit.MILLISECONDS.toHours(this)
        val second = this / 1000 % 60
        val minute = this / (1000 * 60) % 60
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minute, second)
        else String.format("%02d:%02d", minute, second)
    }


fun ImageView.loadImage(imageUrl: String, @DrawableRes placeholderResId: Int? = null) {
    val requestOptions = object : RequestOptions() {}
    placeholderResId?.let {
        requestOptions.error(ContextCompat.getDrawable(this.context, placeholderResId))
    }

    Glide.with(this.context)
        .load(imageUrl)
        .apply(requestOptions)
        .into(this)

}



fun FragmentActivity.showDialog(themeResId: Int = R.style.AppAlertDialog, builderFunction: android.app.AlertDialog.Builder.() -> Any): android.app.AlertDialog? {
    return showDialog(lifecycle, themeResId, builderFunction)
}

fun Fragment.showDialog(themeResId: Int = R.style.AppAlertDialog, builderFunction: android.app.AlertDialog.Builder.() -> Any): android.app.AlertDialog? {
    return context?.showDialog(lifecycle, themeResId, builderFunction)
}

private fun Context.showDialog(lifecycle: Lifecycle, themeResId: Int, builderFunction: android.app.AlertDialog.Builder.() -> Any): android.app.AlertDialog {
    val builder = android.app.AlertDialog.Builder(this, themeResId).apply {
        setCancelable(false)
    }
    builder.builderFunction()
    val dialog = builder.create()
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            dialog.dismiss()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            dialog.dismiss()
        }
    })
    dialog.show()
    return dialog
}

fun android.app.AlertDialog.Builder.positiveButton(@StringRes btnTextId: Int, handleClick: () -> Unit = {}) {
    this.setPositiveButton(context.getString(btnTextId).uppercase(Locale.getDefault())) { dialogInterface, _ ->
        handleClick()
        dialogInterface.dismiss()
    }
}

fun android.app.AlertDialog.Builder.negativeButton(@StringRes btnTextId: Int, handleClick: () -> Unit = {}) {
    this.setNegativeButton(context.getString(btnTextId).uppercase(Locale.getDefault())) { dialogInterface, _ ->
        handleClick()
        dialogInterface.dismiss()
    }
}


fun TextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
    text = SpannableStringBuilder.valueOf(text).apply {
        getSpans(0, length, URLSpan::class.java).forEach {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url)
                    }
                },
                getSpanStart(it),
                getSpanEnd(it),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            removeSpan(it)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

