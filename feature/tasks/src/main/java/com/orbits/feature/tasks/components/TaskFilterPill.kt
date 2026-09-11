package com.orbits.feature.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.core.theme.SphereTheme

@Composable
fun TaskFilterPill(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    count: Int,
    isActive: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(38.dp)
            .clip(CircleShape),
        color = if (isActive) SphereTheme.colors.cardTertiary else SphereTheme.colors.cardSecondary,
        border = if (isActive) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, SphereTheme.colors.borderSecondary)
        },
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(iconColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(12.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SphereTheme.colors.textTertiary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) MaterialTheme.colorScheme.primary else SphereTheme.colors.textSecondary,
                fontWeight = FontWeight.Bold
            )

            // Count badge
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary else Color(0xFF4040A8),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = SphereTheme.colors.textTertiary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
