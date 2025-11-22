package com.basebox.smartvendor.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReorderSuggestionsBanner(suggestedCount: Int, onClick: () -> Unit) {
    if (suggestedCount == 0) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF7BFC53).copy(alpha = 0.25f))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Reorder Suggestions Available",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "$suggestedCount items predicted to run out soon",
                color = Color.DarkGray
            )
        }
    }
}
