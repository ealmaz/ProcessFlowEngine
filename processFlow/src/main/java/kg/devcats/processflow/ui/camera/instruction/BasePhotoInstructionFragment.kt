package kg.devcats.processflow.ui.camera.instruction

import android.os.Bundle
import android.view.View
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.devcats.processflow.base.BaseFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentPhotoInstructionBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.ui.camera.PhotoFlowFragment

abstract class BasePhotoInstructionFragment : BaseFragment<ProcessFlowFragmentPhotoInstructionBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhotoInstructionView()
        vb.btnAction.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow()
        }
        getProcessFlowHolder().setToolbarTitle("")
    }

    override fun inflateViewBinging() =
        ProcessFlowFragmentPhotoInstructionBinding.inflate(layoutInflater)

    abstract fun getInstructionTitleRes(): Int
    abstract fun getInstructionImageRes(): Int

    private fun setupPhotoInstructionView() {
        with(vb) {
            tvTitle.text = getString(getInstructionTitleRes())
            ivCorrect.setImageResource(getInstructionImageRes())
        }
    }
}