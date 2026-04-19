// app/src/main/java/com/example/nfccardmanager/data/CardDao.kt
package com.example.nfccardmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE uid = :uid LIMIT 1")
    suspend fun getCardByUid(uid: String): Card?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: Card): Long

    @Delete
    suspend fun delete(card: Card)

    @Update
    suspend fun update(card: Card)
}
