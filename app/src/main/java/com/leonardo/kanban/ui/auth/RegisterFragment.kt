package com.example.task.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.task.R
import com.example.task.databinding.FragmentRegisterBinding
import com.example.task.util.initToolbar
import com.example.task.util.showBottomSheet
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private fun registerUser(email: String, password: String) {
        try {
            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message ?: "Erro ao criar conta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        } catch (e: Exception) {
            binding.progressBar.isVisible = false
            Toast.makeText(
                requireContext(),
                e.message ?: "Ocorreu um erro",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()
    }

    private fun initListener() {
        binding.registrar.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.email.text.toString().trim()
        val senha = binding.senha.text.toString().trim()

        if (email.isNotBlank()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (senha.isNotBlank()) {
                    binding.progressBar.isVisible = true
                    registerUser(email, senha)
                } else {
                    showBottomSheet(
                        message = getString(
                            R.string.password_empty_register_fragment
                        )
                    )
                }
            } else {
                showBottomSheet(message = "Digite um e-mail válido.")
            }
        } else {
            binding.progressBar.isVisible = false
            showBottomSheet(
                message = getString(
                    R.string.email_empty_register_fragment
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}