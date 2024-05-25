package com.example.chickcheckapp.presentation.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.chickcheckapp.databinding.FragmentResultBinding
import com.example.chickcheckapp.presentation.ui.analysis.AnalysisFragmentArgs

class ResultFragment : Fragment() {
    private var _binding : FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val args: ResultFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = args.uriImage
        binding.imageView.setImageURI(uri.toUri())
    }

}