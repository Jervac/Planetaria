package core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.viewport.ExtendViewport
import util.Gfx
import util.Inputer
import util.Level


class LevelEditor : ApplicationAdapter() {


    override fun create() {
        Gfx.initOrtho(Const.WIDTH.toFloat(), Const.HEIGHT.toFloat(), false)
        Gfx.cam.update()


        Const.view = ExtendViewport(Const.WIDTH.toFloat(), Const.HEIGHT.toFloat(), Gfx.cam)
        Const.view!!.apply()

        Const.currentLevel = Level("levels/1-" + Const.LEVELID + ".lvl")
    }

    private fun update() {
        Gfx.update()

        Const.currentLevel!!.update()

        if (Inputer.tappedKey(Input.Keys.ESCAPE))
            Gdx.app.exit()

        if (Inputer.tappedKey(Input.Keys.F1))
            Const.DEBUG = !Const.DEBUG
        if (Inputer.tappedKey(Input.Keys.F2))
            Const.EDITOR = !Const.EDITOR
        if (Inputer.tappedKey(Input.Keys.F3))
            Const.currentLevel!!.hero!!.invincible = !Const.currentLevel!!.hero!!.invincible

        if (Inputer.tappedKey(Input.Keys.SHIFT_LEFT))
            Const.currentLevel!!.hero!!.die()


        if (Inputer.tappedKey(Input.Keys.RIGHT_BRACKET))
            Core.upLevel()
        if (Inputer.tappedKey(Input.Keys.LEFT_BRACKET)) {
            Const.currentLevel!!.dispose()
            Const.LEVELID = Const.LEVELID - 1
            Const.currentLevel = Level("levels/1-" + Const.LEVELID + ".lvl")
        }

    }

    override fun render() {
        update()
        Gfx.setClearColor(Color(0 / 255f, 145 / 255f, 255 / 255f, 1f))
        Const.currentLevel!!.render()

        // fps
        Gfx.drawTextUI("FPS: " + Gdx.graphics.framesPerSecond,
                10f, Const.HEIGHT - Gfx.font.lineHeight, Color.BLACK)
        Gfx.drawTextUI("" + Const.currentLevel!!.filePath,
                100f, Const.HEIGHT - Gfx.font.lineHeight, Color.BLACK)
    }

    override fun resize(width: Int, height: Int) {
        Const.view!!.update(width, height)
    }

    override fun dispose() {
        Const.currentLevel!!.dispose()
    }


}
