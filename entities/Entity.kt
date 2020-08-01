package entities

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import core.Const
import util.Inputer


abstract class Entity {

    var shootDelay = 1.79f
    var hp = 1
    var id = 0
    var leverControled = false
    var depth = 1
    var right = true
    var color = Color.WHITE
    var controlable = false
    var poly = Polygon(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f))
    var saveData: String? = null
    var alive = true
    var visible = true

    // status effects
    var invincible: Boolean = false

    // movement
    var vel: Float = 0.toFloat()
    var max_vel: Float = 0.toFloat()
    var xVel = .01f
    var rotation = 0f
    var movementVel = .009f
    var largerSize = 80f
    var angle: Float = 0.toFloat()
    var height: Float = 0.toFloat()

    var pos = Vector2(0f, 0f)
    var size = Vector2(0f, 0f)

    lateinit var sprite: Sprite

    var p: Planet? = null


    abstract fun render()

    abstract fun update()

    abstract fun dispose()


    open fun save() {}

    fun centerPos(): Vector2 {
        return Vector2(pos.x + size.x / 2, pos.y + size.y / 2)
    }

    open fun bounds(): Rectangle {
        return Rectangle(pos.x, pos.y, size.x, size.y)
    }

    fun editorControl() {
        if (controlable) {
            if (Inputer.pressedKey(Input.Keys.W))
                pos.y += Const.heightenSpeed
            if (Inputer.pressedKey(Input.Keys.S))
                pos.y -= Const.heightenSpeed
            if (Inputer.pressedKey(Input.Keys.A))
                pos.x -= Const.heightenSpeed
            if (Inputer.pressedKey(Input.Keys.D))
                pos.x += Const.heightenSpeed

            if (Inputer.pressedKey(Input.Keys.E))
                rotation += Const.rotateSpeed
            if (Inputer.pressedKey(Input.Keys.Q))
                rotation -= Const.rotateSpeed
        }
    }

    fun allignInWorld(x: Float, y: Float) {
        pos.x = Const.WIDTH / 2 + x
        pos.y = Const.HEIGHT / 2 + y
    }

    fun faceSprite() {
        // sprite face direction moving in
        if (right)
            sprite.setFlip(false, false)
        else
            sprite.setFlip(false, true)
    }

}

