package com.example.openweather.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true) var cityId: Long?,
    @ColumnInfo(name = "cityName") var cityName: String
) : Serializable