package com.altomedia.altotap.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager private constructor(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var coinTapSoundId: Int = 0
    private var upgradeSuccessSoundId: Int = 0
    private var isLoaded = false
    var isSoundEnabled: Boolean = true

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attributes)
                .build()

            val coinFile = createCoinTapWavFile(context)
            val upgradeFile = createUpgradeSuccessWavFile(context)

            coinTapSoundId = soundPool?.load(coinFile.absolutePath, 1) ?: 0
            upgradeSuccessSoundId = soundPool?.load(upgradeFile.absolutePath, 1) ?: 0
            isLoaded = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playCoinTap() {
        if (!isSoundEnabled || !isLoaded) return
        soundPool?.play(coinTapSoundId, 0.85f, 0.85f, 1, 0, 1.0f)
    }

    fun playUpgradeSuccess() {
        if (!isSoundEnabled || !isLoaded) return
        soundPool?.play(upgradeSuccessSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun createCoinTapWavFile(context: Context): File {
            val file = File(context.cacheDir, "coin_tap_fx.wav")
            if (file.exists() && file.length() > 0) return file

            val sampleRate = 44100
            val durationSeconds = 0.08
            val numSamples = (sampleRate * durationSeconds).toInt()
            val pcmData = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Bright high metallic tone 2400Hz rising to 3200Hz
                val freq = 2400.0 + (t / durationSeconds) * 800.0
                val decay = exp(-t * 50.0) // fast decay
                val wave1 = sin(2.0 * PI * freq * t)
                val wave2 = sin(2.0 * PI * freq * 1.8 * t) * 0.45
                val sampleValue = ((wave1 + wave2) * 0.5 * decay * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcmData[i] = sampleValue.toShort()
            }

            writeWavFile(file, pcmData, sampleRate)
            return file
        }

        private fun createUpgradeSuccessWavFile(context: Context): File {
            val file = File(context.cacheDir, "upgrade_success_fx.wav")
            if (file.exists() && file.length() > 0) return file

            val sampleRate = 44100
            val durationSeconds = 0.38
            val numSamples = (sampleRate * durationSeconds).toInt()
            val pcmData = ShortArray(numSamples)

            // Triumphant 4-note ascending chord fanfare
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val noteDuration = durationSeconds / notes.size

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val noteIndex = (t / noteDuration).toInt().coerceIn(0, notes.size - 1)
                val noteFreq = notes[noteIndex]
                val tInNote = t - (noteIndex * noteDuration)

                val decay = exp(-tInNote * 10.0)
                val wave = sin(2.0 * PI * noteFreq * tInNote) + 0.35 * sin(2.0 * PI * noteFreq * 2.0 * tInNote)
                val sampleValue = (wave * 0.45 * decay * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcmData[i] = sampleValue.toShort()
            }

            writeWavFile(file, pcmData, sampleRate)
            return file
        }

        private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
            val fos = FileOutputStream(file)
            val byteRate = sampleRate * 2
            val dataSize = pcmData.size * 2
            val totalSize = 36 + dataSize

            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalSize and 0xff).toByte()
            header[5] = ((totalSize shr 8) and 0xff).toByte()
            header[6] = ((totalSize shr 16) and 0xff).toByte()
            header[7] = ((totalSize shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1
            header[21] = 0
            header[22] = 1
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            header[32] = 2
            header[33] = 0
            header[34] = 16
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (dataSize and 0xff).toByte()
            header[41] = ((dataSize shr 8) and 0xff).toByte()
            header[42] = ((dataSize shr 16) and 0xff).toByte()
            header[43] = ((dataSize shr 24) and 0xff).toByte()

            fos.write(header)

            val pcmBytes = ByteArray(dataSize)
            for (i in pcmData.indices) {
                val val16 = pcmData[i].toInt()
                pcmBytes[i * 2] = (val16 and 0xff).toByte()
                pcmBytes[i * 2 + 1] = ((val16 shr 8) and 0xff).toByte()
            }
            fos.write(pcmBytes)
            fos.close()
        }
    }
}
