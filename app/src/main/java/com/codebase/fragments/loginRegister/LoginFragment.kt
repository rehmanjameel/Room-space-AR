package com.codebase.fragments.loginRegister

import android.content.Intent
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
import com.codebase.activities.ShoppingActivity
import com.codebase.util.Resource
import com.codebase.aroom.R
import com.codebase.aroom.databinding.FragmentLoginBinding
import com.codebase.dialog.setupBottomSheetDialog
import com.codebase.viewmodel.starting.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            tvForgotPasswordLogin.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.resetPassword.collect{
                    when(it){
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            Snackbar.make(requireView(),"Reset link has sent to your email",Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(),"Error: ${it.message}",Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }

            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnLoginLogin.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email,password)
        }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLoginLogin.revertAnimation()

                        getDetails()
                        Intent(requireActivity(),ShoppingActivity::class.java).also {intent->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        binding.btnLoginLogin.revertAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }

            }


        }



    }

    private fun getDetails() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){

                    is Resource.Success ->{
                        val user = it.data
                        val aRoomApplication = ARoomApplication()
                        aRoomApplication.saveString("role", user!!.userRole)
                        Log.e("user role ion home rolee", user.userRole)

                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }
    }

}