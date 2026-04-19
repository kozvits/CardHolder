// app/src/main/java/com/example/nfccardmanager/ui/ScanActivity.kt
package com.example.nfccardmanager.ui

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcA
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nfccardmanager.R
import com.example.nfccardmanager.data.Card
import com.example.nfccardmanager.databinding.ActivityScanBinding
import com.example.nfccardmanager.viewmodel.CardViewModel
import java.io.IOException

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var viewModel: CardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CardViewModel::class.java]
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        
        pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        binding.textViewInstructions.text = getString(R.string.scan_instructions)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let { readTag(it) }
        }
    }

    private fun readTag(tag: Tag) {
        try {
            when {
                MifareClassic::class.java.name in tag.techList -> {
                    readMifareClassic(tag)
                }
                MifareUltralight::class.java.name in tag.techList -> {
                    readMifareUltralight(tag)
                }
                else -> {
                    Toast.makeText(this, "Неподдерживаемый тип карты", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка чтения карты: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readMifareClassic(tag: Tag) {
        val mifareClassic = MifareClassic.get(tag)
        try {
            mifareClassic.connect()
            
            // Чтение данных из секторов (пример для первых 4 секторов)
            val data = mutableListOf<ByteArray>()
            for (sector in 0 until minOf(4, mifareClassic.sectorCount)) {
                if (mifareClassic.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT)) {
                    for (block in 0 until mifareClassic.getBlockCountInSector(sector)) {
                        val blockIndex = mifareClassic.sectorToBlock(sector) + block
                        data.add(mifareClassic.readBlock(blockIndex))
                    }
                }
            }
            
            saveCard(tag.id.toHexString(), "Mifare Classic", data.flatten().toByteArray())
            mifareClassic.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка подключения к карте", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readMifareUltralight(tag: Tag) {
        val ultralight = MifareUltralight.get(tag)
        try {
            ultralight.connect()
            
            // Чтение страниц данных
            val data = mutableListOf<ByteArray>()
            for (page in 0..3) { // Читаем первые 4 страницы
                data.add(ultralight.readPages(page))
            }
            
            saveCard(tag.id.toHexString(), "Mifare Ultralight", data.flatten().toByteArray())
            ultralight.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка подключения к карте", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCard(uid: String, type: String, data: ByteArray) {
        Thread {
            runOnUiThread {
                viewModel.getCardByUid(uid)
                // Здесь должна быть логика проверки существования карты и сохранения
                val card = Card(uid = uid, type = type, data = data)
                viewModel.insert(card)
                Toast.makeText(this, "Карта сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
