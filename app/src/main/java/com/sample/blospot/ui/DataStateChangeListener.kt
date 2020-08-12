package com.sample.blospot.ui

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()
}