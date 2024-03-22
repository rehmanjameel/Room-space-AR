package com.codebase.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codebase.ARoomApplication
import com.codebase.adapters.HomeViewPagerAdapter
import com.codebase.aroom.R
import com.codebase.aroom.databinding.FragmentHomeBinding
import com.codebase.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment :Fragment(R.layout.fragment_home) {

    private lateinit var binding:FragmentHomeBinding
//    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }
    val aRoomApplication = ARoomApplication()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
//            ChairFragment(),
//            CupboardFragment(),
//            TableFragment(),
//            SofaFragment(),
//            BedFragment()
        )

        Log.e("user role ion home", aRoomApplication.getValueString("role").toString())
        if (aRoomApplication.getValueString("role").equals("admin")) {
            binding.addProducts.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
            }
        } else {
            binding.addProducts.visibility = View.GONE
        }

        binding.viewPagerHome.isUserInputEnabled = false

        val viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){ tab,position ->
            when(position){
                0 -> tab.text = "Home"
//                1 -> tab.text = "Chair"
//                2 -> tab.text = "Cupboard"
//                3 -> tab.text = "Table"
//                4 -> tab.text = "Sofa"
//                5 -> tab.text = "Bed"
            }
        }.attach()


    }
}
