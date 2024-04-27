package com.taein.travelmap

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.taein.travelmap.ui.theme.TravelMapTheme

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "TravelMap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelMapTheme {
                NavGraph()
            }
        }

    }
}