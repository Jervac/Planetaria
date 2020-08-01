package core

import com.badlogic.gdx.utils.viewport.Viewport
import util.Level

object Const {

    var WIDTH = 1280
    var HEIGHT = 720

    var DEFAULTL_RADIUS = 210

    var DEFAULT_GRAVITY = -1.7f

    var DEBUG = false
    var EDITOR = false

    var ZOOM = 1f
    var START_ROTATION = 0f

    // editor data
    var moveSpeed = 0.0004f
    var rotateSpeed = 2f
    var heightenSpeed = 1.4f

    var LEVELID = 0

    // for when level is loaded to prevent load order problems
    var STAR_HEIGHT: Float = 0.toFloat()

    var PLATFORM_HEIGHT = 5f


    var currentLevel: Level? = null
    var view: Viewport? = null

    var CURRENT_WORLD = 1

}
