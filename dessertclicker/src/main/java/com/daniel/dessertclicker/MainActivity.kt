package com.daniel.dessertclicker

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.daniel.dessertclicker.databinding.ActivityMainBinding
import timber.log.Timber

const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"
const val KEY_TIMER_SECONDS = "timer_seconds_key"


class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var dessertsSold = 0

    private lateinit var dessertTimer: DessertTimer


    // contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId:Int, val price: Int, val startProductionAmount: Int)


    // Create a list of all desserts, in order of when they start being produced
    private val allDessert = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )

    private var currentDessert = allDessert[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dessertTimer = DessertTimer(this.lifecycle)


        // If there is a savedInstanceState bundle, then you're "restarting" the activity
        // If there isn't a bundle, then it's a "fresh" start
        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            dessertTimer.secondsCount =
                savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            // Show the next dessert
            showCurrentDessert()
        }

        binding.dessertButton.setOnClickListener{
            onDessertClicked()
        }

        // set the textviews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
        outState.putInt(KEY_TIMER_SECONDS, dessertTimer.secondsCount)
        outState.putInt(KEY_REVENUE, revenue)

        Timber.i("onSaveInstanceState Called")
    }


    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // show the next dessert
        showCurrentDessert()

    }

    override fun onStart() {
        super.onStart()
//        dessertTimer.startTimer()

        // DessertTimer is now observing the lifecycle itself and is automatically notified when the lifecycle state changes. All you do in these callbacks now is log a message.
        Timber.i("onStart called")
    }

    override fun onStop() {
        super.onStop()
//        dessertTimer.stopTimer()
//DessertTimer is now observing the lifecycle itself and is automatically notified when the lifecycle state changes. All you do in these callbacks now is log a message.
        Timber.i("onStop Called")
    }


    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDessert[0]
        for (dessert in allDessert){
            if (dessertsSold >= dessert.startProductionAmount){
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }
    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}