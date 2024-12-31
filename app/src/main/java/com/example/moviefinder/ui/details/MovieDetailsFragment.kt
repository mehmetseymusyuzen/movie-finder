package com.example.moviefinder.ui.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.moviefinder.R
import com.example.moviefinder.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val args: MovieDetailsFragmentArgs by navArgs()
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieDetailsBinding.bind(view)

        observeViewModel()
        viewModel.loadMovieDetails(args.movieId)
    }

    private fun observeViewModel() {
        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            binding.apply {
                movieTitle.text = movie.title
                movieOverview.text = movie.overview
                movieRating.text = "Rating: ${movie.voteAverage}"
                movieReleaseDate.text = "Release Date: ${movie.releaseDate}"
                movieGenres.text = "Genres: ${movie.genres.joinToString { it.name }}"

                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                    .into(movieBackdrop)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 