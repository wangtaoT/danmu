package net.codeages.live.danmaku

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import net.codeages.live.danmaku.model.Danmaku
import net.codeages.live.danmaku.sprite.DanmakuSprite
import net.codeages.live.danmaku.sprite.DanmakuSpriteSpawn
import net.codeages.live.danmaku.util.TextureHelper
import net.codeages.live.danmaku.util.nextInt
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class DanmakuRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private var textureId: Int = -1

    private var startTime = SystemClock.uptimeMillis()

    private var sprites = mutableListOf<DanmakuSprite>()
    private val spawns: List<DanmakuSpriteSpawn>
    private val spawnCount = 24

    init {
        spawns = List(spawnCount) { index ->
            DanmakuSpriteSpawn(context, PointF(0.0f, 0.0f))
        }
    }

    override fun onDrawFrame(p0: GL10?) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val updateTime = SystemClock.uptimeMillis()
        val deltaTime = (updateTime - startTime).toFloat() / 1000
        startTime = updateTime

        drawDanmaku(deltaTime)
    }

    override fun onSurfaceChanged(gl10: GL10, width: Int, height: Int) {

        DanmakuEnv.border.left = 0
        DanmakuEnv.border.top = 0
        DanmakuEnv.border.right = width
        DanmakuEnv.border.bottom = height

        glViewport(0, 0, width, height)
        Matrix.orthoM(projectionMatrix, 0,
                0.0f,
                width.toFloat(),
                0.0f,
                height.toFloat(), 0.0f, 10.0f)

        startTime = SystemClock.uptimeMillis()

        val scopeHeight = height * 3 / 4
        val safeHeight = height - scopeHeight
        val spawnHeight = scopeHeight / spawnCount

        spawns.forEachIndexed { index, spawn ->
            spawn.position.x = DanmakuEnv.border.width().toFloat()
            spawn.position.y = spawnHeight.toFloat() * (spawnCount - index) + safeHeight
        }
    }

    override fun onSurfaceCreated(gl10: GL10, eglConfig: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // 启用混合模式
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun addDanmaku(danmaku: Danmaku) {
        val randomIndex = Random().nextInt(0, spawnCount - 1)
        sprites.addAll(spawns[randomIndex].spawn(danmaku))
    }

    fun clearAllDanmaku() {
        sprites.clear()
    }

    fun release() {
        // bitmap 在Android 2.3后 不用去手动调用 recycle
    }

    private fun drawDanmaku(deltaTime: Float) {

        val it = sprites.iterator()

        while (it.hasNext()) {
            val danmakuSprite = it.next()

            danmakuSprite.useProgram()

            if (textureId == -1) {
                textureId = TextureHelper.loadTexture(danmakuSprite.fontBitmap)
            } else {
                TextureHelper.setTexutreData(textureId, danmakuSprite.fontBitmap)
            }

            danmakuSprite.setUniforms(projectionMatrix, deltaTime, textureId)
            danmakuSprite.draw()

            if (!danmakuSprite.isVisible()) {
                it.remove()
            }
        }
    }
}