package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation


class AddTaskFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonClose: ImageButton = view.findViewById(R.id.imageButtonClose)
        val TextViewSafe: TextView = view.findViewById(R.id.SafeTextView)
        val editViewTask: EditText = view.findViewById(R.id.EditTextTaskOf)
        TextViewSafe.setOnClickListener(){
            if(editViewTask != null){
                // нужно сохранить данные все
            }
        }

        buttonClose.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }
    }

}