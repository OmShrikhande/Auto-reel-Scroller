package com.example.autoscrollapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoscrollapp.ui.theme.AutoScrollAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoScrollAppTheme {
                AutoScrollUI(
                    onStart = {
                        // Open accessibility settings if service not running
                        if (AutoScrollService.instance == null) {
                            Toast.makeText(this, "Please enable Accessibility Service", Toast.LENGTH_LONG).show()
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        } else {
                            AutoScrollService.instance?.startScrolling()
                            Toast.makeText(this, "Auto-scroll started", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ,
                    onStop = {
                        AutoScrollService.instance?.stopScrolling()
                        Toast.makeText(this, "Auto-scroll stopped", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun AutoScrollUI(onStart: () -> Unit, onStop: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("Start Auto-Scroll")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onStop, modifier = Modifier.fillMaxWidth()) {
            Text("Stop Auto-Scroll")
        }
    }
}
