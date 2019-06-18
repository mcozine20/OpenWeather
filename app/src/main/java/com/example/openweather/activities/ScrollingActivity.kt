package com.example.openweather.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.openweather.R
import com.example.openweather.adaptor.CityAdaptor
import com.example.openweather.data.AppDatabase
import com.example.openweather.data.City
import com.example.openweather.dialog.NewCityDialog
import com.example.openweather.touch.CityRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), NewCityDialog.CityHandler {

    lateinit var cityAdaptor: CityAdaptor

    companion object {
        const val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
        const val KEY_WAS_OPEN = "KEY_WAS_OPEN"
        const val TAG_CITY_DIALOG = "TAG_CITY_DIALOG"
        const val TUTORIAL_PRIMARY_TEXT = "New City"
        const val TUTORIAL_SECONDARY_TEXT = "Click here to add a city to your list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showAddCityDialog()
        }

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(TUTORIAL_PRIMARY_TEXT)
                .setSecondaryText(TUTORIAL_SECONDARY_TEXT)
                .show()
        }

        saveFirstOpenInfo()

        initRecyclerViewFromDB()

    }

    private fun saveFirstOpenInfo() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_WAS_OPEN, true)
        editor.apply()
    }

    private fun wasOpenedEarlier(): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(KEY_WAS_OPEN, false)
    }

    private fun initRecyclerViewFromDB() {
        Thread {
            var listCities = AppDatabase.getInstance(this@ScrollingActivity).cityDao().getAllCities()

            runOnUiThread {
                cityAdaptor = CityAdaptor(this, listCities, { city : City -> cityItemClicked(city)})
                recyclerCities.layoutManager = LinearLayoutManager(this)
                recyclerCities.adapter = cityAdaptor

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerCities.addItemDecoration(itemDecoration)

                val callback = CityRecyclerTouchCallback(cityAdaptor)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerCities)
            }

        }.start()
    }

    private fun showAddCityDialog() {
        NewCityDialog().show(supportFragmentManager, TAG_CITY_DIALOG)
    }

    var editIndex: Int = -1

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun cityCreated(city: City) {
        Thread {
            val newId = AppDatabase.getInstance(this).cityDao().insertCity(city)
            city.cityId = newId
            runOnUiThread {
                cityAdaptor.addCity(city)
            }
        }.start()
    }

    override fun cityUpdated(city: City) {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).cityDao().updateCity(city)
            runOnUiThread{
                cityAdaptor.updateCity(city, editIndex)
            }
        }.start()
    }

    override fun deleteAllCities() {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).cityDao().deleteAll()
            runOnUiThread{
                cityAdaptor.deleteAll()
            }
        }.start()
    }

    private fun cityItemClicked(city: City) {
        val detailIntent = Intent(this, WeatherDetailActivity::class.java)
        detailIntent.putExtra(Intent.EXTRA_TEXT, city.cityName)
        startActivity(detailIntent)
    }

}
