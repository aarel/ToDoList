package com.dev.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Create tasks table
        db?.execSQL(CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop old table if exists and create a new one
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Function to add a task to the database
    fun addTask(task: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK, task)  // Task name as a string
        values.put(COLUMN_STATUS, 0)   // Default status (0 = incomplete)
        return db.insert(TABLE_TASKS, null, values)
    }

    // Function to retrieve all tasks
    fun getAllTasks(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_TASKS", null)
    }

    // Function to update a task's status
    fun updateTaskStatus(id: Int, status: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, status)  // Update status (0 = incomplete, 1 = complete)
        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Function to delete a task
    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ToDoList.db"
        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TASK = "task"
        const val COLUMN_STATUS = "status"

        // SQL query to create the tasks table
        const val CREATE_TASKS_TABLE = (
                "CREATE TABLE $TABLE_TASKS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_TASK TEXT, " +
                        "$COLUMN_STATUS INTEGER DEFAULT 0)"
                )
    }
}
