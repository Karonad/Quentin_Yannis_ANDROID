package com.example.androidcodelab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_task.*
import java.util.*

class TaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_task)
        val task = intent.getSerializableExtra(TasksFragment.TASK) as? Task
        val taskDescriptionShare = intent.getStringExtra(Intent.EXTRA_TEXT)


        if(task != null){
            create_title.setText(task.title)
            create_description.setText(task.description)
        }

        if(taskDescriptionShare != null){
            create_description.setText(taskDescriptionShare)
        }

        share_description.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, create_description.text.toString())
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        valid_create_button.setOnClickListener{
            val newTask = Task(
                id = task?.id ?: UUID.randomUUID().toString(),
                title = create_title.text.toString(),
                description = create_description.text.toString()
            )
            intent.putExtra(EXTRA_REPLY, newTask)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    companion object {
        const val EXTRA_REPLY = "add_task_key"
    }


}

