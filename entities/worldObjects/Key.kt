package entities.worldObjects

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import core.Const
import entities.Entity
import util.Gfx
import util.Inputer

class Key(x: Float, y: Float, var rot: Float) : Entity() {

    internal var top: Float = 0.toFloat()

    init {
        size.set(80f, 10f)
        //allignInWorld(x, y);
        pos.set(x, y)
        poly = Polygon(floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
        poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2)
        poly.rotation = rot
    }

    override fun update() {
        if (controlable) {
            poly = Polygon(floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
            poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2)
            poly.rotation = rotation

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

    override fun render() {
        Gfx.sr.color = Color.BLUE

        Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
        Gfx.sr.polygon(poly.transformedVertices)
        Gfx.sr.end()

    }

    override fun dispose() {

    }

}
