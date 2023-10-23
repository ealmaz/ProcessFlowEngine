package kg.devcats.processflow.ui.camera.instruction.photo

import kg.devcats.processflow.R
import kg.devcats.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class PassportFrontInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() = R.string.process_flow_photo_instruction_passport_front
    override fun getInstructionImageRes() = R.drawable.process_flow_ic_instruction_passport_front
}
