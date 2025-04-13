package com.oltrysifp.arrowdrawer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oltrysifp.arrowdrawer.composable.EdgeToEdgeConfig
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.composable.inputs.TextFieldDefault
import com.oltrysifp.arrowdrawer.models.Project
import com.oltrysifp.arrowdrawer.repositories.ProjectRepository
import com.oltrysifp.arrowdrawer.ui.theme.ArrowDrawerTheme
import com.oltrysifp.arrowdrawer.util.Palette
import com.oltrysifp.arrowdrawer.util.bitmap.loadBitmapFromUri
import com.oltrysifp.arrowdrawer.util.log
import com.oltrysifp.arrowdrawer.util.palette
import com.oltrysifp.arrowdrawer.viewModels.ProjectViewModel
import com.oltrysifp.arrowdrawer.viewModels.ProjectViewModelFactory
import kotlin.math.abs
import kotlin.random.Random

class Menu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mContext = LocalContext.current
            val repository = remember { ProjectRepository(mContext) }
            val viewModelFactory = remember { ProjectViewModelFactory(repository) }
            val viewModel: ProjectViewModel = viewModel(factory = viewModelFactory)

            val isSaving = viewModel.isSaving.collectAsState()

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
                        viewModel.createProject(
                            Project(
                                filename,
                                bt,
                                listOf()
                            )
                        )
                        imageUri = null
                    }
                }
            }

            ArrowDrawerTheme {
                EdgeToEdgeConfig(this)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        AddButton(
                            isSaving.value,
                            askImage
                        )
                    }
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MenuScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    viewModel: ProjectViewModel = viewModel()
) {
    val mContext = LocalContext.current

    val projects by viewModel.projects.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "spacer") { VSpacer(4.dp) }

        items(projects, key = {
            "project ${it.name}"
        }) {
            Box(
                Modifier.animateItem()
            ) {
                ProjectCard(
                    it,
                    onOpen = {
                        val intent = Intent(mContext, MainActivity::class.java)
                        val b = Bundle()
                        b.putString("project", it.name)
                        intent.putExtras(b)
                        mContext.startActivity(intent)
                    },
                    onEdit = { project ->
                        viewModel.renameProject(it.name, project.name)
                    },
                    onDelete = {
                        viewModel.deleteProject(it.name)
                    }
                )
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: Project,
    onOpen: () -> Unit,
    onEdit: (Project) -> Unit,
    onDelete: () -> Unit
) {
    var editMode by remember { mutableStateOf(false) }

    val projectName = remember { mutableStateOf(project.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = palette.surface
        ),
        onClick = onOpen
    ) {
        Row(
            Modifier.padding(
                vertical = 10.dp,
                horizontal = 10.dp
            )
        ) {
            AnimatedVisibility(
                !editMode
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
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
            }

            AnimatedContent(
                editMode,
                label = "editMode"
            ) { isEdit ->
                if (!isEdit) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            project.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.weight(1f)
                        )

                        HSpacer(10.dp)

                        IconButton(
                            onClick = {
                                editMode = true
                            }
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                "edit"
                            )
                        }
                    }
                } else {
                    Column() {
                        TextFieldDefault(
                            projectName,
                            modifier = Modifier.fillMaxWidth()
                        )

                        VSpacer(2.dp)

                        Row(
                            Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row {
                                IconButton(
                                    onClick = {
                                        editMode = false
                                        onEdit(project.copy(
                                            name = projectName.value
                                        ))
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Done,
                                        "done"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editMode = false
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        "close"
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDelete
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    "delete"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddButton(
    isSaving: Boolean,
    onAdd: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            if (!isSaving) {
                onAdd()
            }
        },
        containerColor = if (isSaving) palette.cancel else palette.primary
    ) {
        Box(
            Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isSaving) {
                Icon(
                    Icons.Filled.Add,
                    "Create project",
                    tint = palette.onPrimary
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 1.dp,
                    color = palette.primary,
                    trackColor = palette.onSurface,
                )
            }
        }
    }
}