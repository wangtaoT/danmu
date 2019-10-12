package net.codeages.live.danmaku.util

import android.util.Log

import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.GL_VALIDATE_STATUS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glGetProgramiv
import android.opengl.GLES20.glGetShaderiv
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glValidateProgram

/**
 * Created by retamia on 2017/11/7.
 */

object ShaderHelper {
    private val TAG = "ShaderHelper"

    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode)
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = glCreateProgram()

        if (programId == 0) {

            Log.w(TAG, "Could not create new program")


            return 0
        }

        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)

        glLinkProgram(programId)

        val linkStatus = IntArray(1)
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0)


        if (linkStatus[0] == 0) {
            glDeleteShader(programId)

            Log.w(TAG, "Linking of program failed.")


            return 0
        }

        return programId
    }

    fun validateProgram(programId: Int): Boolean {
        glValidateProgram(programId)

        val validateStatus = IntArray(1)
        glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0)

        return validateStatus[0] != 0
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = glCreateShader(type)

        if (shaderObjectId == 0) {
            Log.w(TAG, "Cloud not create new shader.")

            return 0
        }

        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)

        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)

            Log.w(TAG, "compilation of shader failed")

            return 0
        }

        return shaderObjectId
    }

    fun buildProgram(vertexCode: String, fragmentCode: String): Int {
        val program: Int

        val vertexShader = compileVertexShader(vertexCode)
        val fragmentShader = compileFragmentShader(fragmentCode)

        program = linkProgram(vertexShader, fragmentShader)

        validateProgram(program)

        return program
    }
}