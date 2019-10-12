package net.codeages.live.danmaku.sprite

import android.content.Context
import android.graphics.PointF
import net.codeages.live.danmaku.model.Danmaku

class DanmakuSpriteSpawn {

    val position: PointF
    private val context: Context

    constructor(context: Context, position: PointF) {
        this.position = position
        this.context = context
    }

    fun spawn(danmaku: Danmaku) : List<DanmakuSprite> {
        val list = mutableListOf<DanmakuSprite>()

        val count = danmaku.text.count() / 50
        var width = 0
        IntRange(0, count).forEach { it
            val text = if (it >= count) danmaku.text.substring(it * 50) else danmaku.text.substring(it * 50, (it + 1) * 50)
            val splitDanmaku = Danmaku(text, danmaku.textSpSize, danmaku.destroyTime)
            val danmakuSprite = DanmakuSprite(context, PointF(position.x + width, position.y), splitDanmaku)
            list.add(danmakuSprite)

            width += danmakuSprite.width
        }

        return list.toList()
    }
}