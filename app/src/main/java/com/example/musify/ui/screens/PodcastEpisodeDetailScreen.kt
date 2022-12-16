package com.example.musify.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musify.R
import com.example.musify.domain.PodcastEpisode
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.ExpandableText
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface


@Suppress("RemoveSingleExpressionStringTemplate")
private fun PodcastEpisode.getDateAndDurationString(context: Context): String {
    val dateString = "${releaseDateInfo.month} " +
            "${releaseDateInfo.day}, " +
            "${releaseDateInfo.year}"
    val hourString = if (durationInfo.hours == 0) {
        ""
    } else {
        context.resources.getQuantityString(
            R.plurals.numberOfHoursOfEpisode,
            durationInfo.hours,
            durationInfo.hours
        )
    }
    val minuteString = context.resources.getQuantityString(
        R.plurals.numberOfMinutesOfEpisode,
        durationInfo.minutes,
        durationInfo.minutes
    )
    return "$dateString • $hourString $minuteString"
}

@Composable
fun PodcastEpisodeDetailScreen(
    podcastEpisode: PodcastEpisode,
    onPlayButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit,
    onAddButtonClicked: () -> Unit,
    onDownloadButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    Column {
        PodcastEpisodeHeader(
            episodeImageUrl = podcastEpisode.podcastInfo.imageUrl,
            episodeTitle = podcastEpisode.title,
            podcastName = podcastEpisode.podcastInfo.name,
            dateAndDurationString = podcastEpisode.getDateAndDurationString(context),
            onBackButtonClicked = onBackButtonClicked
        )
        Spacer(Modifier.height(16.dp))
        // The episode header has a background gradient that needs to
        // be edge-to-edge. Therefore, use a separate column to add
        // horizontal padding to the rest of the content.
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionsRow(
                modifier = Modifier.fillMaxWidth(),
                onPlayButtonClicked = onPlayButtonClicked,
                onShareButtonClicked = onShareButtonClicked,
                onAddButtonClicked = onAddButtonClicked,
                onDownloadButtonClicked = onDownloadButtonClicked
            )
            ExpandableText(
                text = podcastEpisode.description,
                expandButtonText = "See more",
                style = MaterialTheme.typography.subtitle2,
                color = Color.White.copy(alpha = ContentAlpha.medium),
                maxLines = 5
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "See all episodes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_right_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun PodcastEpisodeHeader(
    onBackButtonClicked: () -> Unit,
    episodeImageUrl: String,
    episodeTitle: String,
    podcastName: String,
    dateAndDurationString: String,
) {
    val dynamicThemeResource = remember { DynamicThemeResource.FromImageUrl(episodeImageUrl) }
    val dynamicBackgroundType = remember { DynamicBackgroundType.Gradient() }
    var isImageLoadingPlaceholderVisible by remember { mutableStateOf(true) }
    DynamicallyThemedSurface(
        dynamicThemeResource = dynamicThemeResource,
        dynamicBackgroundType = dynamicBackgroundType
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                // add offset to accommodate for the touch target sizing
                // applied to the icon button
                modifier = Modifier.offset(x = (-16).dp),
                onClick = onBackButtonClicked
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_left_24),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            AsyncImageWithPlaceholder(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                model = episodeImageUrl,
                contentDescription = null,
                onImageLoadingFinished = { isImageLoadingPlaceholderVisible = false },
                isLoadingPlaceholderVisible = isImageLoadingPlaceholderVisible,
                onImageLoading = {
                    if (!isImageLoadingPlaceholderVisible) isImageLoadingPlaceholderVisible = true
                },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = episodeTitle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            // using a column to prevent the text being spaced by 16dp (set by the parent column)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = podcastName,
                    style = MaterialTheme.typography.caption,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateAndDurationString,
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
fun ActionsRow(
    modifier: Modifier = Modifier,
    onPlayButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit,
    onAddButtonClicked: () -> Unit,
    onDownloadButtonClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.width(width = 120.dp),
            onClick = onPlayButtonClicked,
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = "Play",
                fontWeight = FontWeight.SemiBold
            )
        }
        // Use a separate row to group the secondary action buttons. The primary
        // action button is the play button. This will make the parent row collectively
        // consider the secondary action buttons as a single row composable. This
        // ensures that the "SpaceBetween" arrangement (applied by the parent row)
        // creates space between the play button and the secondary action buttons
        // and not between all the buttons including the secondary action buttons.
        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconTintColor = Color.White.copy(alpha = ContentAlpha.medium)
            IconButton(onClick = onShareButtonClicked) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    tint = iconTintColor
                )
            }
            IconButton(onClick = onAddButtonClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_add_circle_outline_24),
                    contentDescription = null,
                    tint = iconTintColor
                )
            }
            IconButton(onClick = onDownloadButtonClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_download_for_offline_24),
                    contentDescription = null,
                    tint = iconTintColor
                )
            }
        }
    }
}
