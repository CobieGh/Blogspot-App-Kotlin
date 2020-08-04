package com.sample.blospot.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sample.blospot.R
import com.sample.blospot.util.ApiEmptyResponse
import com.sample.blospot.util.ApiErrorResponse
import com.sample.blospot.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFramgnet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}