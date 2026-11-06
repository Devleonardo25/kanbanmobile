package com.leonardo.task.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.leonardo.task.R
import com.leonardo.task.databinding.FragmentRecoverAccountBinding
import com.leonardo.task.util.initToolbar
import com.leonardo.task.util.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.isVisible



class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()
        auth = FirebaseAuth.getInstance()
    }

    private fun initListener(){
        binding.enviar.setOnClickListener {
            validateData()
        }
    }

    private fun validateData(){
        val email = binding.email.text.toString().trim()

        if (email.isNotBlank()){
            Toast.makeText(requireContext(), "Tudo OK!", Toast.LENGTH_SHORT).show()
        }else{
            showBottomSheet(message = getString(R.string.email_empty))
        }
    }
    private fun recoverAccountUser(email: String) {
        try {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        showBottomSheet(
                            message = getString(R.string.text_message_recover_account_fragment)
                        )
                    } else {
                        Toast.makeText(
                            requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}