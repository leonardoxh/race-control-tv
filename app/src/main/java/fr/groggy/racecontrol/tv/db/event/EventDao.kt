package fr.groggy.racecontrol.tv.db.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsert(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE id IN (:ids)")
    fun observeById(ids: List<String>): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun findEventsById(id: String): List<EventEntity>
}
