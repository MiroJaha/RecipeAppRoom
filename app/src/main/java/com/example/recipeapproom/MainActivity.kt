package com.example.recipeapproom

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapproom.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val connection by lazy { RecipesDatabase.getInstance(this).recipeDao() }

    private lateinit var addButton: Button
    private lateinit var rvInformation: RecyclerView
    private lateinit var informationList : ArrayList<Information>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connection

        addButton = findViewById(R.id.addButton)
        rvInformation = findViewById(R.id.rvInformation)
        informationList = arrayListOf()

        addButton.setOnClickListener {
            startActivity(Intent(this, AddNewRecipe::class.java))
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        val intent = Intent(this@MainActivity, ShowRecipe::class.java)

        CoroutineScope(IO).launch {
            val data= async {
                connection.gettingAllRecipes()
            }.await()
            withContext(Main){
                informationList.addAll(data)
                val adapter = RVAdapter(informationList)
                rvInformation.adapter = adapter
                rvInformation.layoutManager = LinearLayoutManager(this@MainActivity)
                adapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        intent.putExtra("pk", informationList[position].pk)
                        intent.putExtra("title", informationList[position].title)
                        intent.putExtra("author", informationList[position].author)
                        intent.putExtra("ingredients", informationList[position].ingredients)
                        intent.putExtra("instructions", informationList[position].instructions)
                        startActivity(intent)
                    }
                })
                progressDialog.dismiss()
            }
        }
    }
}