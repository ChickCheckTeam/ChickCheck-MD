package com.example.chickcheckapp.presentation.ui.bottomfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import com.example.chickcheckapp.databinding.FragmentBottomAnalysisBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomAnalysisDialogFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentBottomAnalysisBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getString("uri")
        if (uri != null) {
            binding.ivAnalyzedImage.setImageURI(uri.toUri())
            binding.btnViewDetail.setOnClickListener {
//                val action = AnalysisFragmentDirections.actionAnalysisFragmentToResultFragment(uri)
//                parentFragment?.view?.findNavController()?.navigate(action)
                dismiss()
            }
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
    companion object{
        const val TAG = "BottomAnalysisDialogFragment"
        fun getInstance(uri: String):BottomAnalysisDialogFragment{
            val fragment = BottomAnalysisDialogFragment()
            val bundle = Bundle()
            bundle.putString("uri", uri)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}