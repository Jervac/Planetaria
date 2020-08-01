package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import core.Const
import util.Gfx
import util.Level

class Pellet : Entity {

    internal var vi = .023f
    internal var h: Entity
    internal var t: Float = 0.toFloat()

    internal var thing: Boolean = false


    internal var l: Level

    constructor(l: Level, p: Planet, h: Entity) {
        this.height = h.height
        this.p = p
        this.h = h
        this.angle = h.angle
        this.l = l

        // make dir left if left
        if (h.right)
            vi = -vi

        size.set(25f, 25f)
    }

    constructor(l: Level, p: Planet, h: Entity, thing: Boolean) {
        this.height = h.height + 20
        this.p = p
        this.h = h
        this.rotation = h.rotation
        this.thing = thing
        this.l = l
        this.right = h.right

        size.set(14f, 14f)

        movementVel = 2f


    }

    override fun update() {
        pos.y = p!!.pos.y + (Math.sin(angle.toDouble()) * height).toFloat()
        pos.x = p!!.pos.x + (Math.cos(angle.toDouble()) * height).toFloat()

        poly.vertices = (floatArrayOf(pos.x - size.x, pos.y - size.y, pos.x + size.x, pos.y - size.y, pos.x + size.x, pos.y + size.y, pos.x - size.x, pos.y + size.y))
        poly.rotation = rotation

        // collision
        for (e in l.entities) {
            if (alive && e.alive && e != h && e != p && Intersector.overlapConvexPolygons(poly, e.poly)) {
                alive = false
                break
            }
        }
//
        if (alive && Intersector.overlapConvexPolygons(l.hero.poly, poly)) {
            alive = false
            l.hero.die()
        }
//

        if (!Const.EDITOR)
            if (right)
                rotation -= movementVel
            else
                rotation += movementVel
    }

    private fun onGround(): Boolean {
        return height <= p!!.radius + 5
    }


    override fun render() {
        Gfx.sr.color = Color.BROWN
        Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
        Gfx.sr.polygon(poly.transformedVertices)
        Gfx.sr.end()
    }

    override fun dispose() {

    }

}