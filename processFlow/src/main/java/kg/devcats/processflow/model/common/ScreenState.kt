package kg.devcats.processflow.model.common

import com.google.gson.annotations.SerializedName

data class ScreenState(
    val status: StateScreenStatus? = null,
    val statusImageUrl: String? = null,
    val animationUrl: String? = null,
    @SerializedName("app_bar_text")
    val appBarText: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isDescriptionHtml: Boolean? = null,
    val bottomDescriptionHtml: String? = null,
    val timer: Long? = null,
    val timerText: String? = null,
)
