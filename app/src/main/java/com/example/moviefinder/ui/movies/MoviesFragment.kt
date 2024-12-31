package com.example.moviefinder.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviefinder.databinding.FragmentMoviesBinding
import com.example.moviefinder.data.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MoviesViewModel by viewModels()
    private val moviesAdapter = MoviesAdapter(
        onMovieClick = { movie: Movie ->
            val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(movie.id)
            findNavController().navigate(action)
        },
        onFavoriteClick = { movie: Movie ->
            viewModel.toggleFavorite(movie)
        }
    )
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeMovies()
    }

    private fun setupRecyclerView() {
        binding.moviesRecyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(300L) // Add delay to avoid too many API calls
                editable?.toString()?.let { query ->
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query)
                    } else {
                        viewModel.getPopularMovies()
                    }
                }
            }
        }
    }

    private fun observeMovies() {
        viewModel.movies.observe(viewLifecycleOwner) { movies: List<Movie> ->
            moviesAdapter.submitList(movies)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}