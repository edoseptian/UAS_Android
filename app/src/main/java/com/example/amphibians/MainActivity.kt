package com.example.amphibians

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amphibians.ui.theme.AmphibiansTheme
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Item(
    val id: Int,
    val title: String,
    val imageUrl: String
)

interface ApiService {
    @GET("endpoint_data")
    suspend fun getItems(): List<Item>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/amphibians?hl=id")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val constraintLayout = ConstraintLayout(this)
        constraintLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        constraintLayout.setBackgroundColor(Color.White)

        val recyclerView = RecyclerView(this)
        recyclerView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraintLayout.addView(recyclerView)

        setContentView(constraintLayout)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = ItemAdapter()
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getItems()
                withContext(Dispatchers.Main) {
                    adapter.setItems(response)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }
}

private fun ConstraintLayout.setBackgroundColor(white: Color) {

}

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var items: List<Item> = listOf()

    fun setItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val constraintLayout = ConstraintLayout(context)
        constraintLayout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val title = TextView(context)
        title.id = View.generateViewId()
        title.textSize = 18f
        title.setTypeface(null, Typeface.BOLD)
        title.setPadding(8.dp, 8.dp, 8.dp, 0.dp)

        val imageView = ImageView(context)
        imageView.id = View.generateViewId()
        imageView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            200.dp
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setPadding(8.dp, 8.dp, 8.dp, 0.dp)

        constraintLayout.addView(title)
        constraintLayout.addView(imageView)

        val viewHolder = ViewHolder(constraintLayout, title, imageView)
        constraintLayout.setTag(R.id.item_view_holder, viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.title
        Picasso.get().load(currentItem.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        itemView: View,
        val title: TextView,
        val imageView: ImageView
    ) : RecyclerView.ViewHolder(itemView)
}

// Extension function for converting dp to pixels
private val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


