package com.daniel.dessertclicker

import android.os.Looper
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class DessertTimer(lifecycle: Lifecycle) : LifecycleObserver {

    // se the addObserver() method to connect the lifecycle object passed in from the owner (the activity) to this class (the observer).
    init {
        lifecycle.addObserver(this)
    }


    // The number of seconds counted since the timer started
    var secondsCount = 0

    /**
     * [Handler] is a class meant to process a queue of messages (known as [android.os.Message]s)
     * or actions (known as [Runnable]s)
     */
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startTimer() {
        // create a runnable action which prints out a log and increements the secounds counter
        runnable = Runnable {
            secondsCount++
            Timber.i("Timer is at: $secondsCount")

            // postDelayed re-adds the action to the queue of actions the Handler is cycling
            // through the delayMillis param tells the handler to run the runnable in 1 second

            handler.postDelayed(runnable, 1000)
        }

        // this what initially start the timer
        handler.postDelayed(runnable, 1000)

        // Note that the Thread the handler runs on is determined by a class called Looper.

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopTimer() {
        // Removes all pending posts of runnable from the handler's queue, effectively stopping the
        // timer
        handler.removeCallbacks(runnable)
    }

}