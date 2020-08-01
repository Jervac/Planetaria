package entities.worldObjects

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import core.Const
import entities.Entity
import util.Gfx
import util.Inputer

class Platform(x: Float, y: Float, var rot: Float) : Entity() {
    internal var top: Float = 0.toFloat()

    init {
        rotation = rot
        size.set(80f, -4f)
        pos.set(x, y)

        poly = Polygon(floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
        //        poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2);
        poly.setOrigin(0f, 0f)
        poly.rotation = rot

        sprite = Sprite(Texture("px.png"))
        sprite.color = Color(0 / 255f, 8 / 255f, 23 / 255f, 1f)

        sprite.setSize(size.x, size.y * 4)
        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.rotation = rotation
    }

    override fun update() {
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

        poly.vertices = (floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
        //            poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2);
        poly.setOrigin(0f, 0f)
        poly.rotation = rotation

        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.rotation = rotation

    }

    override fun render() {
        if (Const.DEBUG) {
            Gfx.sr.color = Color.WHITE
            Gfx.sr.projectionMatrix = Gfx.cam.combined
            Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
            Gfx.sr.polygon(poly.transformedVertices)
            Gfx.sr.end()
        }

        Gfx.sb.begin()
        sprite.draw(Gfx.sb)
        Gfx.sb.end()

    }

    override fun dispose() {
        sprite.texture.dispose()
    }

}
