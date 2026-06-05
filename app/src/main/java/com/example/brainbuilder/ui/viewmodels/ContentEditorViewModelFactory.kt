package com.example.brainbuilder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brainbuilder.data.remote.repository.ContentEditorRepository

class ContentEditorViewModelFactory(
    private val contentEditorRepository: ContentEditorRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentEditorViewModel(contentEditorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
