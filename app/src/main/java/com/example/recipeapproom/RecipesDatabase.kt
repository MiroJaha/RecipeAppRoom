package com.example.recipeapproom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Information::class],version = 1, exportSchema = false)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object{
        @Volatile
        var instance: RecipesDatabase? =null

        fun getInstance(context: Context): RecipesDatabase{
            if (instance!=null)
                return instance!!
            synchronized(this){
                instance= Room.databaseBuilder(context.applicationContext,
                    RecipesDatabase::class.java,
                "Recipe")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}