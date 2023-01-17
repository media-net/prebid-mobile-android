package com.app.analytics.providers.cached.db

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class EventDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val pixel: String
)
