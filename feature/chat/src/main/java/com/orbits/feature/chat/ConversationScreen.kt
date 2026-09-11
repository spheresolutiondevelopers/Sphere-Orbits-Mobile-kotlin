package com.orbits.feature.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.chat.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    conversationId: String,
    viewModel: ChatViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.conversationState.collectAsState()
    val listState = rememberLazyListState()

    // Load messages when conversation ID changes
    LaunchedEffect(conversationId) {
        viewModel.handleEvent(ChatEvent.LoadMessages(conversationId))
    }

    // Scroll to bottom when messages change
    LaunchedEffect(state.messages) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Row for specific actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.conversation?.name ?: "Conversation",
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.isTyping && state.typingUserId != null) {
                    Text(
                        text = "Typing...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { /* Navigate to conversation info */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info"
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "❌",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = state.errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(
                        onClick = {
                            viewModel.handleEvent(ChatEvent.LoadMessages(conversationId))
                        }
                    ) {
                        Text("Retry")
                    }
                }
            } else if (state.messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "💬",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Start the conversation!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Group messages by date
                    val groupedMessages = state.messages.groupBy {
                        it.getFormattedDate()
                    }

                    groupedMessages.forEach { (date, messages) ->
                        item {
                            ChatDateDivider(date = date)
                        }
                        items(messages) { message ->
                            val isFromCurrentUser = message.senderUserId == viewModel.getCurrentUserId()
                            ChatMessageItem(
                                message = message,
                                isFromCurrentUser = isFromCurrentUser,
                                showAvatar = true
                            )
                        }
                    }
                }

                // Typing indicator at bottom
                ChatTypingIndicator(
                    isTyping = state.isTyping,
                    userName = state.typingUserId,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }

        MessageInput(
            onSendMessage = { message ->
                viewModel.handleEvent(ChatEvent.SendMessage(conversationId, message))
            },
            isSending = state.isSending,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
