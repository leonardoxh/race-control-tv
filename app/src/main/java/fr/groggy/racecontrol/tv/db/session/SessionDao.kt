package fr.groggy.racecontrol.tv.db.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsert(session: SessionEntity)

    @Insert(onConflict = REPLACE)
    suspend fun upsert(sessions: List<SessionEntity>)

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun observeById(id: String): Flow<SessionEntity>

    @Query("SELECT * FROM sessions WHERE event_id IN (:ids)")
    fun observeById(ids: List<String>): Flow<List<SessionEntity>>

}
