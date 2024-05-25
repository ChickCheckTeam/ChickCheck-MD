package com.example.chickcheckapp.presentation.ui.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.chickcheckapp.databinding.FragmentAnalysisBinding
import com.example.chickcheckapp.presentation.ui.bottomfragment.BottomAnalysisDialogFragment

class AnalysisFragment : Fragment() {
    private var _binding : FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val args: AnalysisFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUri = args.uriImage
        val bottomAnalysisDialogFragment = BottomAnalysisDialogFragment.getInstance(imageUri)
        binding.ivImage.setImageURI(imageUri.toUri())
        binding.btnAnalysis.setOnClickListener {
            bottomAnalysisDialogFragment.show(parentFragmentManager, "BottomAnalysisDialogFragment")
        }
    }
}