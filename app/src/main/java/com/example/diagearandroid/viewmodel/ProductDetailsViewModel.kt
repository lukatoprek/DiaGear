package com.example.diagearandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: FirestoreProductRepository) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    private val _isLoading = MutableStateFlow(true)

    val product: StateFlow<Product?> = _product
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProduct(id: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                _product.update { repository.getProductById(id) }
            } catch (_: Exception) {
            } finally {
                _isLoading.update { false }
            }
        }
    }

    companion object {
        fun provideFactory(repository: FirestoreProductRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProductDetailsViewModel(repository) as T
            }
    }
}
