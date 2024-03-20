package com.codebase.activities

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.codebase.aroom.R
import com.codebase.aroom.databinding.ActivityShoppingBinding
import com.codebase.util.Resource
import com.codebase.viewmodel.shopping.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shopping_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Success -> {
                        val count = it.data?.size?: 0
                        binding.apply {
                            bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                number = count
                                backgroundColor = resources.getColor(R.color.g_blue)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination?, arguments: Bundle? ->

            if (destination?.id == R.id.addProductFragment) {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }
}