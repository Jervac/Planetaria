package core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.viewport.ExtendViewport
import util.Gfx
import util.Inputer
import util.Level

class Core : ApplicationAdapter() {


    override fun create() {
        Gfx.initOrtho(Const.WIDTH.toFloat(), Const.HEIGHT.toFloat(), false)
        Gfx.cam.update()

        Const.view = ExtendViewport(Const.WIDTH.toFloat(), Const.HEIGHT.toFloat(), Gfx.cam)
        Const.view!!.apply()

        Const.currentLevel = Level("levels/world1/1-" + Const.LEVELID + ".lvl")
    }

    private fun update() {
        Gfx.update()

        Const.currentLevel!!.update()

        if (Inputer.tappedKey(Input.Keys.F1))
            Const.DEBUG = !Const.DEBUG
        if (Inputer.tappedKey(Input.Keys.F2))
            Const.EDITOR = !Const.EDITOR
        if (Inputer.tappedKey(Input.Keys.F3))
            Const.currentLevel!!.hero!!.invincible = !Const.currentLevel!!.hero!!.invincible

        if (Inputer.tappedKey(Input.Keys.SHIFT_LEFT))
            Const.currentLevel!!.hero!!.die()

        if (Inputer.tappedKey(Input.Keys.RIGHT_BRACKET))
            upLevel()
        if (Inputer.tappedKey(Input.Keys.LEFT_BRACKET)) {
            Const.currentLevel!!.dispose()
            Const.LEVELID = Const.LEVELID - 1
            Const.currentLevel = Level("levels/world1/1-" + Const.LEVELID + ".lvl")
        }

    }

    override fun render() {
        update()


    }

    override fun resize(width: Int, height: Int) {
        Const.view!!.update(width, height)
    }

    override fun dispose() {
        door_open.dispose()
        Gfx.dispose()
        Const.currentLevel!!.dispose()
    }

    companion object {

        var door_open: Sound = Gdx.audio.newSound(Gdx.files.internal("dooropen1.wav"))

        fun resetLevel() {
            Const.currentLevel!!.restart()
        }

        fun upLevel() {
            Const.currentLevel!!.clear()
            Const.LEVELID++
            Const.currentLevel!!.loadLevelFile("levels/world1/1-" + Const.LEVELID + ".lvl")
        }

        fun setLevel(levelFile: String) {
            Const.currentLevel!!.clear()
            Const.currentLevel!!.loadLevelFile(levelFile)
            Core.door_open.play()
        }

    }
}