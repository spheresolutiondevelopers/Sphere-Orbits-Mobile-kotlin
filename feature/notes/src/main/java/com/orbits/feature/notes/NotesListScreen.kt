package com.orbits.feature.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.domain.notes.Note
import com.orbits.feature.notes.components.*

@Composable
fun NotesListScreen(
    viewModel: NoteViewModel = hiltViewModel(),
    onNavigateToNoteEditor: (String?) -> Unit,
    onNavigateToNewNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val pinnedNotes = state.filteredNotes.filter { it.isPinned }
    val regularNotes = state.filteredNotes.filter { !it.isPinned }

    Scaffold(
        topBar = {
            NotesTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { viewModel.handleEvent(NoteEvent.Search(it)) },
                isGridView = state.isGridView,
                onToggleViewMode = { viewModel.handleEvent(NoteEvent.ToggleViewMode) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewNote,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF7C6CF8), Color(0xFFA855F7))
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("New Note", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color(0xFF080712)
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C6CF8))
            }
        } else {
            if (state.isGridView) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    // Pinned Section
                    if (pinnedNotes.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            PinnedSection(
                                pinnedNotes = pinnedNotes,
                                onNoteClick = { onNavigateToNoteEditor(it.id) }
                            )
                        }
                    }

                    // All Notes Header
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(
                            title = "All Notes",
                            icon = Icons.Default.History,
                            count = regularNotes.size
                        )
                    }

                    items(regularNotes) { note ->
                        NoteCard(
                            note = note,
                            modifier = Modifier.padding(horizontal = 4.dp), // Some padding inside grid
                            onClick = { onNavigateToNoteEditor(note.id) },
                            onPinToggle = { viewModel.handleEvent(NoteEvent.PinNote(note.id, !note.isPinned)) },
                            onDelete = { viewModel.handleEvent(NoteEvent.DeleteNote(note.id)) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Pinned Section
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            PinnedSection(
                                pinnedNotes = pinnedNotes,
                                onNoteClick = { onNavigateToNoteEditor(it.id) }
                            )
                        }
                    }

                    // All Notes Header
                    item {
                        SectionHeader(
                            title = "All Notes",
                            icon = Icons.Default.History,
                            count = regularNotes.size
                        )
                    }

                    items(regularNotes) { note ->
                        NoteCard(
                            note = note,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            onClick = { onNavigateToNoteEditor(note.id) },
                            onPinToggle = { viewModel.handleEvent(NoteEvent.PinNote(note.id, !note.isPinned)) },
                            onDelete = { viewModel.handleEvent(NoteEvent.DeleteNote(note.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isGridView: Boolean,
    onToggleViewMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(Color(0xFF080712).copy(alpha = 0.88f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF9896C4),
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = "My Notes",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color(0xFF9B8DFA)
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Search
        Box(
            modifier = Modifier
                .width(180.dp)
                .background(Color(0xFF1A1930), RoundedCornerShape(11.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(11.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Default.Search, 
                    contentDescription = null, 
                    modifier = Modifier.size(12.dp), 
                    tint = Color(0xFF5E5C88)
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 13.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Search notes...", color = Color(0xFF5E5C88), fontSize = 13.sp)
                        }
                        innerTextField()
                    }
                )
            }
        }
        
        IconButton(onClick = onToggleViewMode) {
            Icon(
                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                contentDescription = "Toggle View",
                tint = if (isGridView) Color(0xFF9B8DFA) else Color(0xFF9896C4)
            )
        }
        
        IconButton(onClick = { /* Toggle theme placeholder */ }) {
            Icon(Icons.Default.Contrast, contentDescription = "Theme", tint = Color(0xFF9896C4))
        }
    }
}

@Composable
fun PinnedSection(
    pinnedNotes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "Pinned",
            icon = Icons.Default.PushPin,
            count = pinnedNotes.size
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(pinnedNotes) { note ->
                PinnedNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
            tint = Color(0xFF5E5C88)
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFF5E5C88),
                fontSize = 12.sp
            )
        )
        Surface(
            color = Color(0xFF262440),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = Color(0xFF5E5C88)
                )
            )
        }
    }
}
