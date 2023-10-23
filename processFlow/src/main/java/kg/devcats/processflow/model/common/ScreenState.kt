package kg.devcats.processflow.model.common

import com.google.gson.annotations.SerializedName
import kg.devcats.processflow.model.common.StateScreenStatus

data class ScreenState(
    val status: StateScreenStatus? = null,
    @SerializedName("app_bar_text")
    val appBarText: String? = null,
    val title: String? = null,
    val description: String? = null)
