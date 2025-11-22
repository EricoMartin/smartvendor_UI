package com.basebox.smartvendor.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.basebox.smartvendor.data.local.model.InventoryItem

@Composable
fun InventoryCard(item: InventoryItem, onRestockClick: () -> Unit, onSaleClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Text("Stock: ${item.stock}", color = Color.DarkGray)

                if (item.stock <= item.reorderThreshold) {
                    Text(
                        "Low stock!",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column {
                Button(
                    onClick = onSaleClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Sell", color = Color.White)
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = onRestockClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Restock", color = Color.White)
                }
                Spacer(Modifier.height(4.dp)) // Add a little space between buttons

                // Add the new Delete button
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Red color
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}
