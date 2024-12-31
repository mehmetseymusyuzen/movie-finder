package com.example.moviefinder.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviefinder.R
import com.example.moviefinder.data.models.Movie

class MoviesAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImageView: ImageView = itemView.findViewById(R.id.posterImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val releaseDateTextView: TextView = itemView.findViewById(R.id.releaseDateTextView)
        private val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(getItem(position))
                }
            }

            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = getItem(position)
                    movie.isFavorite = !movie.isFavorite
                    onFavoriteClick(movie)
                    updateFavoriteIcon(movie.isFavorite)
                }
            }
        }

        fun bind(movie: Movie) {
            titleTextView.text = movie.title
            releaseDateTextView.text = itemView.context.getString(
                R.string.release_date_format,
                movie.releaseDate
            )
            ratingTextView.text = itemView.context.getString(
                R.string.rating_format,
                movie.voteAverage
            )

            updateFavoriteIcon(movie.isFavorite)

            movie.posterPath?.let { posterPath ->
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w500$posterPath")
                    .into(posterImageView)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
} 