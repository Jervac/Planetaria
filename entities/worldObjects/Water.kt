package entities.worldObjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20.*
import com.badlogic.gdx.math.Circle
import entities.Entity
import util.Gfx

class Water(x: Float, y: Float, radius: Float) : Entity() {

    var body: Circle

    init {
        allignInWorld(x, y)
        size.set(radius, radius)
        body = Circle(x, y, radius)
    }

    override fun update() {
        body.set(pos.x, pos.y, size.x)
        editorControl()
    }

    override fun render() {
        Gdx.gl.glEnable(GL_BLEND)
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        Gfx.setColor(Color(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b, 0.4f))
        Gfx.fillCircle(body.x, body.y, body.radius)
        Gdx.gl.glDisable(GL_BLEND)
    }

    override fun dispose() {

    }

}
