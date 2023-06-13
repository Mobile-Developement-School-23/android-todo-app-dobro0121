package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.Navigation


class AddTaskFragment : Fragment() {

    var textTask: String? = null
    var textImportant: String? = null
    var textDoneTo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonClose: ImageButton = view.findViewById(R.id.imageButtonClose)
        val textViewDelete: TextView = view.findViewById(R.id.DeleteTextView)
        val textViewSafe: TextView = view.findViewById(R.id.SafeTextView)
        val editViewTask: EditText = view.findViewById(R.id.EditTextTaskOf)

        textTask = arguments?.getString("textOfTask")
        textImportant = arguments?.getString("importance")
        //textDoneTo = arguments?.getString("deadline")
        editViewTask.setText(textTask)
        val spinner: Spinner = view.findViewById(R.id.spinnerImportant)
        //?? подумать как передавать важность
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        dateTextView.text = textDoneTo

        textViewSafe.setOnClickListener(){
            if(editViewTask != null){
                // нужно сохранить данные все
            }
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        buttonClose.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        textViewDelete.setOnClickListener() {
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }
    }

}