package com.example.recipeapproom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Recipes")
data class Information(
    @PrimaryKey(autoGenerate = true) val pk: Int,
    val title: String?,
    val author: String?,
    val ingredients: String?,
    val instructions: String?
)
