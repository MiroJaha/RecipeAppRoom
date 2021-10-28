package com.example.recipeapproom

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewRecipe : AppCompatActivity() {

    private val connection by lazy { RecipesDatabase.getInstance(this).recipeDao() }

    private lateinit var saveButton: Button
    private lateinit var viewButton: Button
    private lateinit var callData: Button
    private lateinit var titleEntry: EditText
    private lateinit var authorEntry: EditText
    private lateinit var ingredientsEntry: EditText
    private lateinit var instructionsEntry: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe)

        saveButton= findViewById(R.id.saveButton)
        viewButton= findViewById(R.id.viewButton)
        callData= findViewById(R.id.callData)
        titleEntry= findViewById(R.id.titleEntry)
        authorEntry= findViewById(R.id.authorEntry)
        ingredientsEntry= findViewById(R.id.ingredientsEntry)
        instructionsEntry= findViewById(R.id.instructionsEntry)

        viewButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        callData.setOnClickListener{
            callFromApi()
        }

        saveButton.setOnClickListener{
            if (checkEntry()){
                addNewRecipe()
                titleEntry.text.clear()
                authorEntry.text.clear()
                ingredientsEntry.text.clear()
                instructionsEntry.text.clear()
                val view: View? = this@AddNewRecipe.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            else{
                StyleableToast.makeText(this,"Please Enter Correct Values",R.style.mytoast).show()
            }
        }
    }

    private fun callFromApi() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        apiInterface?.getInformation()?.enqueue(object : Callback<List<Information>> {
            override fun onResponse(call: Call<List<Information>>, response: Response<List<Information>>) {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (recipe in response.body()!!) {
                            connection.addNewRecipe(
                                Information(
                                    recipe.pk,
                                    recipe.title!!,
                                    recipe.author!!,
                                    recipe.ingredients!!,
                                    recipe.instructions!!
                                )
                            )
                        }
                    }
                    StyleableToast.makeText(this@AddNewRecipe, "Saved Successfully!!", R.style.mytoast)
                        .show()
                    progressDialog.dismiss()
                } catch (e: Exception) {
                    Log.d("MyInformation", "failed")
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<List<Information>>, t: Throwable) {
                StyleableToast.makeText(this@AddNewRecipe, "Failed ", R.style.mytoast)
                    .show()
                progressDialog.dismiss()
            }
        })
    }

    private fun checkEntry() : Boolean{
        return when {
            titleEntry.text.isBlank() -> false
            authorEntry.text.isBlank() -> false
            ingredientsEntry.text.isBlank() -> false
            else -> instructionsEntry.text.isNotBlank()
        }
    }

    private fun addNewRecipe(){
        CoroutineScope(Dispatchers.IO).launch {
            connection.addNewRecipe(
                Information(
                    0,
                    titleEntry.text.toString(),
                    authorEntry.text.toString(),
                    ingredientsEntry.text.toString(),
                    instructionsEntry.text.toString()
                )
            )
        }
        StyleableToast.makeText(this, "Saved Successfully!!", R.style.mytoast)
            .show()
    }
}