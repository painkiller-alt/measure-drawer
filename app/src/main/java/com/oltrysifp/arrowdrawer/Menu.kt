package com.oltrysifp.arrowdrawer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.bitmap.loadAllProjectsFromAppStorage
import com.oltrysifp.arrowdrawer.bitmap.loadBitmapFromUri
import com.oltrysifp.arrowdrawer.bitmap.saveBitmapToInternalStorage
import com.oltrysifp.arrowdrawer.composable.EdgeToEdgeConfig
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.models.Project
import com.oltrysifp.arrowdrawer.ui.theme.ArrowDrawerTheme
import com.oltrysifp.arrowdrawer.util.log
import com.oltrysifp.arrowdrawer.util.palette
import kotlin.math.abs
import kotlin.random.Random

class Menu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mContext = LocalContext.current

            val projectCreated = remember { mutableStateOf(false) }

            var imageUri: Uri? by remember { mutableStateOf(null) }
            var bitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
            val pickMedia = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri -> if (uri != null) { imageUri = uri } }
            val askImage = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            LaunchedEffect(imageUri) {
                imageUri?.let { uri ->
                    val filename = "project ${abs(Random.nextInt())}"
                    bitmap = loadBitmapFromUri(mContext, uri)
                    bitmap?.let { bt ->
                        val saveResult = saveBitmapToInternalStorage(
                            mContext,
                            bt,
                            "origin",
                            filename
                        )
                        log(saveResult)
                        projectCreated.value = true
                        imageUri = null
                    }
                }
            }

            ArrowDrawerTheme {
                EdgeToEdgeConfig(this)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { askImage() },
                            containerColor = if (imageUri != null) palette.cancel else palette.primary
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                "Create project",
                                tint = palette.onPrimary
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MenuScreen(projectCreated)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    projectCreated: MutableState<Boolean>
) {
    val mContext = LocalContext.current

    val projects = remember { mutableStateListOf<Project>() }

    LaunchedEffect(projectCreated.value) {
        val allProjects = loadAllProjectsFromAppStorage(mContext)
        projects.clear()
        projects.addAll(allProjects)
        projectCreated.value = false
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VSpacer(4.dp)

        projects.forEach {
            ProjectCard(it) {
                val intent = Intent(mContext, MainActivity::class.java)
                val b = Bundle()
                b.putString("project", it.name)
                intent.putExtras(b)
                mContext.startActivity(intent)
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: Project,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = palette.onSurface
        ),
        onClick = onOpen
    ) {
        Row(
            Modifier.padding(
                vertical = 10.dp,
                horizontal = 10.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    project.image.asImageBitmap(),
                    "image",
                    contentScale = ContentScale.Crop
                )
            }

            HSpacer(6.dp)

            Text(
                project.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
        }
    }
}