package com.example.babel.domain.usecases

import com.example.babel.data.models.Book
import com.example.babel.data.models.BookRating
import com.example.babel.data.repository.RatingRepository
import com.example.babel.data.repository.UserLibraryRepository
import com.example.babel.data.repository.UserStatsRepository
import com.google.firebase.auth.FirebaseAuth

class RatingUseCase(
    private val ratingRepo: RatingRepository = RatingRepository(),
    private val statsRepo: UserStatsRepository = UserStatsRepository(),
    private val libraryRepo: UserLibraryRepository = UserLibraryRepository()
) {
    suspend fun submitRating(book: Book, stars: Int, comment: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val rating = BookRating(
            userId = userId,
            bookId = book.id,
            rating = stars,
            comment = comment
        )

        ratingRepo.addOrUpdateRating(rating)
        statsRepo.updateUserStats(userId, stars, book.genreId, book.authorIds)
        libraryRepo.markAsFinished(userId, book.id)
    }
}
