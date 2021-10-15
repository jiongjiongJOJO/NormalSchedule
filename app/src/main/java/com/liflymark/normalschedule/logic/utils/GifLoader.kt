package com.liflymark.normalschedule.logic.utils

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

object GifLoader {
    @JvmStatic
    @JvmName("create")
    operator fun invoke(context: Context) =
        ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
}