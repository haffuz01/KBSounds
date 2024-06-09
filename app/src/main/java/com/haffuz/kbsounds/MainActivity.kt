package com.haffuz.kbsounds

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.haffuz.kbsounds.ui.theme.KBSoundsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KBSoundsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlaylistItemPreview( modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
//@Preview(showBackground = true, showSystemUi = true)
@OptIn(UnstableApi::class)
@Composable
fun PlaylistItemPreview(modifier: Modifier) {

    val currentlyPlaying by remember {
        mutableIntStateOf(R.raw.forest1)
    }

    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()

    val fileSrc = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(
        currentlyPlaying.toString()
    ).build()

    val dataSourceFactory = DefaultDataSource.Factory(context)
    val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
    val mediaSource = remember (fileSrc)
    { MediaItem.fromUri(fileSrc) }

    val source1 = mediaSourceFactory.createMediaSource(mediaSource)

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaSource(source1)
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    @Composable
    fun PlaylistItem(song: Int, title: String, artist: String, img: Int) {

        val fileSource = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(
            song.toString()
        ).build()
        val dataSourceFact = DefaultDataSource.Factory(context)
        val mediaSourceFact = ProgressiveMediaSource.Factory(dataSourceFact)
        val medSource = remember (fileSource)
        { MediaItem.fromUri(fileSource) }

        val source = mediaSourceFact.createMediaSource(medSource)

        TextButton(
            onClick = {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.setMediaSource(source)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Row(
                modifier = Modifier
//                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .height(72.dp)
                        .width(72.dp)
                ) {
                    Image(
                        painter = painterResource(id = img),
                        contentScale = ContentScale.Crop,
                        contentDescription = "$title image",
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(text = artist, fontSize = 14.sp, fontWeight = FontWeight.Light)
                }
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "Play Button",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Z01 © Abdulhafiz Ahmed", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text(text = "Music and Image rights", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Row (horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth(0.4f)
                    ) {
                        Text(text = "Youtube.com", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "@MrStylemed", fontSize = 12.sp)
                        Text(text = "Natural Sounds Selection", fontSize = 12.sp, overflow = TextOverflow.Ellipsis,
                            maxLines = 1)
                        Text(text = "@TaylorTazJohnson", fontSize = 12.sp)
                        Text(text = "@johnnielawson", fontSize = 12.sp)

                    }
                    Column (
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.66f)
                    ) {
                        Text(text = "Pexels.com", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Fabian Wiktor", fontSize = 12.sp)
                        Text(text = "Leslie Jenkins", fontSize = 12.sp)
                        Text(text = "Lum3n", fontSize = 12.sp)
                        Text(text = "Sebastian Voortman", fontSize = 12.sp)

                    }
                }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text("Dismiss", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            }
        }
    }

    Column (verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "KBSounds", fontSize = 24.sp)
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Outlined.Info, contentDescription = "Licensing info", modifier = Modifier.size(24.dp))
                }
            }
            PlaylistItem(
                song = R.raw.forest1,
                title = stringResource(R.string.forest1_title),
                artist = stringResource(id = R.string.forest1_artist),
                img = R.drawable.forest1,
            )
            PlaylistItem(
                song = R.raw.forest2,
                title = stringResource(R.string.forest2_title),
                artist = stringResource(id = R.string.forest2_artist),
                img = R.drawable.forest2,
            )
            PlaylistItem(
                song = R.raw.water1,
                title = stringResource(R.string.water1_title),
                artist = stringResource(id = R.string.water1_artist),
                img = R.drawable.water1,
            )
            PlaylistItem(
                song = R.raw.water2,
                title = stringResource(R.string.water2_title),
                artist = stringResource(id = R.string.water2_artist),
                img = R.drawable.water2,
            )
        }
    AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    controllerHideOnTouch = false
                    controllerShowTimeoutMs = 0
                    showController()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}
