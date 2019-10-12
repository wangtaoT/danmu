package net.codeages.live.danmaku.sprite

import android.content.Context
import android.opengl.GLES20
import net.codeages.live.danmaku.R
import net.codeages.live.danmaku.util.ShaderHelper
import net.codeages.live.danmaku.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.graphics.*
import android.opengl.GLES20.*
import android.opengl.Matrix
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import net.codeages.live.danmaku.DanmakuEnv
import net.codeages.live.danmaku.model.Danmaku
import net.codeages.live.danmaku.util.Geometry


class DanmakuSprite(
        context: Context,
        private val position: PointF,
        private val danmaku: Danmaku) {

    companion object {
        const val COORDS_PER_VERTEX = 3
    }

    private var shaderProgram: Int = -1
    private var aPositionLocation: Int = -1
    private var aTextureCoordinatesLocation: Int = -1
    private var uTextureUnitLocation: Int = -1

    private var uProjectionMatrix: Int = -1
    private var uModelMatrix: Int = -1

    private val vertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer

    private val boringLayout by lazy {
        val boring = BoringLayout.isBoring(danmaku.text, p)
        BoringLayout.make(danmaku.text, p, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, boring, true)
    }

    val fontBitmap: Bitmap by lazy {

        val point = PointF(0.0f, 0.0f)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.TRANSPARENT)
        canvas.translate(point.x, point.y)
        boringLayout.draw(canvas)

        bitmap
    }

    var width = 0
    var height = 0
    private val p = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val textCoords: FloatArray

    private val textureCoords = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    )

    private val vertexCount: Int
        get() = textCoords.count() / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var modelMatrix = FloatArray(4 * 4) { 0.0f }
    private var translateVector = Geometry.Vector(0f, 0f, 0f)
    private var directionVector = Geometry.Vector(-DanmakuEnv.border.width().toFloat() / danmaku.destroyTime, 0.0f, 0.0f)

    init {

        Matrix.setIdentityM(modelMatrix, 0)

        p.color = Color.WHITE
        p.textAlign = Paint.Align.LEFT
        p.setShadowLayer(3.0f, 0.0f, 0.0f, Color.BLACK)


        val scaledSizeInPixels = danmaku.textSpSize * context.resources.displayMetrics.scaledDensity
        p.textSize = scaledSizeInPixels

        width = p.measureText(danmaku.text).toInt()
        height = boringLayout.height

        textCoords = floatArrayOf(
                position.x, position.y - height, 0.0f, // bottom left
                position.x + width, position.y - height, 0.0f, // bottom right
                position.x, position.y, 0.0f, // top left
                position.x + width, position.y, 0.0f
        )

        shaderProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.danmaku_vertex),
                TextResourceReader.readTextFileFromResource(context, R.raw.danmaku_fragment)
        )

        val bb = ByteBuffer.allocateDirect(textCoords.count() * 4)
        bb.order(ByteOrder.nativeOrder())

        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(textCoords)
        vertexBuffer.position(0)

        val textureByteBuffer = ByteBuffer.allocateDirect(textureCoords.count() * 4)
        textureByteBuffer.order(ByteOrder.nativeOrder())

        textureBuffer = textureByteBuffer.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        aPositionLocation = glGetAttribLocation(shaderProgram, "a_Position")

        uProjectionMatrix = glGetUniformLocation(shaderProgram, "u_ProjectionMatrix")
        uModelMatrix = glGetUniformLocation(shaderProgram, "u_ModelMatrix")

        aTextureCoordinatesLocation = glGetAttribLocation(shaderProgram, "a_TextureCoordinates")
        uTextureUnitLocation = glGetUniformLocation(shaderProgram, "u_TextureUnit")
    }

    fun useProgram() {
        glUseProgram(shaderProgram)
    }

    fun setUniforms(projectionMatrix: FloatArray, deltaTime: Float, textureId: Int) {
        glUniformMatrix4fv(uProjectionMatrix, 1, false, projectionMatrix, 0)

        Matrix.setIdentityM(modelMatrix, 0)
        translateVector.plus(directionVector.scale(deltaTime))
        Matrix.translateM(modelMatrix, 0, translateVector.x, translateVector.y, translateVector.z)
        glUniformMatrix4fv(uModelMatrix, 1, false, modelMatrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
    }

    fun draw() {

        glEnableVertexAttribArray(aPositionLocation)
        glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GL_FLOAT, false, vertexStride, vertexBuffer)

        glEnableVertexAttribArray(aTextureCoordinatesLocation)
        glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL_FLOAT, false, 2 * 4, textureBuffer)

        glUniform1i(uTextureUnitLocation, 0)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, vertexCount)

        glDisableVertexAttribArray(aPositionLocation)
        glDisableVertexAttribArray(aTextureCoordinatesLocation)
    }

    fun isVisible(): Boolean {
        return position.x + translateVector.x + width >= DanmakuEnv.border.left
    }
}