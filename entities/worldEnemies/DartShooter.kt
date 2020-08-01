package entities.worldEnemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Timer
import core.Const
import entities.Enemy
import entities.Entity
import entities.Pellet
import entities.Planet
import util.Level

class DartShooter(p: Planet, l: Level, startRot: Float, right: Boolean, height: Float, shootDelay: Float) : Enemy(l, p, startRot, right, 30f, height) {

    var pellets = ArrayList<Pellet>()

    internal var canShoot = true

    init {
        vi = 0f

        sprite = Sprite(Texture(Gdx.files.internal("entities/tim.png")))
        sprite.setSize(size.x * 2, size.y * 2)

        faceSprite()
        shoot(l, this)

    }

    fun reset() {
        for (p in pellets) {
            p.alive = false
            p.dispose()
        }
        pellets = ArrayList<Pellet>()
    }

    private fun shoot(l: Level, ee: Entity) {
        val lvl = l
        val e = ee
        var delay = shootDelay
        canShoot = true
        // skip timer if debug
        if (Const.EDITOR) {
            delay = 0f
            canShoot = false
        }
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                if (canShoot)
                    pellets.add(Pellet(lvl, p!!, e, true))
                shoot(lvl, e)
            }
        }, delay)
    }

    override fun update() {
        updatePositionOnAngle()

        // position sprite
        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.rotation = rotation
        sprite.setOrigin(poly.originX, poly.originY)

        faceSprite()

        for (p in pellets)
            if (p.alive)
                p.update()

    }

    override fun render() {
        renderAll()

        for (p in pellets)
            if (p.alive)
                p.render()
    }

    override fun dispose() {
        for (p in pellets)
            p.dispose()
        pellets.clear()
    }

    override fun bounds(): Rectangle {
        return Rectangle(pos.x - size.x / 2, pos.y - size.y / 2, size.x, size.y)
    }


}