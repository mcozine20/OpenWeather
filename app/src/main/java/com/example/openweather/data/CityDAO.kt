package com.example.openweather.data

import android.arch.persistence.room.*

@Dao
interface CityDAO {
    @Query("SELECT * FROM cities")
    fun getAllCities(): List<City>

    @Insert
    fun insertCity(city: City): Long

    @Insert
    fun insertCities(vararg city: City): List<Long>

    @Delete
    fun deleteCity(city: City)

    @Update
    fun updateCity(city: City)

    @Query("DELETE FROM cities")
    fun deleteAll()
}