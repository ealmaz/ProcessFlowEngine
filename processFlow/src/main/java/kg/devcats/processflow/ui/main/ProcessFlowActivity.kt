package kg.devcats.processflow.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.base.process.ProcessFlowHolder
import kg.devcats.processflow.base.process.ProcessFlowScreen
import kg.devcats.processflow.databinding.ProcessFlowActivityProcessFlowBinding
import kg.devcats.processflow.extension.negativeButton
import kg.devcats.processflow.extension.positiveButton
import kg.devcats.processflow.extension.showDialog
import kg.devcats.processflow.model.AppActionUrlConstants.ACTION_TYPE_BUTTON_CLICK
import kg.devcats.processflow.model.AppActionUrlConstants.APP_ACTION_URL_TYPE
import kg.devcats.processflow.model.AppActionUrlConstants.PARAM_NAME_ACTION
import kg.devcats.processflow.model.AppActionUrlConstants.PARAM_NAME_ADDITIONAL_DATA
import kg.devcats.processflow.model.AppActionUrlConstants.PARAM_NAME_ADDITIONAL_PARAM
import kg.devcats.processflow.model.ButtonIds.OPEN_SUB_PROCESS
import kg.devcats.processflow.model.ButtonIds.RETURN_TO_PARENT_PROCESS
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.ScreenKey.FOREIGN_PASSPORT_PHOTO
import kg.devcats.processflow.model.ScreenKey.INPUT_FIELD
import kg.devcats.processflow.model.ScreenKey.INPUT_FORM
import kg.devcats.processflow.model.ScreenKey.INPUT_OTP
import kg.devcats.processflow.model.ScreenKey.PASSPORT_BACK_PHOTO
import kg.devcats.processflow.model.ScreenKey.PASSPORT_FRONT_PHOTO
import kg.devcats.processflow.model.ScreenKey.SELFIE_PHOTO
import kg.devcats.processflow.model.ScreenKey.SIMPLE_CAMERA
import kg.devcats.processflow.model.ScreenKey.SIMPLE_SELFIE_PHOTO
import kg.devcats.processflow.model.ScreenKey.STATUS_INFO
import kg.devcats.processflow.model.ScreenKey.VIDEO_CALL
import kg.devcats.processflow.model.ScreenKey.VIDEO_CALL_PROMO
import kg.devcats.processflow.model.ScreenKey.WEB_VIEW
import kg.devcats.processflow.model.WebViewIds.WEB_VIEW_VIDEO_IDENT
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.ButtonProperties
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.model.component.FlowRetryInfo
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.model.component.WebViewFileTypes
import kg.devcats.processflow.model.component.WebViewProperties
import kg.devcats.processflow.ui.camera.CameraType
import kg.devcats.processflow.ui.camera.PhotoFlowFragment
import kg.devcats.processflow.ui.input_field.ProcessFlowInputFieldFragment
import kg.devcats.processflow.ui.input_form.InputFormFragment
import kg.devcats.processflow.ui.status.ProcessStatusInfoFragment
import kg.devcats.processflow.ui.status.VideoPromoStatusFragment
import kg.devcats.processflow.ui.web_view.ProcessFlowLinksWebView
import kg.devcats.processflow.ui.web_view.ProcessFlowPdfWebViewFragment
import kg.devcats.processflow.ui.web_view.ProcessFlowWebViewFragment
import kg.devcats.processflow.ui.web_view.VideoCallWebViewFragment
import java.io.File
import java.lang.Exception

abstract class ProcessFlowActivity<VM: ProcessFlowVM<*>> : AppCompatActivity(), ProcessFlowHolder {

    protected var retryDelayMills = 1000L
    protected var retryRequestCounter = 0
    protected var isNeedToExecuteRetry = false
    protected var isRetryLoaderInProgress = false

    protected val loader: AlertDialog by lazy {
        val alert = AlertDialog.Builder(this)
            .setView(LayoutInflater.from(this).inflate(R.layout.process_flow_progress_dialog, null))
            .setCancelable(false)
            .create()
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert
    }

    protected lateinit var vb: ProcessFlowActivityProcessFlowBinding
    abstract val vm: VM

    abstract val processType: String

    open val possibleProcessTypesToRestore: List<String> by lazy { listOf(processType) }

    protected val currentScreen: ProcessFlowScreen?
        get() = (supportFragmentManager.findFragmentById(R.id.fl_container)) as? ProcessFlowScreen


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ProcessFlowActivityProcessFlowBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupViews()
        observeLiveData()
        commit(ProcessFlowCommit.Initial)
    }

    override fun onBackPressed() {
        val backPressHandledState = currentScreen?.handleBackPress() ?: BackPressHandleState.NOT_HANDLE
        when (backPressHandledState) {
            BackPressHandleState.HANDLED -> return
            BackPressHandleState.CALL_SUPER -> super.onBackPressed()
            else -> {
                if (vm.isProcessTerminated()) super.onBackPressed()
                else showExitDialog()
            }
        }
    }

    open fun setupViews() {
        setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
    }


    open fun getAppLocale(): String = "ru"

    open fun isAppThemeLight(): Boolean = true

    open fun observeLiveData() = with (vm) {
        event.observe(this@ProcessFlowActivity) { resolveNewEvent(it) }
        processFlowScreenDataLive.observe(this@ProcessFlowActivity) { resolveNewScreenState(it); resolveNewScreenKey(it) }
        loaderState.observe(this@ProcessFlowActivity) { if (it) showLoading() else hideLoading() }
    }

    override fun setToolbarNavIcon(navIconRes: Int) {
        vb.chiliToolbar.initToolbar(
            ChiliToolbar.Configuration(
                hostActivity = this,
                navigationIconRes = navIconRes,
                isNavigateUpButtonEnabled = true,
            )
        )
    }

    override fun setIsToolbarVisible(isVisible: Boolean) {
        vb.chiliToolbar.isVisible = isVisible
    }

    override fun setIsNavigationUpEnabled(isEnabled: Boolean) {
        vb.chiliToolbar.isUpHomeEnabled(hostActivity = this, isEnabled = isEnabled)
    }

    override fun setIsActivityLoading(isLoading: Boolean) {
        vm.loaderState.postValue(isLoading)
    }

    override fun setToolbarTitle(title: String) { vb.chiliToolbar.setTitle(title) }

    override fun setToolbarTitleCentered(isCentered: Boolean) {
        vb.chiliToolbar.setIsTitleCentered(isCentered)
    }

    override fun setupToolbarEndIcon(iconRes: Int?, onClick: (() -> Unit)?): Unit = with(vb.chiliToolbar) {
        if (iconRes != null) {
            setEndIcon(iconRes)
            setEndIconClickListener { onClick?.invoke() }
        } else {
            setIconVisibility(false)
        }
    }

    override fun commit(commit: ProcessFlowCommit) {
        resolveNewCommit(commit)
    }


    open fun showLoading() {
        if (currentScreen?.handleShowLoading(true) != true)
            loader.show()
    }

    open fun hideLoading() {
        if (isRetryLoaderInProgress) return
        if (currentScreen?.handleShowLoading(false) != true)
            loader.dismiss()
    }

    open fun showExitDialog() {
        showDialog {
            setMessage(R.string.process_flow_warning_exit)
            positiveButton(R.string.process_flow_yes) { cancelChatAndClose() }
            negativeButton(R.string.process_flow_no)
            setCancelable(false)
        }

    }

    open fun showErrorDialog(message: String) {
        showDialog {
            setCancelable(false)
            setMessage(message)
            setPositiveButton(R.string.process_flow_clearly) { _, _ -> cancelChatAndClose() }
        }
    }

    open fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        showDialog {
            setMessage(message)
            setCancelable(true)
            positiveButton(R.string.process_flow_yes, handleClick = onConfirm)
            negativeButton(R.string.process_flow_no)
        }
    }

    open fun cancelChatAndClose() {
        showLoading()
        vm.cancelProcessFlow()
    }

    open fun resolveNewScreenKey(data: ProcessFlowScreenData) {
        when (data.screenKey) {
            STATUS_INFO -> openStatusScreen(data)
            VIDEO_CALL -> openWebView(data)
            WEB_VIEW -> openWebView(data)
            VIDEO_CALL_PROMO -> {
                navigateTo(VideoPromoStatusFragment::class.java)
                setScreenData(currentScreen as Fragment, data)
            }
            INPUT_OTP -> openInputField(data)
            INPUT_FIELD -> openInputField(data)
            FOREIGN_PASSPORT_PHOTO -> openCameraFlow(CameraType.FOREIGN_PASSPORT, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            PASSPORT_FRONT_PHOTO -> openCameraFlow(CameraType.FRONT_PASSPORT, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            PASSPORT_BACK_PHOTO -> openCameraFlow(CameraType.BACK_PASSPORT_WITH_RECOGNIZER, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            SELFIE_PHOTO -> openCameraFlow(CameraType.SELFIE, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            INPUT_FORM -> openInputForm(data)
            SIMPLE_CAMERA -> openCameraFlow(CameraType.SIMPLE_CAMERA, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            SIMPLE_SELFIE_PHOTO -> openCameraFlow(CameraType.SIMPLE_SELFIE_PHOTO, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            else -> {}
        }
    }


    open fun resolveNewEvent(event: Event) {
        when (event) {
            is Event.Notification -> showErrorDialog(event.message)
            is Event.NotificationResId -> showErrorDialog(getString(event.messageResId))
            is Event.ProcessFlowIsExist -> {
                when {
                    !(event.subProcessFlowType.isNullOrBlank()) && !(event.isExist) -> handleStartSubProcessFlow(event.subProcessFlowType)
                    !event.isExist ->  handleStartProcessFlow()
                }
            }
            is Event.AdditionalOptionsFetched -> {
                (currentScreen as? InputFormFragment)?.setAdditionalFetchedOptions(event.formId, event.options)
            }
            is Event.FlowCancelledCloseActivity -> closeCurrentFlowActivity()
            else -> {}
        }
    }

    open fun resolveNewCommit(commit: ProcessFlowCommit) {
        when (commit) {
            is ProcessFlowCommit.Initial -> handleInitCommit()
            is ProcessFlowCommit.OnButtonClick -> resolveButtonClickConfirmation(commit)
            is ProcessFlowCommit.OnFlowPhotoCaptured -> uploadPhotos(commit)
            is ProcessFlowCommit.CommitUploadMultipleFiles -> uploadMultipleFiles(commit)
            is ProcessFlowCommit.CommitContentFormResponseId -> vm.commit(commit.responseId, commit.content)
            is ProcessFlowCommit.FetchAdditionalOptionsForDropDown -> vm.fetchOptions(commit.formId, commit.parentSelectedOptionId)
            is ProcessFlowCommit.OnLinkClicked -> openWebViewFromUrl(commit.link)
            is ProcessFlowCommit.HandleEvent -> resolveNewEvent(commit.event)
            else -> {}
        }
    }

    open fun handleInitCommit() {
        vm.restoreActiveFlow(possibleProcessTypesToRestore)
    }

    open fun handleOpenSubProcess(subProcessFlowType: String) {
        vm.restoreActiveFlow(listOf(subProcessFlowType), newSubProcessType = subProcessFlowType, parentProcessId = vm.getCurrentProcessFlowId())
    }

    open fun handleStartProcessFlow() {
        vm.startProcessFlow(getProcessFlowStartParams())
    }

    open fun handleStartSubProcessFlow(subProcessFlowType: String) {
        vm.startProcessFlow(getSubProcessFlowStartParams(subProcessFlowType))
    }

    open fun getProcessFlowStartParams(): Map<String, Any> = mapOf(
        "process_type" to processType
    )

    open fun getSubProcessFlowStartParams(subProcessFlowType: String): Map<String, Any> = mapOf(
        "process_type" to subProcessFlowType,
        "parent_instance_key" to vm.requireProcessFlowId()
    )

    protected fun uploadPhotos(commit: ProcessFlowCommit.OnFlowPhotoCaptured) {
        val file = File(commit.filePath)
        vm.upload(commit.responseId, file, commit.fileType, commit.mrz, {}, {_, _ ->
            showErrorDialog(getString(R.string.process_flow_unexpected_error))
        })
    }

    protected fun uploadMultipleFiles(commit: ProcessFlowCommit.CommitUploadMultipleFiles) {
        vm.uploadFiles(commit.responseId, commit.files, ::getContentTypeForMultipleFileLoading)
    }

    protected open fun getContentTypeForMultipleFileLoading(uploadedType: String): String {
        return currentScreen?.handleMultipleFileLoaderContentType(uploadedType) ?: uploadedType
    }

    protected open fun resolveButtonClickConfirmation(commit: ProcessFlowCommit.OnButtonClick) {
        val confirmationText = commit.buttonsInfo.properties?.get(ButtonProperties.COMMIT_CONFIRMATION.propertyName)
        if (confirmationText == null) resolveButtonClickCommit(commit.buttonsInfo, commit.additionalContent)
        else showConfirmationDialog(message = confirmationText) { resolveButtonClickCommit(commit.buttonsInfo, commit.additionalContent) }
    }

    open fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        if (button == null) {
            vm.getState()
            return
        }
        when(button.buttonId) {
            OPEN_SUB_PROCESS -> with(vm) {
                button.properties?.get(ButtonProperties.SUB_PROCESS_FLOW_TYPE.propertyName)?.let {
                    handleOpenSubProcess(it)
                } ?: getState()
            }
            RETURN_TO_PARENT_PROCESS -> with(vm) {
                button.properties?.get(ButtonProperties.PARENT_PROCESS_ID.propertyName)?.let {
                    val childProcessFlowId = vm.getCurrentProcessFlowId() ?: ""
                    updateProcessFlowId(it)
                    commit(button.buttonId, listOf(
                        Content(childProcessFlowId, ContentTypes.CHILD_INSTANCE_KEY),
                    ))
                } ?: getState()
            }
            else -> vm.commit(button.buttonId, additionalContent)
        }
    }

    open fun resolveNewScreenState(screenData: ProcessFlowScreenData) {
        screenData.allowedAnswer?.filterIsInstance<FlowRetryInfo>()?.let { handleRetry(it) }
    }

    open fun navigateTo(
        fragmentClass: Class<out Fragment>,
        checkPrevFragment: Boolean = true,
        addToBackStack: Boolean = false,
        fragmentCreator: (fragmentClass: Class<out Fragment>) -> Fragment = { fragmentClass.newInstance() }
    ) {
        if (checkPrevFragment) {
            if ((currentScreen == null) || (currentScreen?.javaClass != fragmentClass)) {
                supportFragmentManager.commit {
                    if (addToBackStack) {
                        add(R.id.fl_container, fragmentCreator.invoke(fragmentClass))
                        addToBackStack(null)
                    }
                    else replace(R.id.fl_container, fragmentCreator.invoke(fragmentClass))
                }
            }
        } else {
            supportFragmentManager.commit {
                if (addToBackStack) {
                    add(R.id.fl_container, fragmentCreator.invoke(fragmentClass))
                    addToBackStack(null)
                }
                else replace(R.id.fl_container, fragmentCreator.invoke(fragmentClass))
            }
        }
        supportFragmentManager.executePendingTransactions()
    }

    open fun setScreenData(currentScreen: Fragment, data: ProcessFlowScreenData) {
        (currentScreen as? BaseProcessScreenFragment<*>)?.apply {
            setScreenData(data)
            setThemeAndLocale(isAppThemeLight(), getAppLocale())
        }
    }

    open fun openInputForm(data: ProcessFlowScreenData) {
        navigateTo(InputFormFragment::class.java)
        setScreenData(currentScreen as Fragment, data)
    }

    open fun handleRetry(retryInfo: List<FlowRetryInfo?>?) {
        val retry = retryInfo?.firstOrNull()
        if (retry != null) {
            retryRequestCounter += 1
            if (retryDelayMills < 8000 && retry.properties?.enableAt == null && retryRequestCounter > 5) {
                retryDelayMills *= 2
                retryRequestCounter = 0
            }
            isNeedToExecuteRetry = true
            fetchScreenStateAfter(
                millis = retry.getMillsOrNull() ?: retryDelayMills,
                showLoader = retry.properties?.showLoader ?: false
            )
            (currentScreen as? BaseProcessScreenFragment<*>)?.onHandleRetry(retry)
        } else {
            retryDelayMills = 1000L
            isNeedToExecuteRetry = false
            isRetryLoaderInProgress = false
            retryRequestCounter = 0
            hideLoading()
            (currentScreen as? BaseProcessScreenFragment<*>)?.onHandleRetry(null)
        }
    }

    protected fun fetchScreenStateAfter(millis: Long, showLoader: Boolean) {
        if (!showLoader) hideLoading()
        isRetryLoaderInProgress = showLoader
        vb.flContainer.postDelayed({
            if (isNeedToExecuteRetry) vm.getState(showLoader)
        }, millis)
    }

    open fun openWebView(data: ProcessFlowScreenData) {
        val webView = data.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()
        val fileType = webView?.properties?.fileType
        when {
            webView?.id == WEB_VIEW_VIDEO_IDENT -> navigateTo(VideoCallWebViewFragment::class.java, checkPrevFragment = false)
            fileType == WebViewFileTypes.PDF || fileType == WebViewFileTypes.BASE_64 -> navigateTo(ProcessFlowPdfWebViewFragment::class.java, checkPrevFragment = false)
            else -> navigateTo(ProcessFlowWebViewFragment::class.java, checkPrevFragment = false)
        }
        setScreenData(currentScreen as Fragment, data)
    }

    open fun openWebViewFromUrl(url: String) {
        when {
            url.startsWith(APP_ACTION_URL_TYPE) -> {
                handleCustomUrlActionClick(url)
                return
            }
            url.endsWith(".pdf") -> {
                navigateTo(ProcessFlowPdfWebViewFragment::class.java, checkPrevFragment = false, addToBackStack = true) { ProcessFlowPdfWebViewFragment.create(true) }
                setScreenData(currentScreen as Fragment, ProcessFlowScreenData(screenKey = WEB_VIEW, allowedAnswer = listOf(FlowWebView(id = "OPEN_LINK", url = url, properties = WebViewProperties(fileType = WebViewFileTypes.PDF)))))
            }
            else -> {
                navigateTo(ProcessFlowLinksWebView::class.java, addToBackStack = true)
                setScreenData(currentScreen as Fragment, ProcessFlowScreenData(screenKey = WEB_VIEW, allowedAnswer = listOf(FlowWebView(id = "OPEN_LINK", url = url))))
            }
        }
    }

    open fun openStatusScreen(data: ProcessFlowScreenData) {
        navigateTo(ProcessStatusInfoFragment::class.java)
        setScreenData(currentScreen as Fragment, data)
    }

    open fun openCameraFlow(cameraType: CameraType, responseId: String) {
        navigateTo(PhotoFlowFragment::class.java, false) {
            PhotoFlowFragment.create(cameraType, responseId)
        }
    }

    open fun openInputField(data: ProcessFlowScreenData) {
        navigateTo(ProcessFlowInputFieldFragment::class.java)
        setScreenData(currentScreen as Fragment, data)
    }

    //Example: "APP_ACTION?action=BUTTON_CLICK&param=OPEN_AGREEMENT_DOCUMETS\"
    open fun handleCustomUrlActionClick(url: String) {
        try {
            val pairs = url.subSequence(url.indexOf("?") + 1, url.length).split("&")
            val params = mutableMapOf<String, String>()
            pairs.forEach {
                params[it.substring(0, it.indexOf("="))] = it.substring(it.indexOf("=") + 1, it.length)
            }
            when(params[PARAM_NAME_ACTION]!!) {
                ACTION_TYPE_BUTTON_CLICK -> {
                    val buttonId = params[PARAM_NAME_ADDITIONAL_PARAM]!!
                    val buttonProperties = params[PARAM_NAME_ADDITIONAL_DATA]?.let {
                        mapOf(ButtonProperties.DATA.propertyName to it)
                    }
                    val button = FlowButton(buttonId = buttonId, properties = buttonProperties)
                    commit(ProcessFlowCommit.OnButtonClick(button))
                }
            }
        } catch (_: Exception) {}
    }

    open fun closeCurrentFlowActivity() {
        hideLoading()
        finish()
    }
}
