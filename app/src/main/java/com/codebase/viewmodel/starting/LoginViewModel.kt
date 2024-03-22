package com.codebase.viewmodel.starting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebase.data.User
import com.codebase.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    private val firestore = FirebaseFirestore.getInstance()

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email:String , password:String){

        viewModelScope.launch { _login.emit(Resource.Loading()) }

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
               viewModelScope.launch {
                   it.user?.let {
                       _login.emit(Resource.Success(it))
                       getUser()
                   }
               }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }

            }
    }

    fun resetPassword(email:String){

        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Success(email))
                    }
                }.addOnFailureListener{
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Error(it.message.toString()))
                    }
                }
        }


    private fun getUser (){

        viewModelScope.launch { _user.emit(Resource.Loading()) }

        firestore.collection("user").document(firebaseAuth.uid!!)
            .addSnapshotListener { value, error ->
                if(error != null){
                    viewModelScope.launch { _user.emit(Resource.Error(error.message.toString())) }
                } else{
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch { _user.emit(Resource.Success(user)) }
                    }

                }
            }
    }


}