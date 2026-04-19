// app/src/main/java/com/example/nfccardmanager/ui/MainActivity.kt
package com.example.nfccardmanager.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nfccardmanager.R
import com.example.nfccardmanager.databinding.ActivityMainBinding
import com.example.nfccardmanager.viewmodel.CardViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CardViewModel
    private lateinit var adapter: CardAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Разрешение необходимо для работы NFC", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CardViewModel::class.java]
        
        setupRecyclerView()
        checkPermissions()
        checkNfcSupport()
        
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = CardAdapter { card ->
            val intent = Intent(this, CardDetailActivity::class.java)
            intent.putExtra("CARD_ID", card.uid)
            startActivity(intent)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        
        viewModel.allCards.observe(this) { cards ->
            adapter.submitList(cards)
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.NFC
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже предоставлено
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.NFC)
            }
        }
    }

    private fun checkNfcSupport() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Устройство не поддерживает NFC", Toast.LENGTH_LONG).show()
            finish()
        } else if (!nfcAdapter.isEnabled) {
            Toast.makeText(this, "Включите NFC в настройках", Toast.LENGTH_LONG).show()
        }
    }
}
