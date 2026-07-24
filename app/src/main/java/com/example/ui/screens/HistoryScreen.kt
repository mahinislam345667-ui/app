package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.ScanResultEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel
import java.io.File

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onSelectScan: () -> Unit
) {
    val historyList by viewModel.scansHistory.collectAsState()
    var filterFavoritesOnly by remember { mutableStateOf(false) }

    val filteredList = if (filterFavoritesOnly) {
        historyList.filter { it.isFavorite }
    } else {
        historyList
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Scan History",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = { filterFavoritesOnly = false },
                    shape = RoundedCornerShape(14.dp),
                    color = if (!filterFavoritesOnly) NeonPurple.copy(alpha = 0.25f) else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!filterFavoritesOnly) NeonPurple else DarkCardBorder
                    )
                ) {
                    Text(
                        text = "All Scans (${historyList.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (!filterFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                            color = if (!filterFavoritesOnly) TextPrimary else TextSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                Surface(
                    onClick = { filterFavoritesOnly = true },
                    shape = RoundedCornerShape(14.dp),
                    color = if (filterFavoritesOnly) NeonPink.copy(alpha = 0.25f) else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (filterFavoritesOnly) NeonPink else DarkCardBorder
                    )
                ) {
                    Text(
                        text = "Favorites (${historyList.count { it.isFavorite }})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (filterFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                            color = if (filterFavoritesOnly) TextPrimary else TextSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredList.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No History",
                            tint = NeonPurple.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (filterFavoritesOnly) "No favorite scans saved yet." else "No neural scans found.",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HistoryCardItem(
                            scan = item,
                            onClick = {
                                viewModel.selectScanResult(item)
                                onSelectScan()
                            },
                            onToggleFavorite = { viewModel.toggleFavoriteScan(item) },
                            onDelete = { viewModel.deleteScan(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCardItem(
    scan: ScanResultEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        cornerRadius = 18.dp,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                if (!scan.imagePath.isNullOrEmpty() && File(scan.imagePath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(scan.imagePath)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selfie",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        FallbackThumbnail()
                    }
                } else {
                    FallbackThumbnail()
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Est. Age: ${scan.estimatedAge} Yrs",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${scan.overallScore}% Score",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = scan.getFormattedDate(),
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (scan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (scan.isFavorite) NeonPink else TextSecondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextSecondary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun FallbackThumbnail() {
    Image(
        painter = painterResource(id = R.drawable.sample_selfie_1_1784904810228),
        contentDescription = "Sample Selfie",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
