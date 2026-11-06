package com.leonardo.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leonardo.task.R
import com.leonardo.task.data.model.Task
import com.leonardo.task.databinding.FragmentTodoBinding
import com.leonardo.task.ui.adapter.TaskAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database



class TodoFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var reference: DatabaseReference

    private lateinit var auth: FirebaseAuth

    private var valueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = Firebase.database.reference
        auth = Firebase.auth

        initListener()
        initRecyclerViewTask()
        getTask()
    }

    private fun initListener() {
        binding.floatingActionButton2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
        }
    }

    private fun initRecyclerViewTask() {
        taskAdapter = TaskAdapter(requireContext()) { task, option -> optionSelected(task, option) }

        with(binding.recyclerViewTask) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = taskAdapter
        }
    }

    private fun optionSelected(task: Task, option: Int) {
        when (option) {
            TaskAdapter.SELECT_REMOVER -> {
                Toast.makeText(requireContext(), "Removendo ${task.description}", Toast.LENGTH_SHORT).show()
            }

            TaskAdapter.SELECT_EDIT -> {
                Toast.makeText(requireContext(), "Editando ${task.description}", Toast.LENGTH_SHORT).show()
            }

            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(requireContext(), "Detalhes ${task.description}", Toast.LENGTH_SHORT).show()
            }

            TaskAdapter.SELECT_NEXT -> {
                Toast.makeText(requireContext(), "Próximo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTask() {
        valueEventListener = reference
            .child("tasks")
            .child(auth.currentUser?.uid ?: "")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()

                    for (ds in snapshot.children) {
                        val task = ds.getValue(Task::class.java)
                        task?.let { taskList.add(it) }
                    }

                    taskAdapter.submitList(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            R.string.error_generic,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Remove o listener para não receber dados depois que a tela some
        valueEventListener?.let {
            reference
                .child("tasks")
                .child(auth.currentUser?.uid ?: "")
                .removeEventListener(it)
        }
        valueEventListener = null

        _binding = null
    }
}