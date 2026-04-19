// app/src/main/java/com/example/nfccardmanager/repository/CardRepository.kt
package com.example.nfccardmanager.repository

import com.example.nfccardmanager.data.Card
import com.example.nfccardmanager.data.CardDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CardRepository(private val cardDao: CardDao) {
    
    fun getAllCards(): Flow<List<Card>> = cardDao.getAllCards()

    suspend fun getCardByUid(uid: String): Card? = cardDao.getCardByUid(uid)

    suspend fun insert(card: Card): Long = withContext(Dispatchers.IO) {
        cardDao.insert(card)
    }

    suspend fun delete(card: Card) = withContext(Dispatchers.IO) {
        cardDao.delete(card)
    }

    suspend fun update(card: Card) = withContext(Dispatchers.IO) {
        cardDao.update(card)
    }
}
