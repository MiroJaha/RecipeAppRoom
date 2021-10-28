package com.example.recipeapproom

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ShowRecipe : AppCompatActivity() {

    private val connection by lazy { RecipesDatabase.getInstance(this).recipeDao() }

    private lateinit var titleTV: EditText
    private lateinit var authorTV: EditText
    private lateinit var ingredientsTV: EditText
    private lateinit var instructionsTV: EditText
    private lateinit var backButton: FloatingActionButton
    private lateinit var editButton: FloatingActionButton
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var bundle: Bundle
    private var pk= 0
    private lateinit var title: String
    private lateinit var author: String
    private lateinit var ingredients: String
    private lateinit var instructions: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe_information)

        titleTV= findViewById(R.id.titleTV)
        authorTV= findViewById(R.id.authorTV)
        ingredientsTV= findViewById(R.id.ingredientsTv)
        instructionsTV= findViewById(R.id.instructionsTV)
        backButton= findViewById(R.id.backButton)
        editButton= findViewById(R.id.editButton)
        deleteButton= findViewById(R.id.deleteButton)

        bundle= intent.extras!!
        pk= bundle.getInt("pk")
        title= "Recipe Name: ${bundle.getString("title")!!}"
        author= "Author Name: ${bundle.getString("author")!!}"
        ingredients= "Ingredients:\n\n${bundle.getString("ingredients")!!}"
        instructions= "Instructions:\n\n${bundle.getString("instructions")!!}"

        setHint()

        editButton.setOnClickListener{
            if (checkEntry()){
                CoroutineScope(IO).launch {
                    connection.updateRecipe(Information(pk, title, author, ingredients, instructions))
                }
                StyleableToast.makeText(
                    this,
                    "Updated Successfully!!",
                    R.style.mytoast
                ).show()
                clearEntry()
                setHint()
            }
            else
                StyleableToast.makeText(
                    this,
                    "Please Enter Valid Values!!",
                    R.style.mytoast
                ).show()
        }
        deleteButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Are You Sure You Want To Delete This Recipe")
                .setCancelable(false)
                .setPositiveButton("YES"){_,_ ->
                    CoroutineScope(IO).launch {
                        connection.deleteRecipe(Information(pk, title, author, ingredients, instructions))
                    }
                    StyleableToast.makeText(
                        this,
                        "Deleted Success!!",
                        R.style.mytoast
                    ).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }
                .setNegativeButton("Cancel"){dialog,_ -> dialog.cancel() }
                .show()
        }
        backButton.setOnClickListener{
            finish()
        }
    }

    private fun clearEntry() {
        titleTV.text.clear()
        authorTV.text.clear()
        ingredientsTV.text.clear()
        instructionsTV.text.clear()
    }

    private fun checkEntry(): Boolean {
        var check= false
        if (titleTV.text.isNotBlank()) {
            title= titleTV.text.toString()
            check= true
        }
        if (authorTV.text.isNotBlank()) {
            author= authorTV.text.toString()
            check= true
        }
        if (ingredientsTV.text.isNotBlank()) {
            ingredients= ingredientsTV.text.toString()
            check= true
        }
        if (instructionsTV.text.isNotBlank()) {
            instructions= instructionsTV.text.toString()
            check= true
        }
        return check
    }

    private fun setHint() {
        titleTV.hint= title
        authorTV.hint= author
        ingredientsTV.hint= ingredients
        instructionsTV.hint= instructions
    }
}