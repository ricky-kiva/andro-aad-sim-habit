package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit
        val workManager = WorkManager.getInstance(applicationContext)

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

        // XTODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)

        viewModel.currentTimeString.observe(this) { timeString ->
            findViewById<TextView>(R.id.tv_count_down).text = timeString
        }

        viewModel.eventCountDownFinish.observe(this) { countdownFinished ->
            if (countdownFinished) {
                updateButtonState(isRunning = false)
                notifyWorker(habit, workManager)
            }
        }

        // XTODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.startTimer()
            updateButtonState(isRunning = true)
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            cancelTimer(workManager)
            viewModel.onStopButtonClick()
            updateButtonState(isRunning = false)
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }

    private fun notifyWorker(habit: Habit, workManager: WorkManager) {
        val inputData = workDataOf(HABIT_ID to habit.id, HABIT_TITLE to habit.title)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(notificationRequest)
    }

    private fun cancelTimer(workManager: WorkManager) {
        workManager.cancelAllWorkByTag(HABIT_ID)
    }
}