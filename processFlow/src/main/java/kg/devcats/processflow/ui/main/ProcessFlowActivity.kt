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
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.ScreenKey.INPUT_FIELD
import kg.devcats.processflow.model.ScreenKey.INPUT_FORM
import kg.devcats.processflow.model.ScreenKey.INPUT_OTP
import kg.devcats.processflow.model.ScreenKey.PASSPORT_BACK_PHOTO
import kg.devcats.processflow.model.ScreenKey.PASSPORT_FRONT_PHOTO
import kg.devcats.processflow.model.ScreenKey.SELFIE_PHOTO
import kg.devcats.processflow.model.ScreenKey.STATUS_INFO
import kg.devcats.processflow.model.ScreenKey.VIDEO_CALL
import kg.devcats.processflow.model.ScreenKey.VIDEO_CALL_PROMO
import kg.devcats.processflow.model.ScreenKey.WEB_VIEW
import kg.devcats.processflow.model.WebViewIds.WEB_VIEW_VIDEO_IDENT
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.model.component.FlowRetryInfo
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.model.component.WebViewFileTypes
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

abstract class ProcessFlowActivity<VM: ProcessFlowVM<*>> : AppCompatActivity(), ProcessFlowHolder {

    protected var retryDelayMills = 1000L
    protected var retryRequestCounter = 0
    protected var isNeedToExecuteRetry = false

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
        val isBackPressHandled = (currentScreen as? BaseProcessScreenFragment<*>)?.handleBackPress() ?: BackPressHandleState.NOT_HANDLE
        if (isBackPressHandled == BackPressHandleState.HANDLED) return
        val isProcessTerminated = vm.isProcessTerminated()
        if (isBackPressHandled == BackPressHandleState.CALL_SUPER || isProcessTerminated) super.onBackPressed()
        else showExitDialog()
    }

    open fun setupViews() {
        setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
    }

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

    override fun setToolbarTitle(title: String) { vb.chiliToolbar.setTitle(title) }
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
        if (isNeedToExecuteRetry) return
        if (currentScreen?.handleShowLoading(false) != true)
            loader.dismiss()
    }

    open fun showExitDialog() {
        showDialog {
            setMessage(R.string.process_flow_warning_exit)
            positiveButton(android.R.string.ok) { cancelChatAndClose() }
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
            PASSPORT_FRONT_PHOTO -> openCameraFlow(CameraType.FRONT_PASSPORT, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            PASSPORT_BACK_PHOTO -> openCameraFlow(CameraType.BACK_PASSPORT_WITH_RECOGNIZER, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            SELFIE_PHOTO -> openCameraFlow(CameraType.SELFIE, data.allowedAnswer?.filterIsInstance<FlowButton>()?.first()?.buttonId ?: "")
            INPUT_FORM -> openInputForm(data)
        }
    }


    open fun resolveNewEvent(event: Event) {
        when (event) {
            is Event.Notification -> showErrorDialog(event.message)
            is Event.NotificationResId -> showErrorDialog(getString(event.messageResId))
            is Event.ProcessFlowIsExist -> {
                if (!event.isExist) handleStartProcessFlow()
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
            is ProcessFlowCommit.OnButtonClick -> resolveButtonClickCommit(commit.buttonsInfo, commit.additionalContent)
            is ProcessFlowCommit.OnFlowPhotoCaptured -> uploadPhotos(commit)
            is ProcessFlowCommit.CommitContentFormResponseId -> vm.commit(commit.responseId, commit.content)
            is ProcessFlowCommit.FetchAdditionalOptionsForDropDown -> vm.fetchOptions(commit.formId, commit.parentSelectedOptionId)
            is ProcessFlowCommit.OnLinkClicked -> openWebViewFromUrl(commit.link)
            else -> {}
        }
    }

    open fun handleInitCommit() {
        vm.restoreActiveFlow(processType)
    }

    open fun handleStartProcessFlow() {
        vm.startProcessFlow(getProcessFlowStartParams())
    }

    open fun getProcessFlowStartParams(): Map<String, Any> = mapOf()

    protected fun uploadPhotos(commit: ProcessFlowCommit.OnFlowPhotoCaptured) {
        val file = File(commit.filePath)
        vm.upload(commit.responseId, file, commit.fileType, commit.mrz, {}, {_, _ ->
            showErrorDialog(getString(R.string.process_flow_unexpected_error))
        })
    }

    open fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        if (button == null) {
            vm.getState()
            return
        }
        vm.commit(button.buttonId, additionalContent)
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
        (currentScreen as? BaseProcessScreenFragment<*>)?.setScreenData(data)
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
            retryRequestCounter = 0
            hideLoading()
            (currentScreen as? BaseProcessScreenFragment<*>)?.onHandleRetry(null)
        }
    }

    protected fun fetchScreenStateAfter(millis: Long, showLoader: Boolean) {
        if (!showLoader) hideLoading()
        vb.flContainer.postDelayed({
            if (isNeedToExecuteRetry) vm.getState(showLoader)
        }, millis)
    }

    open fun openWebView(data: ProcessFlowScreenData) {
        val webView = data.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()
        when {
            webView?.id == WEB_VIEW_VIDEO_IDENT -> navigateTo(VideoCallWebViewFragment::class.java, checkPrevFragment = false)
            webView?.properties?.fileType == WebViewFileTypes.PDF -> navigateTo(ProcessFlowPdfWebViewFragment::class.java, checkPrevFragment = false)
            else -> navigateTo(ProcessFlowWebViewFragment::class.java)
        }
        setScreenData(currentScreen as Fragment, data)
    }

    open fun openWebViewFromUrl(url: String) {
        if (url.endsWith(".pdf")) navigateTo(ProcessFlowPdfWebViewFragment::class.java, checkPrevFragment = false, addToBackStack = true) {
            ProcessFlowPdfWebViewFragment.create(true)
        }
        else navigateTo(ProcessFlowLinksWebView::class.java, addToBackStack = true)
        setScreenData(currentScreen as Fragment, ProcessFlowScreenData(screenKey = WEB_VIEW, allowedAnswer = listOf(FlowWebView(id = "OPEN_LINK", url = url))))
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

    open fun closeCurrentFlowActivity() {
        finish()
    }
}
