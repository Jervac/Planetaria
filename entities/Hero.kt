package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import core.Const
import core.Core
import entities.worldEnemies.Goomba
import entities.worldObjects.*
import util.Collision
import util.Gfx
import util.Inputer
import util.Level

class Hero(internal var level: Level, p: Planet, var initialHeight: Float) : Entity() {

    var initialRotation: Float = 0.toFloat()
    var xVelMax = .26f
    var vi: Float = 0.toFloat()
    var a: Float = 0.toFloat()
    var t: Float = 0.toFloat()

    internal var step: Music
    internal var jump: Music

    internal var death: Sound
    internal var land: Sound

    internal var onPlatform = false
    internal var jumping = false
    internal var falling = false
    internal var inWater = false    // TODO: make inwater be a boolean function
    private var jumpVel = 6.9f
    private var minVel = -5.7f        // falling velocity
    private val waterXVel = .011f

    var landedOnGround = true

    var feet: Polygon

    var nextJumpFeet: Polygon // checks if jump pressed  little before feet touch ground
    var jumpReady = false
    var pressedJump = false


    init {
        this.p = p
        size.set(28f, 28f)
        height = initialHeight
        a = p.gravity
        rotation = Const.START_ROTATION
//        initialRotation = angle
        initialRotation = rotation

        step = Gdx.audio.newMusic(Gdx.files.internal("footsteps.wav"))
        jump = Gdx.audio.newMusic(Gdx.files.internal("player_jump3.wav"))
        death = Gdx.audio.newSound(Gdx.files.internal("death.wav"))
        land = Gdx.audio.newSound(Gdx.files.internal("player_lands2.wav"))

        poly = Polygon(floatArrayOf(pos.x - size.x, pos.y - size.y, pos.x + size.x, pos.y - size.y, pos.x + size.x, pos.y + size.y, pos.x - size.x, pos.y + size.y))
        poly.setOrigin(p!!.pos.x + p!!.size.x / 2, p!!.pos.y + p!!.size.y / 2)
        poly.rotation = rotation

        feet = Polygon(floatArrayOf(pos.x - size.x, pos.y - size.y - 4, pos.x + size.x, pos.y - size.y - 4, pos.x + size.x, pos.y - size.y, pos.x - size.x, pos.y - size.y))
        feet.setOrigin(p!!.pos.x + p!!.size.x / 2, p!!.pos.y + p!!.size.y / 2)
        feet.rotation = rotation

        nextJumpFeet = Polygon(floatArrayOf(pos.x - size.x, pos.y - size.y - 12, pos.x + size.x, pos.y - size.y - 12, pos.x + size.x, pos.y - size.y, pos.x - size.x, pos.y - size.y))
        nextJumpFeet.setOrigin(p.pos.x, p.pos.y)
        nextJumpFeet.rotation = rotation

        sprite = Sprite(Texture(Gdx.files.internal("entities/hero.png")))
        sprite.setSize(size.x * 2f, size.y * 2f)
    }

    override fun update() {
        a = -2.0f
        xVelMax = .7f
        jumpVel = 5.99f
        minVel = -5.2f

        pos.y = p!!.pos.y + (Math.sin(angle.toDouble()) * height).toFloat()
        pos.x = p!!.pos.x + (Math.cos(angle.toDouble()) * height).toFloat()

        poly.vertices = (floatArrayOf(pos.x - size.x, pos.y - size.y, pos.x + size.x, pos.y - size.y, pos.x + size.x, pos.y + size.y, pos.x - size.x, pos.y + size.y))
        poly.rotation = rotation
        poly.setOrigin(p!!.pos.x, p!!.pos.y)


        feet.vertices = (floatArrayOf(pos.x - size.x, pos.y - size.y,
                pos.x - size.x - 4, pos.y - size.y,
                pos.x - size.x - 4, pos.y + size.y,
                pos.x - size.x, pos.y + size.y))
        feet.rotation = rotation
        feet.setOrigin(poly.originX, poly.originY)

        nextJumpFeet.vertices = (floatArrayOf(// 10 allows player to jump a little after off platform
                pos.x - size.x, pos.y - size.y - 10,
                pos.x - size.x - 35, pos.y - size.y - 10,
                pos.x - size.x - 35, pos.y + size.y + 10,
                pos.x - size.x, pos.y + size.y + 10))
        nextJumpFeet.rotation = rotation
        nextJumpFeet.setOrigin(poly.originX, poly.originY)


        sprite.setPosition(poly.transformedVertices[0], poly.transformedVertices[1])
        sprite.setOrigin(poly.originX, poly.originY)
        sprite.rotation = rotation

        faceSprite()

        updateXMovement()
        jumpLogic()

        inWater = false

        for (e in level.entities) {

            // GOOMBA //
            if (Intersector.overlapConvexPolygons(poly, e.poly))
                if (e.javaClass == Goomba::class.java) {
                    die()
                    break
                }

            if (Intersector.overlapConvexPolygons(e.poly, poly)) {

                // LEVER  //
                if (e.javaClass == Lever::class.java && e.alive && Inputer.tappedKey(Input.Keys.DOWN)) {
                    var l = e as Lever
                    l.pull()
                }

                // SPIKE //
                if (e.javaClass == Spike::class.java && e.alive) {
                    die()
                    break
                }

                // GOAL //
                if (e.javaClass == Goal::class.java && Inputer.tappedKey(Input.Keys.DOWN)) {
                    val g = e as Goal
                    Core.setLevel(g.exitLevel)
//                    initialRotation = this.rotation
                    break
                }
            }

            // WATER //
            if (e.javaClass == Water::class.java) {
                val w = e as Water
                if (Collision.overlapsPC(poly, w.body))
                    inWater = true
            }

        }
    }


    private fun jumpLogic() {
        for (e in level.entities) {
            if (e.alive && e.javaClass == Platform::class.java) {
                if (Intersector.overlapConvexPolygons(e.poly, feet)) {
                    height = e.pos.y + 30   // makes it land open nicely
                    onPlatform = true
                    vi = minVel
                    break
                }
            } else
                onPlatform = false
        }

        // jump when ready
        if (jumpReady) {
            if (onGround() || onPlatform) {
                jumpReady = false
                jump()
            }
        }

        if (Inputer.pressedKey(Input.Keys.SPACE)) {
            if (!inWater) {

                if (!pressedJump) {

                    // about to touch ground
                    if (vi <= 0 && Collision.overlapsPC(nextJumpFeet, p!!.body)) {
                        jumpReady = true
                        pressedJump = true
                    }

                    // about to touch platform
                    if (vi <= 0) {
                        for (e in level.entities) {
                            if (e.alive && e.javaClass == Platform::class.java) {
                                if (Intersector.overlapConvexPolygons(e.poly, nextJumpFeet)) {
                                    jumpReady = true
                                    pressedJump = true
                                    break
                                }
                            }
                        }
                    }
                }

            }

            if (inWater) {
                height += Math.abs(vi * 1.1f)
                jumping = false
                falling = true

                // swam out of water
                inWater = false
                for (e in Const.currentLevel!!.entities) {
                    if (e.javaClass == Water::class.java) {
                        val w = e as Water
                        if (Collision.overlapsPC(poly, w.body))
                            inWater = true
                    }
                }

                if (!inWater) {
                    jump()
                    // vi = jumpVel * 4;
                    t = 0f
                }
            }

        } else {

            if (vi <= 0)
                pressedJump = false

            // go down if max height reached
            if (!onGround() && !onPlatform && vi <= 0) {
                falling = true
            }
        }

        // you can't land if you're not open a platform or ground (head tap)
        if (!onGround() && !onPlatform)
            landedOnGround = false

        if (jumping || !onPlatform) {
            if (!inWater)
                height += vi
            else
                height += vi / 2

            t += 0.009f

            // back open planet / platform
            if (onGround() || onPlatform) {

                if (onGround()) {
                    height = p!!.radius + size.y
                    if (!landedOnGround) {
                        land.play(.9f)
                        landedOnGround = true
                    }
                }
                if (onPlatform) {
                    vi = 0f
                    if (!landedOnGround) {
                        land.play(.9f)
                        landedOnGround = true
                    }
                }

                jumping = false

                t = 0f  // setting t to 0 helps with new jumps for some reason
            }

            // if not falling at max vel
            if (vi > minVel) {
                vi += a * t
            }
        }
    }

    private fun updateXMovement() {
        var xAcceleration = 0.1f

        // move slower in air
        if (!onGround() && !onPlatform)
            xAcceleration = 0.5f

        if (!onGround() && !onPlatform) {
            step.isLooping = false
            step.stop()
        }

        if (Inputer.pressedKey(Input.Keys.LEFT)) {
            if (right)
                xVel = 0f
            if (!step.isLooping) {
                if (onGround() || onPlatform) {
                    step.isLooping = true
                    step.play()
                }
            }
            right = false

            if (xVel < xVelMax)
                xVel += xAcceleration

        }

        if (Inputer.pressedKey(Input.Keys.RIGHT)) {
            if (!right)
                xVel = 0f
            if (!step.isLooping) {
                if (onGround() || onPlatform) {
                    step.isLooping = true
                    step.play()
                }
            }
            right = true

            if (xVel < xVelMax)
                xVel += xAcceleration
        }

        if (!Inputer.pressedKey(Input.Keys.LEFT) && !Inputer.pressedKey(Input.Keys.RIGHT)) {
            step.isLooping = false
            step.stop()
            // friction
            var friction = .13f

            if (xVel > 0 && !right)
                xVel -= friction
            if (xVel < 0 && !right)
                xVel = 0f

            if (xVel > 0 && right)
                xVel -= friction
            if (xVel < 0 && right)
                xVel = 0f
        }

        if (right) {
            if (!inWater)
                rotation -= xVel
            else
                rotation -= xVel / 2
        } else {
            if (!inWater)
                rotation += xVel
            else
                rotation += xVel / 2
        }
    }

    override fun bounds(): Rectangle {
        val w = size.x * 2
        val h = w
        return Rectangle(pos.x - w / 2, pos.y - h / 2, w, h)
    }

    private fun onGround(): Boolean {
        return height <= p!!.radius + size.y + 4 // + 4 cuz feet
    }

    override fun render() {

        if (Const.DEBUG) {
            Gfx.sr.color = Color.ORANGE
            Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
            Gfx.sr.polygon(poly.transformedVertices)
            Gfx.sr.color = Color.PINK
            Gfx.sr.polygon(feet.transformedVertices)
            Gfx.sr.color = Color.PURPLE
            Gfx.sr.polygon(nextJumpFeet.transformedVertices)
            Gfx.sr.end()
        } else {
            if (invincible)
                sprite.color = Color.SKY
            else
                sprite.color = Color.WHITE

            Gfx.sb.projectionMatrix = Gfx.sr.projectionMatrix
            Gfx.sb.begin()
            sprite.draw(Gfx.sb)
            Gfx.sb.end()
        }
    }

    override fun dispose() {
        step.dispose()
        jump.dispose()
        death.dispose()
        land.dispose()
        sprite.texture.dispose()
    }

    private fun jump() {
        vi = jumpVel //vi
        jumping = true
        falling = false
        jump.stop()
        jump.play()
        landedOnGround = false
        xVel = 0f
        t = 0f      // this fixes jumping rape when holding up open platform
        onPlatform = false  // this solves landing sound not being played open platforms bug
    }

    fun die() {
        if (!invincible) {
            //            dispose();
            death.play()
            Core.resetLevel()
        }
    }

    private fun heightFromPlanet(): Float {
        return height - p!!.radius - 4
    }
}
