package com.example.moviefinder.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefinder.data.models.MovieDetail
import com.example.moviefinder.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movie = MutableLiveData<MovieDetail>()
    val movie: LiveData<MovieDetail> = _movie

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.getMovieDetails(movieId)
                    .fold(
                        onSuccess = { movieDetail ->
                            _movie.value = movieDetail
                        },
                        onFailure = { exception ->
                            _error.value = exception.message
                        }
                    )
            } finally {
                _loading.value = false
            }
        }
    }
} 