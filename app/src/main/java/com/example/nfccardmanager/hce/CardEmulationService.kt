// app/src/main/java/com/example/nfccardmanager/hce/CardEmulationService.kt
package com.example.nfccardmanager.hce

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import java.util.*

class CardEmulationService : HostApduService() {
    companion object {
        private const val TAG = "CardEmulationService"
        private val SELECT_APDU = byteArrayOf(
            0x00.toByte(), // CLA
            0xA4.toByte(), // INS
            0x04.toByte(), // P1
            0x00.toByte(), // P2
            0x07.toByte(), // LC
            0xF2.toByte(), 0x22.toByte(), 0x22.toByte(), 
            0x22.toByte(), 0x22.toByte(), 0x22.toByte(), 0x22.toByte() // AID
        )
        private val SUCCESS_RESPONSE = byteArrayOf(0x90.toByte(), 0x00.toByte())
    }

    private var emulatedCardData: ByteArray? = null

    fun setEmulatedCardData(data: ByteArray) {
        emulatedCardData = data
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.d(TAG, "Received APDU: ${commandApdu.contentToString()}")
        
        // Проверяем команду SELECT
        if (Arrays.equals(commandApdu.copyOf(SELECT_APDU.size), SELECT_APDU)) {
            Log.d(TAG, "SELECT command received")
            return SUCCESS_RESPONSE
        }
        
        // Возвращаем данные карты
        emulatedCardData?.let {
            Log.d(TAG, "Returning card data")
            return it + SUCCESS_RESPONSE
        }
        
        return byteArrayOf(0x6A.toByte(), 0x82.toByte()) // File not found
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Card emulation deactivated, reason: $reason")
    }
}
