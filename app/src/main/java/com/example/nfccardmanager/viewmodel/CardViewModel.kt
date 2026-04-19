// app/src/main/java/com/example/nfccardmanager/viewmodel/CardViewModel.kt
package com.example.nfccardmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nfccardmanager.data.Card
import com.example.nfccardmanager.data.CardDatabase
import com.example.nfccardmanager.repository.CardRepository
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardRepository
    val allCards = repository.getAllCards()
    private val _selectedCard = MutableLiveData<Card?>()
    val selectedCard: LiveData<Card?> = _selectedCard

    init {
        val cardDao = CardDatabase.getDatabase(application).cardDao()
        repository = CardRepository(cardDao)
    }

    fun insert(card: Card) = viewModelScope.launch {
        repository.insert(card)
    }

    fun delete(card: Card) = viewModelScope.launch {
        repository.delete(card)
    }

    fun selectCard(card: Card?) {
        _selectedCard.value = card
    }

    fun getCardByUid(uid: String) = viewModelScope.launch {
        _selectedCard.value = repository.getCardByUid(uid)
    }
}
