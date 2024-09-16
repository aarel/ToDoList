package com.dev.todolist

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    var cursor: Cursor?,   // Cursor is mutable, allowing it to be swapped later
    private val dbHelper: ToDoDatabaseHelper
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class that holds references to the UI components of each list item
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.taskName)
        val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
    }

    // Inflates the item_task.xml layout for each list item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    // Binds the data from the cursor to the UI components of each list item
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentCursor = cursor ?: return  // Safeguard: return if cursor is null

        // Move the cursor to the correct position
        if (!currentCursor.moveToPosition(position)) {
            return
        }

        // Get task data from the currentCursor
        val taskName = currentCursor.getString(currentCursor.getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_TASK))
        val isCompleted = currentCursor.getInt(currentCursor.getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_STATUS)) == 1
        val taskId = currentCursor.getInt(currentCursor.getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_ID))

        // Bind the data to the view
        holder.taskNameTextView.text = taskName
        holder.checkBoxComplete.isChecked = isCompleted

        // Handle checkbox toggle: update task status in the database
        holder.checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) 1 else 0
            dbHelper.updateTaskStatus(taskId, newStatus)
        }
    }

    // Returns the number of tasks (items) in the cursor
    override fun getItemCount(): Int {
        val currentCursor = cursor  // Local reference for safety
        return currentCursor?.count ?: 0  // Return 0 if cursor is null
    }

    // Function to swap the cursor and refresh the RecyclerView
    fun swapCursor(newCursor: Cursor?) {
        if (newCursor != null && newCursor != cursor) {
            val oldCursor = cursor  // Store reference to the old cursor
            cursor = newCursor      // Update the cursor with the new one
            notifyDataSetChanged()  // Notify RecyclerView that the data has changed

            // Close the old cursor to prevent memory leaks
            oldCursor?.close()
        }
    }
}
