package com.example.anunciorecompensado

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {

    private var rewardedAd: RewardedAd? = null
    private val adUnitIdTest = "ca-app-pub-3940256099942544/5224354917"

    private var contadorRecompensas = 0

    private lateinit var tvEstado: TextView
    private lateinit var tvRecompensas: TextView
    private lateinit var btnVerAnuncio: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEstado = findViewById(R.id.tvEstado)
        tvRecompensas = findViewById(R.id.tvRecompensas)
        btnVerAnuncio = findViewById(R.id.btnVerAnuncio)

        // Inicializar SDK de AdMob
        MobileAds.initialize(this) {
            tvEstado.text = "SDK inicializado. Cargando anuncio..."
            cargarAnuncio()
        }

        btnVerAnuncio.setOnClickListener {
            mostrarAnuncio()
        }
    }

    private fun cargarAnuncio() {
        btnVerAnuncio.isEnabled = false
        tvEstado.text = "Cargando anuncio..."

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, adUnitIdTest, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                tvEstado.text = "¡Anuncio listo para ver!"
                btnVerAnuncio.isEnabled = true

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        rewardedAd = null
                        tvEstado.text = "Anuncio cerrado. Cargando siguiente..."
                        cargarAnuncio()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        rewardedAd = null
                        tvEstado.text = "Error al mostrar anuncio"
                        cargarAnuncio()
                    }
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
                tvEstado.text = "Error al cargar: ${loadAdError.message}"
                btnVerAnuncio.isEnabled = false
            }
        })
    }

    private fun mostrarAnuncio() {
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem ->
                // ¡Recompensa otorgada al usuario!
                contadorRecompensas++
                tvRecompensas.text = "Recompensas obtenidas: $contadorRecompensas"
                Toast.makeText(this, "¡Recompensa ganada: ${rewardItem.amount} ${rewardItem.type}!", Toast.LENGTH_LONG).show()
            }
        } ?: run {
            Toast.makeText(this, "El anuncio aún no está listo", Toast.LENGTH_SHORT).show()
        }
    }
}
