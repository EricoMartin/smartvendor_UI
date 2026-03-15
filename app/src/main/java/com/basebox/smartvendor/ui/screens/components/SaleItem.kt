package com.basebox.smartvendor.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basebox.smartvendor.data.local.model.Sales
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A composable that displays a single sale transaction with a professional look.
 *
 * @param sale The Sale object containing the data to display.
 */
@Composable
fun SaleItem(sale: Sales) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    val profitColor = if ((sale.profit ?: 0) >= 0) Color(0xFF388E3C) else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with a background
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Sale Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Item Name and Timestamp
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.item,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDate(sale.timestamp).toString() ?: "Just now",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sale Amount and Profit
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormat.format(sale.amount),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Profit: ${currencyFormat.format(sale.profit)}",
                    color = profitColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val pattern = "hh:mm a" // e.g., "02:30 PM"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
}

// Preview for easy UI development
@Preview(showBackground = true)
@Composable
private fun SaleItemPreview() {
    val previewSale = Sales(
        item = "Gala",
        amount = 150.0,
        profit = 30,
        timestamp = Date.from(Date().toInstant().minusSeconds(3600))
    )
    SaleItem(sale = previewSale)
}
