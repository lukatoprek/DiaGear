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

sealed class AdminDialogState {
    data object None : AdminDialogState()
    data object Add : AdminDialogState()
    data class Edit(val product: Product) : AdminDialogState()
}

data class ProductForm(
    val productId: String = "",
    val manufacturer: String = "",
    val name: String = "",
    val detailedName: String = "",
    val category: String = "",
    val amount: String = "",
    val price: String = "0.0",
    val useWithin: String = "",
    val image: String = "",
    val details: String = ""
)

fun Product.toForm() = ProductForm(
    productId = productId,
    manufacturer = manufacturer,
    name = name,
    detailedName = detailedName,
    category = category,
    amount = amount,
    price = price.toString(),
    useWithin = useWithin,
    image = image,
    details = details
)

class AdminProductViewModel(private val repository: FirestoreProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _dialogState = MutableStateFlow<AdminDialogState>(AdminDialogState.None)
    private val _addForm = MutableStateFlow(ProductForm())
    private val _editForm = MutableStateFlow(ProductForm())

    val isLoading: StateFlow<Boolean> = _isLoading
    val searchQuery: StateFlow<String> = _searchQuery
    val selectedCategory: StateFlow<String?> = _selectedCategory
    val dialogState: StateFlow<AdminDialogState> = _dialogState
    val addForm: StateFlow<ProductForm> = _addForm
    val editForm: StateFlow<ProductForm> = _editForm

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

    fun setSearchQuery(query: String) { _searchQuery.update { query } }

    fun setCategory(category: String?) { _selectedCategory.update { category } }

    fun updateAddForm(form: ProductForm) { _addForm.update { form } }

    fun updateEditForm(form: ProductForm) { _editForm.update { form } }

    fun showAddDialog() {
        _dialogState.update { AdminDialogState.Add }
    }

    fun showEditDialog(product: Product) {
        _editForm.update { product.toForm() }
        _dialogState.update { AdminDialogState.Edit(product) }
    }

    fun dismissDialog() {
        _dialogState.update { AdminDialogState.None }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.addProduct(product)
                _addForm.update { ProductForm() }
                loadProducts()
                dismissDialog()
            } catch (_: Exception) {
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                loadProducts()
                dismissDialog()
            } catch (_: Exception) {
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
                loadProducts()
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        fun provideFactory(repository: FirestoreProductRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AdminProductViewModel(repository) as T
            }
    }
}
