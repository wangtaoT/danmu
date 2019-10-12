package net.codeages.live.danmaku.parser

import net.codeages.live.danmaku.model.Danmaku
import net.codeages.live.danmaku.util.nextInt
import java.util.*

class TextParser {

    fun parse(source: String) : Danmaku {
        return Danmaku(source.replace('\n', ' '), 14, Random().nextInt(3, 6))
    }
}