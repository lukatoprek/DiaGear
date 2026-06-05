package com.example.diagearandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(private val repository: FirestoreProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = _isLoading
    val searchQuery: StateFlow<String> = _searchQuery
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val displayedProducts: StateFlow<List<Product>> = combine(
        _products, _searchQuery, _selectedCategory
    ) { products, query, category ->
        products.filter { p ->
            (category == null || p.category == category) &&
            (query.isBlank() ||
             p.name.contains(query, ignoreCase = true) ||
             p.productId.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                _products.update { repository.getProducts() }
            } catch (_: Exception) {
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun setCategory(category: String?) {
        _selectedCategory.update { category }
    }

    companion object {
        fun provideFactory(repository: FirestoreProductRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProductListViewModel(repository) as T
            }
    }
}
