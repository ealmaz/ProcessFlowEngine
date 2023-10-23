package kg.devcats.processflow.extension

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import kg.devcats.processflow.main.ProcessFlowHolderActivity

fun Context.getProcessFlowHolder(): ProcessFlowHolderActivity {
    return this as ProcessFlowHolderActivity
}

fun Fragment.getProcessFlowHolder(): ProcessFlowHolderActivity {
    return  requireContext().getProcessFlowHolder()
}

fun Context.getThemeColor(colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return typedValue.data
}