package com.nabilmh.example

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nabilmh.lottieswiperefreshlayout.LottieSwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val swipeRefreshLayout = findViewById<LottieSwipeRefreshLayout>(R.id.homePageSwipeRefresh)

        swipeRefreshLayout.setOnRefreshListener {
            Toast.makeText(this, "Refreshing Data", Toast.LENGTH_LONG).show()

            Handler().postDelayed({
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }

//        swipeRefreshLayout.setColorSchemeResources(Color.RED)


    }
}