package com.codyzen.spriterunner

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.min


class BitMapUtils {
    companion object {

        @JvmStatic fun decodeSampledBitmapFromResource(
            res: Resources?,
            resId: Int,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, resId, options)
        }

        @JvmStatic private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            // Raw dimensions of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            // Determine if the image is smaller or larger than the requested dimensions.
            if (height > reqHeight || width > reqWidth) {
                // Decide how much to reduce the sample size.
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = min(heightRatio.toDouble(), widthRatio.toDouble()).toInt()
            }
            return inSampleSize
        }

    }

}