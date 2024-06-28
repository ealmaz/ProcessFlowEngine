package kg.devcats.processflow.ui.camera.instruction.photo

import android.os.Bundle
import android.view.View
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentPhotoSelfeInstrustionBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.loadImage
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.ui.camera.PhotoFlowFragment

class SelfiePhotoInstructionFragment : BaseFragment<ProcessFlowFragmentPhotoSelfeInstrustionBinding>() {

    override fun inflateViewBinging() = ProcessFlowFragmentPhotoSelfeInstrustionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhotoInstructionView()
        setupActionButton()
        getProcessFlowHolder().setToolbarTitle("")
    }

    private fun setupActionButton() {
        vb.btnAction.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow()
        }
    }

    private fun setupPhotoInstructionView() {
        with(vb) {
            tvTitle.text = getString(R.string.process_flow_photo_instruction_selfie)
            tvSubtitle.setText(R.string.process_flow_photo_instruction_selfie_description)
            ivCorrect.loadImage(ProcessFlowConfigurator.selfieInstructionUrlResolver.invoke())
        }
    }
}
