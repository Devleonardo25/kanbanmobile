package com.example.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.task.R
import com.example.task.data.model.Status
import com.example.task.data.model.Task
import com.example.task.databinding.FragmentFormTaskBinding
import com.example.task.util.initToolbar
import com.example.task.util.showBottomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database


class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task

    private var newTask: Boolean = true

    private var status: Status = Status.TODO

    private lateinit var reference: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()

        reference = Firebase.database.reference
        auth = Firebase.auth
    }

    private fun initListener() {
        binding.buttonSave.setOnClickListener {
            validateData()
        }


        //Evento que monitora a mudança de escolha do radioGroup
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            status = when (id) {
                R.id.rbTodo -> Status.TODO
                R.id.rbDoing -> Status.DOING
                else -> Status.DONE
            }
        }
    }

    private fun validateData(){
        val description = binding.editTextDescricao.text.toString().trim()
        if (description.isNotBlank()){
            binding.progressBar.isVisible = true

            if (newTask) task = Task(
                id = TODO(),
                description = TODO()
            )

            task.id = reference.database.reference.push().key ?: " "
            task.description = description
            task.status = status

            saveTask()
            makeText(requireContext(), "Tudo OK!", Toast.LENGTH_SHORT).show()
        }else{
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }

    private fun saveTask() {
        reference
            .child("task")
            .child(auth.currentUser?.uid ?: "")
            .child(task.id)
            .setValue(task).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    makeText(
                        requireContext(),
                         R.string.text_save_success_for_task_fragment,
                          Toast.LENGTH_SHORT
                    ).show()

                    if (newTask) {
                        //Criando nova tarefa
                        findNavController().popBackStack()
                    } else {

                        binding.progressBar.isVisible = false
                    }
                } else {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = getString(R.string.error_generic))
                }
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}