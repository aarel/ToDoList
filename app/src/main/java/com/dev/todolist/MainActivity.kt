package com.dev.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

class MainActivity : ComponentActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var recyclerViewTasks: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the database helper
        val dbHelper = ToDoDatabaseHelper(context = this@MainActivity)

        // Link the UI elements to their respective IDs and use explicit type casting
        editTextTask = findViewById<EditText>(R.id.editTextTask)
        buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)

        // Setup RecyclerView with a LinearLayoutManager
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        // Fetch all tasks from the database
        val cursor = dbHelper.getAllTasks()

        // Set up the RecyclerView with TaskAdapter
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(this, cursor, dbHelper)
        recyclerViewTasks.adapter = adapter

        // Add a new task when the button is clicked
        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotEmpty()) {
                dbHelper.addTask(task)
                editTextTask.text.clear()

                // Refresh the RecyclerView by fetching the tasks again
                val newCursor = dbHelper.getAllTasks()
                adapter.swapCursor(newCursor)  // Update the adapter with the new data
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We are not implementing drag & drop, so return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the position of the swiped item
                val position = viewHolder.bindingAdapterPosition

                // Get the cursor from the adapter safely
                val currentCursor = adapter.cursor ?: return  // Safeguard for null cursor

                // Move the cursor to the position of the swiped item
                if (currentCursor.moveToPosition(position)) {
                    // Get the ID of the task to delete from the cursor
                    val taskId = currentCursor.getInt(currentCursor.getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_ID))

                    // Delete the task from the database
                    dbHelper.deleteTask(taskId)

                    // Refresh the RecyclerView by getting the updated cursor
                    val newCursor = dbHelper.getAllTasks()
                    adapter.swapCursor(newCursor)  // Swap the cursor with new data
                }
            }

        }

        // Attach the ItemTouchHelper to the RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewTasks)
    }
}