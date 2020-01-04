package com.example.tdn2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_authentification.*

class AuthenticationFragment : Fragment(R.layout.fragment_authentification) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authentification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connexion.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_loginFragment)
        }

        signup.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_signupFragment)
        }
    }
}