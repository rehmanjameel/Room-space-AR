package com.codebase.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codebase.ARoomApplication
import com.codebase.aroom.R
import com.codebase.aroom.databinding.FragmentRegisterBinding
import com.codebase.data.User
import com.codebase.util.RegisterValidation
import com.codebase.util.Resource
import com.codebase.viewmodel.starting.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    private var userRole = ""
    private val aRoomApplication = ARoomApplication()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            tvDoYouHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            Log.e("role", "admin123")

            if (aRoomApplication.getValueString("role").equals("admin")) {
                userRole = "admin"
                Log.e("role", "admin")
            } else if (aRoomApplication.getValueString("role").equals("user")) {
                userRole = "user"
                Log.e("role", "user")
            }
            btnRegisterRegister.setOnClickListener {
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim(),
                    userRole
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.btnRegisterRegister.revertAnimation()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                        findNavController().popBackStack()
                        Toast.makeText(requireContext(),"Registered successfully!",Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

            }
        }


    }

}