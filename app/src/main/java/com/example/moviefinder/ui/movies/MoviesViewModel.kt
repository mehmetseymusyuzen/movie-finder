package com.example.moviefinder.ui.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefinder.data.models.Movie
import com.example.moviefinder.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private var currentPage = 1
    private var isLastPage = false
    private var currentQuery = ""
    private val favoriteMovies = mutableSetOf<Int>()

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            try {
                repository.getPopularMovies(currentPage).fold(
                    onSuccess = { response ->
                        val updatedMovies = response.results.map { movie ->
                            movie.copy(isFavorite = favoriteMovies.contains(movie.id))
                        }
                        _movies.value = updatedMovies
                        isLastPage = currentPage >= response.totalPages
                        currentPage++
                    },
                    onFailure = {
                        // Handle error
                    }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun searchMovies(query: String) {
        currentPage = 1
        currentQuery = query
        viewModelScope.launch {
            try {
                repository.searchMovies(query, currentPage).fold(
                    onSuccess = { response ->
                        val updatedMovies = response.results.map { movie ->
                            movie.copy(isFavorite = favoriteMovies.contains(movie.id))
                        }
                        _movies.value = updatedMovies
                        isLastPage = currentPage >= response.totalPages
                        currentPage++
                    },
                    onFailure = {
                        // Handle error
                    }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        if (movie.isFavorite) {
            favoriteMovies.add(movie.id)
        } else {
            favoriteMovies.remove(movie.id)
        }
        
        // Update the current list to reflect changes
        _movies.value = _movies.value?.map { 
            if (it.id == movie.id) it.copy(isFavorite = movie.isFavorite) else it 
        }
    }
} 