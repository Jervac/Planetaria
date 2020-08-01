package entities.worldEnemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import core.Const
import entities.Enemy
import entities.Planet
import entities.worldObjects.Platform
import entities.worldObjects.Rock
import entities.worldObjects.Spike
import entities.worldObjects.SpikeWall
import util.Level

class Goomba(p: Planet, var l: Level, startAngle: Float, right: Boolean) : Enemy(l, p, startAngle, right, 30f, 30f) {

    init {
        sprite = Sprite(Texture(Gdx.files.internal("entities/hero.png")))
        sprite.setSize(size.x * 2, size.y * 2)
        movementVel = .28f

        faceSprite()
    }

    override fun update() {
        movementVel = .36f

        updatePositionOnAngle()

        // position sprite
        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.rotation = rotation
        sprite.setOrigin(poly.originX, poly.originY)

        faceSprite()

        // Turn other way if touch a spike / platform / other enemy
        if (!Const.EDITOR) {
            for (e in l.entities) {
                if (e.javaClass == Spike::class.java || e.javaClass == Platform::class.java || e.javaClass == Enemy::class.java || e.javaClass == Rock::class.java || e.javaClass == Goomba::class.java || e.javaClass == SpikeWall::class.java) {
                    if (e != this && e.alive && Intersector.overlapConvexPolygons(e.poly, this.poly)) {
                        right = !right
                    }
                }
            }
        }

    }

    override fun render() {

        if (!Const.EDITOR)
            if (right)
                rotation -= movementVel
            else
                rotation += movementVel

        renderAll()
    }

    override fun dispose() {}

    override fun bounds(): Rectangle {
        return Rectangle(pos.x - size.x / 2, pos.y - size.y / 2, size.x, size.y)
    }

}