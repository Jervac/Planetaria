package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import core.Const
import util.Gfx

class Planet(x: Float, y: Float, radius: Float) : Entity() {

    var radius = Const.DEFAULTL_RADIUS.toFloat()
    var body: Circle
    internal var gravity = Const.DEFAULT_GRAVITY



    init {
        this.radius = radius
        pos = Vector2(x, y)

        body = Circle(x, y, radius)

        sprite = Sprite(Texture("entities/nature/planet_0.png"))

    }

    override fun update() {
        body.setPosition(pos.x, pos.y)
    }

    override fun render() {
        Gfx.setColor(Color(29 / 255f, 223 / 255f, 15 / 255f, 1f))
        Gfx.fillCircle(pos.x, pos.y, radius)
        Gfx.drawSprite(sprite, pos.x - radius, pos.y - radius, radius * 2, radius * 2)

    }

    override fun dispose() {

    }
}
