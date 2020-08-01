package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import core.Const
import entities.worldEnemies.Goomba
import entities.worldObjects.Platform
import util.Gfx
import util.Inputer
import util.Level

abstract class Enemy(internal var level: Level, p: Planet, rot: Float, right: Boolean, _size: Float, h: Float) : Entity() {

    var initialRotation: Float
    var vi: Float = 0.toFloat()
    var a: Float = 0.toFloat()
    var t: Float = 0.toFloat()

    internal var jumping = false
    internal var falling = false
    internal var onPlatform = false
    internal var jumpVel = 5.4f
    internal var minVel = -3f

    var initialRight: Boolean

    init {
        this.p = p
        this.right = right
        initialRight = right
        size.set(_size, _size)
        height = p.radius + h
        rotation = rot
        initialRotation = rot

        // things positioned like this because they used to be circles
        poly = Polygon(floatArrayOf(pos.x - size.x, pos.y - size.y, pos.x + size.x, pos.y - size.y, pos.x + size.x, pos.y + size.y, pos.x - size.x, pos.y + size.y))
        poly.setOrigin(0f, 0f)
        poly.rotation = rotation

        // since angle always == 0 (for positioning for some reason) it flips the texture 90 degrees but puts player open planet perfectly
        sprite = Sprite(Texture(Gdx.files.internal("badlogic.jpg")))
        sprite.color = Color.MAROON
        sprite.setSize(size.x * 2, size.y * 2)

        pos.y = p.pos.y + (Math.sin(angle.toDouble()) * height).toFloat()
        pos.x = p.pos.x + (Math.cos(angle.toDouble()) * height).toFloat()

        movementVel = 2f
    }

    protected fun updatePositionOnAngle() {
        if (!Const.DEBUG) {

        }

        if (controlable && Const.DEBUG) {
            if (Inputer.pressedKey(Input.Keys.A))
                rotation += movementVel
            if (Inputer.pressedKey(Input.Keys.D))
                rotation -= movementVel
            if (Inputer.pressedKey(Input.Keys.W))
                pos.y += movementVel
            if (Inputer.pressedKey(Input.Keys.S))
                pos.y += movementVel
        }

        pos.y = p!!.pos.y + (Math.sin(angle.toDouble()) * height).toFloat()
        pos.x = p!!.pos.x + (Math.cos(angle.toDouble()) * height).toFloat()

        poly.vertices = (floatArrayOf(pos.x - size.x, pos.y - size.y, pos.x + size.x, pos.y - size.y, pos.x + size.x, pos.y + size.y, pos.x - size.x, pos.y + size.y))
        poly.rotation = rotation

        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.rotation = rotation
        sprite.setOrigin(poly.originX, poly.originY)

    }

    protected fun renderAll() {
        if (Const.DEBUG) {
            Gfx.sr.color = Color.ORANGE
            Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
            Gfx.sr.polygon(poly.transformedVertices)
            Gfx.sr.end()
        } else {
            if (invincible)
                sprite.color = Color.SKY
            else
                sprite.color = Color.WHITE

            Gfx.sb.projectionMatrix = Gfx.cam.combined
            Gfx.sb.begin()
            sprite.draw(Gfx.sb)
            Gfx.sb.end()
        }
    }

    protected fun renderPoly() {

    }

    protected fun onGround(): Boolean {
        return height <= p!!.radius + 5
    }

    protected fun jump() {
        vi = jumpVel //vi
        jumping = true
        falling = false
    }

    protected fun jumpLogic() {

        for (e in level.entities) {
            if (e.alive && e.javaClass == Platform::class.java && Intersector.overlapConvexPolygons(e.poly, poly)) {
                onPlatform = true
                break
            } else
                onPlatform = false
        }


        if (!jumping) {
        } else {// start going down if not holding
            if (!onGround() && !onPlatform && !falling && vi > 0) {
                falling = true
                vi = 0f
            }
        }

        if (jumping || !onPlatform) {
            height = height + vi
            t += 0.009f

            // back open planet
            if (onGround() || onPlatform) {
                if (onGround())
                    height = p!!.radius + 5
                jumping = false

                t = 0f
            }

            // if not falling at max vel
            if (vi > minVel) {
                vi += a * t
            }
        }
    }

    override fun save() {
        if (javaClass != Goomba::class.java) {
            try {
                var saveName = this.javaClass.toString()
                saveName = saveName.replace("class entities.", "").toLowerCase()
                saveData = saveName + ":" + pos.x + ":" + pos.y
                println(this.javaClass)
            } catch (e: Exception) {
            }

        }
    }
}