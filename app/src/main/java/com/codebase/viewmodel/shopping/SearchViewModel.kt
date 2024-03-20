package com.codebase.viewmodel.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebase.data.Product
import com.codebase.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()


    fun searchProduct(query: String) {
        viewModelScope.launch { _search.emit(Resource.Loading()) }

       // val searchTerms = query.toLowerCase().split("\\s+".toRegex())

            firestore.collection("Products").orderBy("name").whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    viewModelScope.launch { _search.emit(Resource.Success(products)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _search.emit(Resource.Error(it.message.toString())) }
                }


    }
}