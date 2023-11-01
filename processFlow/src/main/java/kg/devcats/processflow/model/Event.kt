package kg.devcats.processflow.model

import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option


open class Event {

    class Notification(val message: String) : Event()
    class NotificationResId(val messageResId: Int) : Event()
    class DialogNotification(val message: String) : Event()

    //Flow
    class ProcessFlowIsExist(val isExist: Boolean): Event()


    //InputForm
    object AdditionalOptionsFetching : Event()
    class AdditionalOptionsFetched(val formId: String, val options: List<Option>): Event()

}