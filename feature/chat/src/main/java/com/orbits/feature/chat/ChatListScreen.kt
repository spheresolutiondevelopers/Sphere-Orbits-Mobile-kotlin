package com.orbits.feature.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.chat.components.*

@Composable
fun ChatListScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToConversation: (String) -> Unit,
    onNavigateToNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ─── Search Bar ─────────────────────────────────────
            ChatSearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.handleEvent(ChatEvent.Search(it)) }
            )

            // ─── Error Message ──────────────────────────────────
            if (state.errorMessage != null) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.handleEvent(ChatEvent.DismissError) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // ─── Conversation List ─────────────────────────────
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredConversations.isEmpty()) {
                ChatEmptyState(
                    onStartChat = onNavigateToNewChat,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.filteredConversations) { conversation ->
                        // Get display name and other details
                        // In production, this would come from the repository
                        val displayName = conversation.name ?: "Conversation"
                        ConversationItem(
                            conversation = conversation,
                            name = displayName,
                            lastMessage = "Last message placeholder",
                            timestamp = null,
                            unreadCount = 0,
                            isSelected = conversation.id == state.selectedConversationId,
                            onClick = {
                                viewModel.handleEvent(ChatEvent.SelectConversation(conversation.id))
                                onNavigateToConversation(conversation.id)
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToNewChat,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New chat"
            )
        }
    }
}
