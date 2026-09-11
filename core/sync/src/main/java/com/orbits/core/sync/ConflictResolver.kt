package com.orbits.core.sync

import com.orbits.core.sync.strategy.SyncStrategy
import com.orbits.core.sync.strategy.LastWriteWinsStrategy
import com.orbits.core.sync.strategy.ServerWinsStrategy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages conflict resolution strategies for different entity types.
 */
@Singleton
class ConflictResolver @Inject constructor(
    private val lastWriteWinsStrategy: LastWriteWinsStrategy,
    private val serverWinsStrategy: ServerWinsStrategy
) {

    private val strategies = mutableMapOf<String, SyncStrategy>()

    init {
        // Default strategies by entity type
        registerStrategy(SyncConstants.ENTITY_TASK, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_SUBTASK, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_CALENDAR_EVENT, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_EVENT, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_EVENT_PARTICIPANT, serverWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_MEETING, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_MEETING_PARTICIPANT, serverWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_APPOINTMENT, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_APPOINTMENT_PARTICIPANT, serverWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_MESSAGE, serverWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_CONVERSATION, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_NOTE, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_SETTINGS, lastWriteWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_USER, serverWinsStrategy)
        registerStrategy(SyncConstants.ENTITY_AUTH, serverWinsStrategy)
    }

    /**
     * Register a strategy for an entity type.
     */
    fun registerStrategy(entityType: String, strategy: SyncStrategy) {
        strategies[entityType] = strategy
    }

    /**
     * Get the strategy for an entity type.
     * Returns LastWriteWins as the default if no strategy is registered.
     */
    fun getStrategy(entityType: String): SyncStrategy {
        return strategies[entityType] ?: lastWriteWinsStrategy
    }

    /**
     * Resolve a conflict using the appropriate strategy.
     */
    fun resolveConflict(
        localData: SyncData,
        serverData: SyncData,
        entityType: String,
        entityId: String
    ): com.orbits.core.sync.strategy.SyncConflictResolution {
        val strategy = getStrategy(entityType)
        return strategy.resolve(localData, serverData, entityType, entityId)
    }

    /**
     * Check if an entity type has a registered strategy.
     */
    fun hasStrategy(entityType: String): Boolean {
        return strategies.containsKey(entityType)
    }

    /**
     * Get all registered strategies.
     */
    fun getRegisteredStrategies(): Map<String, SyncStrategy> {
        return strategies.toMap()
    }
}
