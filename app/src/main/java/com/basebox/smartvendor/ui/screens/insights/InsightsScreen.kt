package com.basebox.smartvendor.ui.screens.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basebox.smartvendor.R

@Composable
fun InsightsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Profit Trend", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Profit Trend Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LineChart(modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Best-selling products
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                DonutChart(modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Best-selling products", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(color = Color(0xFF4CAF50), text = "A")
                    LegendItem(color = Color(0xFF81C784), text = "B")
                    LegendItem(color = Color(0xFFA5D6A7), text = "C")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Restock forecast
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.ic_warning), contentDescription = "Warning", tint = Color(0xFFFFA726), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Restock forecast", style = MaterialTheme.typography.titleMedium)
                    Text("You'll run out of Peak Milk in 3 days")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text("AI Tips", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun LineChart(modifier: Modifier = Modifier) {
    val points = listOf(0.2f, 0.5f, 0.3f, 0.6f, 0.4f, 0.8f, 0.7f)
    Canvas(modifier = modifier.height(100.dp).fillMaxWidth()) {
        val path = Path()
        path.moveTo(0f, size.height * (1 - points.first()))
        points.forEachIndexed { index, point ->
            val x = size.width * (index.toFloat() / (points.size - 1))
            val y = size.height * (1 - point)
            path.lineTo(x, y)
        }
        drawPath(path, color = Color(0xFF4CAF50), style = Stroke(width = 5f))
    }
}

@Composable
fun DonutChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawArc(Color(0xFFA5D6A7), 0f, 120f, true)
        drawArc(Color(0xFF81C784), 120f, 100f, true)
        drawArc(Color(0xFF4CAF50), 220f, 140f, true)
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
