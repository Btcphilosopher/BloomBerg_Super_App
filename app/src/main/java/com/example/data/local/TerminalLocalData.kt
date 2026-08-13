package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey val ticker: String,
    val name: String,
    val assetClass: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val ticker: String,
    val title: String,
    val condition: String,
    val thresholdValue: String,
    val category: String,
    val isTriggered: Boolean,
    val triggeredTime: String
)

@Entity(tableName = "saved_notes")
data class SavedNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val ticker: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface TerminalDao {
    // Watchlist
    @Query("SELECT * FROM watchlists ORDER BY addedTimestamp DESC")
    fun getWatchlist(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlists WHERE ticker = :ticker")
    suspend fun deleteWatchlist(ticker: String)

    // Alerts
    @Query("SELECT * FROM user_alerts ORDER BY id DESC")
    fun getAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("DELETE FROM user_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)

    // Notes
    @Query("SELECT * FROM saved_notes ORDER BY timestamp DESC")
    fun getSavedNotes(): Flow<List<SavedNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SavedNoteEntity)
}

@Database(
    entities = [WatchlistEntity::class, AlertEntity::class, SavedNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TerminalDatabase : RoomDatabase() {
    abstract fun terminalDao(): TerminalDao
}
