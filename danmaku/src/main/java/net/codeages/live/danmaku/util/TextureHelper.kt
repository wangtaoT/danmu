package net.codeages.live.danmaku.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteOrder.nativeOrder
import java.nio.ByteBuffer


/**
 * Created by retamia on 2017/11/8.
 */

object TextureHelper {
    private val TAG = "TextureHelper"

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureIds = IntArray(1)

        glGenTextures(1, textureIds, 0)

        if (textureIds[0] == 0) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")

            return 0
        }

        val options = BitmapFactory.Options()

        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {

            Log.w(TAG, "Resource Id $resourceId could not be decoded")

            glDeleteTextures(1, textureIds, 0)

            return 0
        }

        glBindTexture(GL_TEXTURE_2D, textureIds[0])

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)

        return textureIds[0]
    }

    fun loadTexture(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)

        glGenTextures(1, textureIds, 0)

        if (textureIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.")
            return 0
        }

        setTexutreData(textureIds[0], bitmap)

        return textureIds[0]
    }

    fun setTexutreData(textureId: Int, bitmap: Bitmap) {
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        val byteCount = bitmap.rowBytes * bitmap.height

        val byteBuffer = ByteBuffer.allocate(byteCount)
        byteBuffer.order(nativeOrder())
        bitmap.copyPixelsToBuffer(byteBuffer)
        byteBuffer.position(0)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.width, bitmap.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer)

        glBindTexture(GL_TEXTURE_2D, 0)
    }
}
