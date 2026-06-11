package com.example.mayatimegate.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.mayatimegate.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool

    private val soundMap = mutableMapOf<String, Int>()


    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap["success-in"] = soundPool.load(context, R.raw.success, 1)
        soundMap["success-out"] = soundPool.load(context, R.raw.check_out, 1)
        soundMap["error"] = soundPool.load(context, R.raw.error, 1)


    }

    fun play(name: String) {
        val soundId = soundMap[name] ?: return

        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
}