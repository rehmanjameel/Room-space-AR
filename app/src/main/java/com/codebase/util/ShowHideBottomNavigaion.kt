package com.codebase.util

import androidx.fragment.app.Fragment
import com.codebase.activities.ShoppingActivity
import com.codebase.aroom.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.codebase.aroom.R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.GONE
}
fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.codebase.aroom.R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE
}