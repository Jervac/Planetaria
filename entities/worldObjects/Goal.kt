package entities.worldObjects

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import core.Const
import entities.Entity
import util.Gfx
import util.Inputer

class Goal(x: Float, y: Float, rot: Float, exitLevelPath: String) : Entity() {

    var exitLevel: String
    internal var top: Float = 0.toFloat()

    init {
        this.angle = angle
        this.rotation = rot
        size.set(60f, 80f)
        pos.set(x, y)

        poly = Polygon(floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
        poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2)
        poly.rotation = rotation

        sprite = Sprite(Texture("entities/door3.png"))
        sprite.flip(false, true)
//        sprite.color  = Color(114 / 255f, 64 / 255f, 48 / 255f, 1f)


        if (exitLevelPath == "")
            exitLevel = "levels/" + Const.CURRENT_WORLD + "/" + Const.CURRENT_WORLD + "-" + (Const.LEVELID + 1) + ".lvl"
        else
            exitLevel = exitLevelPath
    }

    override fun update() {
        if (controlable) {
            poly.vertices = (floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
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
                rotation = rotation + Const.rotateSpeed
            if (Inputer.pressedKey(Input.Keys.Q))
                rotation = rotation - Const.rotateSpeed
        }
    }

    override fun render() {

        //        Gfx.sr.setColor(Color.YELLOW);
        //        Gfx.sr.begin(ShapeRenderer.ShapeType.Line);
        //        Gfx.sr.polygon(poly.getTransformedVertices());
        //        Gfx.sr.end();

        sprite.setOriginCenter()
        sprite.rotation = rotation
        sprite.setSize(size.x, size.y)
        sprite.setPosition(pos.x, pos.y)

        Gfx.sb.projectionMatrix = Gfx.cam.combined
        Gfx.sb.begin()
        sprite.draw(Gfx.sb)
        Gfx.sb.end()

        //        Gfx.fillRect(poly.getOriginX(), top, 10, 10);
    }

    override fun dispose() {
        //        System.out.println("\nSpike [" + p.spikes.indexOf(this) + "]\n-------");
        //        System.out.println("Angle: " + angle);
        //        System.out.println("Height: " + -(p.radius - height));
        //        System.out.println("Rotation: " + rotation);
    }

}
