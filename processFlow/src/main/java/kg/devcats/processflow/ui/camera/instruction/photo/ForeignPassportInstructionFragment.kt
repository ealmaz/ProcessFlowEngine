package kg.devcats.processflow.ui.camera.instruction.photo

import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.R
import kg.devcats.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class ForeignPassportInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() = R.string.process_flow_photo_instruction_passport_foreigner
    override fun getInstructionSubtitleRes(): Int = R.string.process_flow_photo_instruction_passport_foreigner_subtitle
    override fun getInstructionImageRes() = -1
    override fun getInstructionImageUrl() = ProcessFlowConfigurator.foreignPassportInstructionUrlResolver.invoke()
}