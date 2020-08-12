package com.sample.blospot.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.sample.blospot.R
import com.sample.blospot.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseBlogFragment : DaggerFragment() {

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }

    fun setupActionBarWithNavController(fragmentID: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentID))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }
}