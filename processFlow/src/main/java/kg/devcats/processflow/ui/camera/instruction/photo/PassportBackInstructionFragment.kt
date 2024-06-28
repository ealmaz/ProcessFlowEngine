package kg.devcats.processflow.ui.camera.instruction.photo

import kg.devcats.processflow.R
import kg.devcats.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class PassportBackInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() = R.string.process_flow_photo_instruction_passport_back
    override fun getInstructionSubtitleRes(): Int = R.string.process_flow_photo_instructiin_passports_subtitle
    override fun getInstructionImageRes() = R.drawable.process_flow_ic_instruction_passport_back

}
