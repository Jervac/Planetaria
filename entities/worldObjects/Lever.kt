package entities.worldObjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import core.Const
import entities.Entity
import util.Gfx
import util.Inputer

class Lever(x: Float, y: Float, rot: Float, id: Int) : Entity() {

    var on = false

    var onTexture: Texture
    var offTexture: Texture

    var onSound: Sound
    var offSound: Sound


    init {
        this.id = id
        this.angle = angle
        this.rotation = rot
        size.set(60f, 60f)
        pos.set(x, y)

        poly = Polygon(floatArrayOf(pos.x, pos.y, pos.x + size.x, pos.y, pos.x + size.x, pos.y + size.y, pos.x, pos.y + size.y))
        poly.setOrigin(pos.x + size.x / 2, pos.y + size.y / 2)
        poly.rotation = rotation

        onSound = Gdx.audio.newSound(Gdx.files.internal("sounds/lever_on.wav"))
        offSound = Gdx.audio.newSound(Gdx.files.internal("sounds/lever_off.wav"))

        onTexture = Texture(Gdx.files.internal("entities/switch1.png"))
        offTexture = Texture(Gdx.files.internal("entities/switch2.png"))

        sprite = Sprite(offTexture)
        sprite.flip(false, true)
    }

    fun pull() {
        on = !on

        // activates spike walls
        for (e in Const.currentLevel!!.entities) {
            if (e.javaClass == SpikeWall::class.java) {
                var sw = e as SpikeWall
                if (sw.id == this.id && sw.leverControled) {
                    sw.activate()
                }
            }
        }

        if (!on) {
            sprite.texture = offTexture
            onSound.play()

        } else {
            sprite.texture = onTexture
            offSound.play()
        }

        //...//
        // TODO: make it activate entities with same id
        //...//
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

    }

    override fun dispose() {
        onTexture.dispose()
        offTexture.dispose()
    }

}
