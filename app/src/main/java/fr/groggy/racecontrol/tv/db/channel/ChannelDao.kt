package fr.groggy.racecontrol.tv.db.channel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsert(channels: List<ChannelEntity>)

    @Query("DELETE FROM channels WHERE contentId = :contentId")
    suspend fun deleteOld(contentId: String)

    @Query("SELECT * FROM channels WHERE contentId = :contentId ORDER BY order_index")
    fun observeByContentId(contentId: String): Flow<List<ChannelEntity>>

}
