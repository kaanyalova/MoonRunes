package com.kaanb.moonrunes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kaanb.moonrunes.databinding.ActivityAboutBinding
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.goToJmDictButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                "https://www.edrdg.org/jmdict/j_jmdict.html".toUri())

            startActivity(intent)
        }

        binding.goToKanjiDicButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                "https://www.edrdg.org/kanjidic/kanjidic.html".toUri())

            startActivity(intent)
        }
    }
}