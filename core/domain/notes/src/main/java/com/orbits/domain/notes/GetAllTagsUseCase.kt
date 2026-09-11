package com.orbits.domain.notes

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to get all tags used by the user.
 */
class GetAllTagsUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @return Result containing a list of all tags, or error
     */
    suspend operator fun invoke(): Result<List<String>> {
        return notesRepository.getAllTags()
    }

    /**
     * Get all tags with their usage counts.
     * This requires a separate call to get notes and count tags.
     */
    suspend fun withCounts(): Result<List<NoteTag>> {
        val tagsResult = notesRepository.getAllTags()
        if (tagsResult is Result.Error) {
            return Result.Error(tagsResult.exception)
        }

        val notes = notesRepository.getNotes().first()

        val tagCounts = notes
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()

        val noteTags = (tagsResult as Result.Success).data.map { tag ->
            NoteTag(tag, tagCounts[tag] ?: 0)
        }

        return Result.Success(noteTags.sortedByDescending { it.count })
    }
}
