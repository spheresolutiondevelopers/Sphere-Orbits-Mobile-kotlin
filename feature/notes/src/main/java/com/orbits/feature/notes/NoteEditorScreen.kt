package com.orbits.feature.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.notes.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    viewModel: NoteViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editorState by viewModel.editorState.collectAsState()
    val isEditMode = noteId != null

    // Load note if editing
    LaunchedEffect(noteId) {
        viewModel.handleEvent(NoteEvent.SelectNote(noteId))
    }

    LaunchedEffect(editorState.isSuccess) {
        if (editorState.isSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Note" else "New Note",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color(0xFF9B8DFA)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF9896C4))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Pin toggle logic */ }) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pin",
                            tint = if (editorState.isPinned) Color(0xFFFBBF24) else Color(0xFF9896C4)
                        )
                    }
                    if (isEditMode) {
                        IconButton(onClick = { /* Delete logic */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF9896C4))
                        }
                    }
                    TextButton(onClick = onBack) {
                        Text("Discard", color = Color(0xFF9896C4))
                    }
                    Button(
                        onClick = { viewModel.handleEvent(NoteEvent.FormSubmit) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF7C6CF8), Color(0xFFA855F7))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Text("Save", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF080712)
                )
            )
        },
        containerColor = Color(0xFF080712)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Toolbar
            EditorToolbar(
                selectedColor = editorState.color,
                onColorSelected = { viewModel.handleEvent(NoteEvent.FormColorChanged(it)) }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Title Input
                BasicTextField(
                    value = editorState.title,
                    onValueChange = { viewModel.handleEvent(NoteEvent.FormTitleChanged(it)) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (editorState.title.isEmpty()) {
                            Text("Note title...", color = Color(0xFF5E5C88), fontSize = 22.sp)
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Body Input
                BasicTextField(
                    value = editorState.content,
                    onValueChange = { viewModel.handleEvent(NoteEvent.FormContentChanged(it)) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 400.dp), // Removed weight(1f) to prevent crash in scrollable Column
                    decorationBox = { innerTextField ->
                        if (editorState.content.isEmpty()) {
                            Text(
                                "Write your thoughts, ideas, plans...",
                                color = Color(0xFF5E5C88),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF5E5C88))
                    
                    editorState.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                        TagChip(text = tag.trim()) {
                            // Remove tag logic
                        }
                    }

                    BasicTextField(
                        value = "", // Temp for input
                        onValueChange = { /* Add tag logic */ },
                        textStyle = TextStyle(color = Color.White, fontSize = 12.5.sp),
                        modifier = Modifier.width(90.dp),
                        decorationBox = { innerTextField ->
                            if (true) { // is empty
                                Text("Add tag...", color = Color(0xFF5E5C88), fontSize = 12.5.sp)
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditorToolbar(
    selectedColor: String?,
    onColorSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF080712))
            .border(width = (0.5).dp, color = Color.White.copy(alpha = 0.07f))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarButton(Icons.Default.FormatBold, "Bold")
            ToolbarButton(Icons.Default.FormatItalic, "Italic")
            ToolbarButton(Icons.Default.FormatUnderlined, "Underline")
            ToolbarButton(Icons.Default.StrikethroughS, "Strikethrough")
            
            VerticalDivider(modifier = Modifier.height(18.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.12f))
            
            ToolbarButton(Icons.AutoMirrored.Filled.FormatListBulleted, "List")
            ToolbarButton(Icons.Default.CheckBox, "Checklist")
            ToolbarButton(Icons.Default.Title, "Heading")
            ToolbarButton(Icons.Default.FormatQuote, "Quote")
            ToolbarButton(Icons.Default.TableChart, "Table")
            
            VerticalDivider(modifier = Modifier.height(18.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.12f))
            
            ToolbarButton(Icons.Default.CalendarMonth, "Link Event", tint = Color(0xFFFF6B8A))
            ToolbarButton(Icons.Default.TaskAlt, "Link Task", tint = Color(0xFF2DD4A0))
            ToolbarButton(Icons.Default.AttachFile, "Attach")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        NoteColorPicker(
            selectedColor = selectedColor,
            onColorSelected = onColorSelected
        )
    }
}

@Composable
fun ToolbarButton(
    icon: ImageVector,
    tooltip: String,
    tint: Color = Color(0xFF9896C4)
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(Color(0xFF1A1930), RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(8.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = tooltip, modifier = Modifier.size(12.dp), tint = tint)
    }
}

@Composable
fun TagChip(text: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color(0xFF7C6CF8).copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFF7C6CF8).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(text, color = Color(0xFF9B8DFA), fontSize = 12.sp)
        Icon(
            Icons.Default.Close, 
            contentDescription = null, 
            modifier = Modifier.size(9.dp).clickable { onRemove() },
            tint = Color(0xFF9B8DFA).copy(alpha = 0.7f)
        )
    }
}
