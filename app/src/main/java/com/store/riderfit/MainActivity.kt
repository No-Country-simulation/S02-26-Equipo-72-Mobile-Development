package com.store.riderfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.store.riderfit.presentation.ui.navigation.RiderFitNavGraph
import com.store.riderfit.presentation.ui.theme.RiderFitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RiderFitTheme {
                val navController = rememberNavController()
                RiderFitNavGraph(navController)
            }
        }
    }
}