package kg.devcats.processflow.model

import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option


open class Event {

    class Notification(val message: String) : Event()
    class NotificationResId(val messageResId: Int) : Event()

    //Flow
    class ProcessFlowIsExist(val isExist: Boolean): Event()


    //InputForm
    class AdditionalOptionsFetched(val formId: String, val options: List<Option>): Event()

}