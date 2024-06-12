package com.example.chickcheckapp.presentation.ui.article

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chickcheckapp.databinding.FragmentArticleBinding
import com.example.chickcheckapp.presentation.adapter.ArticleListAdapter
import com.example.chickcheckapp.presentation.ui.result.ResultFragment
import com.example.chickcheckapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.getSession().flowWithLifecycle(lifecycle).collect { user ->
                viewModel.getArticles(user.token).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE

                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            showToast(result.error)
                            Log.d(ResultFragment.TAG, "error: ${result.error}")
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = result.data
                            val articleAdapter = ArticleListAdapter(data)
                            binding.rvArticle.adapter = articleAdapter

                        }
                    }
                }
            }
        }
        binding.rvArticle.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}