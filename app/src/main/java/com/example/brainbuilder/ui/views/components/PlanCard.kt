package com.example.brainbuilder.ui.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.brainbuilder.data.remote.dto.PlanItem
import com.example.brainbuilder.util.formatRupiah

@Composable
fun PlanCard(
    plan: PlanItem,
    onPayNow: () -> Unit
) {
    val formattedPrice = formatRupiah(plan.price)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${plan.durationDays} days",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(onClick = onPayNow) {
                    Text("Pay Now")
                }
            }
        }
    }
}