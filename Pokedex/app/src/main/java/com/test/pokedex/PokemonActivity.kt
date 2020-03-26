package com.test.pokedex

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

class PokemonActivity : AppCompatActivity() {

    val pikachuID = 151




    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var pokeImgView: ImageView
    private lateinit var pokeNameView: TextView

    private lateinit var pokeData: JsonObject
    private var query: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon)

        manageIntent()
        initializeComponents()
        initializeData()
    }

    private fun manageIntent() {
        if (intent != null) {
            // Of course the default has to be Pikachu
            query = intent.getIntExtra("id",pikachuID).toString()
        }
    }

    private fun initializeComponents(){
        pokeImgView = findViewById(R.id.pokemon_picture)
        pokeNameView = findViewById(R.id.pokemon_name)
    }

    private fun appendTextView(innerText: String, parentID: Int) {
        val linearLayout: LinearLayout = findViewById(parentID)
        var textArea = TextView(this)
        textArea.gravity = Gravity.CENTER
        textArea.textSize = 20f
        textArea.setTextColor(Color.parseColor("#353535"))
        textArea.text = innerText
        linearLayout.addView(textArea)
    }

    private fun initializeData(){
        Ion.with(this)
            .load("https://pokeapi.co/api/v2/pokemon/$query/")
            .asJsonObject()
            .done { error, result ->
                pokeData = result
                if(error == null){
                    if(!pokeData.get("sprites").isJsonNull){
                        if(pokeData.get("sprites").asJsonObject.get("front_default") != null){
                            //Pintar
                            Glide.with(this)
                                .load(result.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(pokeImgView)
                        } else {
                            pokeImgView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.pokemon_logo_min
                                )
                            )
                        }
                    } else {
                        pokeImgView.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.pokemon_logo_min
                            )
                        )
                        Log.e("JSON", "Cannot load sprite teacher please be mercifull")
                    }

                    if(!pokeData.get("name").isJsonNull){
                        val name = result.get("name").toString()
                        pokeNameView.text = name
                    } else {
                        pokeNameView.text = "Doesn't exist :,("
                    }

                    if(!pokeData.get("types").isJsonNull){
                        val types = result.get("types").asJsonArray
                        for(t in types){
                            val typeClass = t.asJsonObject.get("type").asJsonObject
                            val typeTitle = typeClass.get("name").toString()
                            appendTextView(typeTitle, R.id.types)
                        }
                    } else {
                        Log.e("JSON","No types")
                    }

                    if(!pokeData.get("stats").isJsonNull){
                        val stats = result.get("stats").asJsonArray
                        for(s in stats){

                            val statData = s.asJsonObject
                            val number = statData.get("base_stat").toString()
                            val name = statData.get("stat").asJsonObject.get("name").toString()
                            appendTextView("$name: $number", R.id.stats)
                        }
                    } else {
                        Log.e("JSON","No stats")
                    }

                    if(!pokeData.get("moves").isJsonNull){
                        val moves = result.get("moves").asJsonArray
                        for(m in moves){
                            val name = m.asJsonObject.get("move").asJsonObject.get("name").toString()
                            appendTextView(name, R.id.moves)
                        }
                    } else {
                        Log.e("JSON","No moves")
                    }
                }
                initList()
            }
    }

    fun initList(){
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)
    }
}
