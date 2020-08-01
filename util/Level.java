package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import core.Const;
import entities.Enemy;
import entities.Entity;
import entities.Hero;
import entities.Planet;
import entities.worldEnemies.DartShooter;
import entities.worldEnemies.Goomba;
import entities.worldObjects.*;
import util.gui.GUIBox;
import util.gui.TextBox;

import java.util.ArrayList;
import java.util.Scanner;

public class Level {

    public ArrayList<Entity> entities = new ArrayList<Entity>();

    public Hero hero;

    public String filePath;
    public Planet firstPlanet;
    Entity selectedEntity;
    boolean selected;

    GUIBox entityStats;
    TextBox i_entityAngle, i_entityRotation;
    TextBox i_entityX, i_entityY, i_entityWidth, i_entityHeight;

    GUIBox playerStats;
    TextBox i_playerXVel, i_playerAngle, i_playerInvincible, i_playerRight, i_playerHeight, i_playerViY;

    public Level(String path) {
        // calling this everytime planet class is created made duplicate render calls
        //Gfx.loadSprite("entities/nature/planet_0.png");

        loadLevelFile(path);
        initGUI();
    }

    public void loadLevelFile(String path) {
        filePath = path;
        FileHandle handle = Gdx.files.internal(path);
        String text = handle.readString();
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            // skip comment lines
            if (line.startsWith("#"))
                continue;

            String[] item = line.split(":");

            // CAMERA //
            if (item[0].equals("cam")) {
                Gfx.cam.position.x = Float.parseFloat(item[1]);
                Gfx.cam.position.y = Float.parseFloat(item[2]);
                Const.INSTANCE.setZOOM(Float.parseFloat(item[3]));
                Gfx.cam.zoom = Const.INSTANCE.getZOOM();
            }

            // PLAYER START POSITION //
            if (item[0].equals("start_height")) {
                Const.INSTANCE.setSTAR_HEIGHT(Float.parseFloat(item[1]));
            }
            if (item[0].equals("start_rotation")) {
                Const.INSTANCE.setSTART_ROTATION(Float.parseFloat(item[1]));
            }

            // PLANET //
            if (item[0].equals("planet")) {
                entities.add(new Planet(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3])));

                // first planet created
                firstPlanet = null;
                for (Entity e : entities)
                    if (e.getClass() == Planet.class) {
                        firstPlanet = (Planet) e;
                        break;
                    }
                if (firstPlanet == null)
                    Logger.error("NO PLANET IN LEVEL! [" + filePath + "]", true);

                // Hero has to be last to prevent concurrent mod
                if (Const.INSTANCE.getSTAR_HEIGHT() != 0 && hero == null) {
                    hero = new Hero(this, firstPlanet, Const.INSTANCE.getSTAR_HEIGHT());
                }
            }


            // PLATFORM //
            if (item[0].equals("platform"))
                entities.add(new Platform(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3])));

            // ROCK //
            if (item[0].equals("rock"))
                entities.add(new Rock(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3])));

            // GOAL //
            // Takes in level path as "" by default. If not set, constructor automatically chooses
            //next level in directory
            if (item[0].equals("goal"))
                entities.add(new Goal(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3]), item[4]));

            // SPIKE //
            if (item[0].equals("spike"))
                entities.add(new Spike(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3])));

            // WATER //
            if (item[0].equals("water"))
                entities.add(new Water(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3])));

            // LEVER //
            if (item[0].equals("lever"))
                entities.add(new Lever(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3]), Integer.parseInt(item[4])));

            // SPIKE WALL //
            if (item[0].equals("spikewall"))
                entities.add(new SpikeWall(Float.parseFloat(item[1]), Float.parseFloat(item[2]), Float.parseFloat(item[3]), Integer.parseInt(item[4])));

            if (firstPlanet != null) {
                if (item[0].equals("goomba"))
                    entities.add(new Goomba(firstPlanet, this, Float.parseFloat(item[1]), Boolean.parseBoolean(item[2])));
                if (item[0].equals("dartshooter"))
                    entities.add(new DartShooter(firstPlanet, this, Float.parseFloat(item[1]), Boolean.parseBoolean(item[3]), Float.parseFloat(item[2]), 1));
            }
        }
    }

    private void initGUI() {
        // ENTITIY //
        entityStats = new GUIBox(10, Const.INSTANCE.getHEIGHT() - (Gfx.font.getLineHeight() * 2));
        entityStats.title = " Entity";

        i_entityAngle = new TextBox(entityStats);
        entityStats.addItem(i_entityAngle);

        i_entityRotation = new TextBox(entityStats);
        entityStats.addItem(i_entityRotation);

        i_entityX = new TextBox(entityStats);
        entityStats.addItem(i_entityX);

        i_entityY = new TextBox(entityStats);
        entityStats.addItem(i_entityY);

        i_entityWidth = new TextBox(entityStats);
        entityStats.addItem(i_entityWidth);

        i_entityHeight = new TextBox(entityStats);
        entityStats.addItem(i_entityHeight);


        // PLAYER //
        playerStats = new GUIBox(Const.INSTANCE.getWIDTH() - 135, Const.INSTANCE.getHEIGHT() - (Gfx.font.getLineHeight() * 2));
        playerStats.title = " Player";

        i_playerXVel = new TextBox(playerStats);
        playerStats.addItem(i_playerXVel);

        i_playerAngle = new TextBox(playerStats);
        playerStats.addItem(i_playerAngle);

        i_playerInvincible = new TextBox(playerStats);
        playerStats.addItem(i_playerInvincible);

        i_playerRight = new TextBox(playerStats);
        playerStats.addItem(i_playerRight);

        i_playerHeight = new TextBox(playerStats);
        playerStats.addItem(i_playerHeight);

        i_playerViY = new TextBox(playerStats);
        playerStats.addItem(i_playerViY);

    }

    public void update() {
        for (Entity e : entities)
            if (e.getAlive())
                e.update();


        if (hero != null)
            hero.update();

        if (selectedEntity != null && selectedEntity.getAlive()) {
            selectedEntity.update();

            if (Inputer.tappedKey(Input.Keys.BACKSPACE))
                selectedEntity.setAlive(false);
        }


        selected = false;

        if (Const.INSTANCE.getEDITOR())
            input();

        // selecting current entity
        Gfx.sr.begin(ShapeRenderer.ShapeType.Line);
        Gfx.sr.polygon(Inputer.mouseBoundsPolygon().getTransformedVertices());
        Gfx.sr.end();

        // TODO: shorten this by checking only when clicked
        for (Entity e : entities) {
            if (Gdx.input.isButtonPressed(0) && e.getClass() == Planet.class) {
                Planet p = (Planet) e;
                if (Collision.overlapsPC(Inputer.mouseBoundsPolygon(), p.getBody())) {
                    selectedEntity = e;
                    selected = true;
                    break;
                }
            }

            if (Gdx.input.isButtonPressed(0) && e.getAlive() && Intersector.overlapConvexPolygons(Inputer.mouseBoundsPolygon(), e.getPoly())) {
                selectedEntity = e;
                selected = true;
                break;
            }
        }

        if (selected) {
            for (Entity e : entities)
                e.setControlable(false);

            selectedEntity.setControlable(true);
        }

        updateEditor();

    }

    private void updateEditor() {
        entityStats.update();
        playerStats.update();

    }

    public void render() {
        Gfx.sb.totalRenderCalls = 0;

        if (filePath.contains("5")) {
            Gfx.font.setColor(Color.BLACK);
            Gfx.drawText("YOU ACTUALLY FINISHED!\nThat means this was beatable open start.\nTell me:\nHow hard it was\nHow long it took\nDid you enjoy it?\nWhat could be better?", Const.INSTANCE.getWIDTH() / 2 - 400, 0);
        }

        for (Entity e : entities)
            if (e.getAlive() && e.getDepth() == 1)
                e.render();

        if (hero != null)
            hero.render();

        for (Entity e : entities)
            if (e.getAlive() && e.getDepth() == 2)
                e.render();

        renderEditor();
    }

    private void renderEditor() {
        // render current entity data
        if (selectedEntity != null && Const.INSTANCE.getEDITOR()) {
            entityStats.render();

            i_entityAngle.text = "angle: " + selectedEntity.getAngle();
            i_entityRotation.text = "rotation: " + selectedEntity.getRotation();
            i_entityX.text = "X: " + selectedEntity.getPos().x;
            i_entityY.text = "Y: " + selectedEntity.getPos().y;
            i_entityWidth.text = "Width: " + selectedEntity.getSize().x;
            i_entityHeight.text = "Height: " + selectedEntity.getSize().y;

            // Goal level path
            if (selectedEntity.getClass() == Goal.class) {
                Goal d = (Goal) selectedEntity;
                i_entityAngle.text = d.getExitLevel();
            }

        }

        if (Const.INSTANCE.getDEBUG() || Const.INSTANCE.getEDITOR() && hero != null) {
            playerStats.render();

            i_playerXVel.text = "xVel: " + hero.getXVel();
            i_playerAngle.text = "rotation: " + hero.getRotation();
            i_playerInvincible.text = "invincible: " + hero.getInvincible();
            i_playerRight.text = "right: " + hero.getRight();
            i_playerViY.text = "ViY: " + hero.getVi();
        }

        // editor items
        if (Const.INSTANCE.getEDITOR()) {
            Gfx.font.setColor(Color.BLACK);
            Gfx.drawTextUI("Entities: " + entities.size(), 10, 150 + (Gfx.font.getLineHeight() * 2));
            Gfx.drawTextUI("sprite batch calls: " + Gfx.sb.totalRenderCalls, 10, 150 + (Gfx.font.getLineHeight() * 3));
            Gfx.drawTextUI("-Level Editor Inputs-", 10, 150 + (Gfx.font.getLineHeight()));
            Gfx.drawTextUI("[1] Platform", 10, 150);
            Gfx.drawTextUI("[2] Spikes", 10, 150 - (Gfx.font.getLineHeight()));
            Gfx.drawTextUI("[3] Goal", 10, 150 - (Gfx.font.getLineHeight() * 2));
            Gfx.drawTextUI("[4] Moving Enemy", 10, 150 - (Gfx.font.getLineHeight() * 3));
            Gfx.drawTextUI("[5] Shooting Enemy", 10, 150 - (Gfx.font.getLineHeight() * 4));
            Gfx.drawTextUI("[6] Water", 10, 150 - (Gfx.font.getLineHeight() * 5));
            Gfx.drawTextUI("[7] Rock", 10, 150 - (Gfx.font.getLineHeight() * 6));
            Gfx.drawTextUI("[8] Lever", 10, 150 - (Gfx.font.getLineHeight() * 7));
            Gfx.drawTextUI("[9] SpikeWall", 10, 150 - (Gfx.font.getLineHeight() * 8));

        }

    }

    public void restart() {
        for (Entity e : entities) {
            if (e.getClass() == Goomba.class || e.getClass() == DartShooter.class) {
                Enemy en = (Enemy) e;
                e.setRotation(en.getInitialRotation());
                e.setRight(en.getInitialRight());
//                en.faceSprite();

                if (en.getClass() == DartShooter.class) {
                    DartShooter d = (DartShooter) e;
                    d.reset();
                }
            }
        }

        hero.setRotation(hero.getInitialRotation());
        hero.setHeight(hero.getInitialHeight());
        hero.setJumpReady(false);
        hero.setPressedJump(true);
        hero.setLandedOnGround(true);
    }

    private void input() {
        // save level to current file
        if (Inputer.tappedKey(Input.Keys.ENTER)) {
            this.save();
        }
        // delete selected entitiy
        if (Inputer.tappedKey(Input.Keys.BACKSPACE) && selectedEntity != null)
            selectedEntity.setAlive(false);
        if (Inputer.tappedKey(Input.Keys.NUM_1)) {
            entities.add(new Platform(0, 0, 0));
            selectedEntity = entities.get(entities.size() - 1);
            selectedEntity.setPos(new Vector2(selectedEntity.getPos().x - (selectedEntity.getSize().x / 2), selectedEntity.getPos().y));
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_2)) {
            entities.add(new Spike(0, 0, 0));
            selectedEntity = entities.get(entities.size() - 1);
            selectedEntity.setPos(new Vector2(selectedEntity.getPos().x - (selectedEntity.getSize().x / 2), selectedEntity.getPos().y));
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_3)) {
            entities.add(new Goal(0, 0, 0, ""));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_4) && firstPlanet != null) {
            entities.add(new Goomba(firstPlanet, this, 0, false));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_5) && firstPlanet != null) {
            entities.add(new DartShooter(firstPlanet, this, 0, false, 0, 1));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_6)) {
            entities.add(new Water(0, 0, 120));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_7)) {
            entities.add(new Rock(0, 0, 0));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_8)) {
            entities.add(new Lever(0, 0, 0, 0));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        if (Inputer.tappedKey(Input.Keys.NUM_9)) {
            entities.add(new SpikeWall(0, 0, 0, 0));
            selectedEntity = entities.get(entities.size() - 1);
            selected = true;
        }
        // change entity direction if can
        if (selectedEntity != null && Inputer.tappedKey(Input.Keys.SPACE)) {
            if (selectedEntity.getClass() == Goomba.class) {
                Goomba g = (Goomba) selectedEntity;
                g.setRight(!g.getRight());
            }
            if (selectedEntity.getClass() == DartShooter.class) {
                DartShooter g = (DartShooter) selectedEntity;
                g.setRight(!g.getRight());
            }
        }

        // Setting level path of door
        if (Inputer.tappedKey(Input.Keys.P) && selectedEntity.getClass() == Goal.class) {
            System.out.println("Enter level this door sends you to: ");
            Scanner scan = new Scanner(System.in);  // TODO: make global scanner rather than recreate each time (same as in save())
            String path = scan.next();

            Goal g = (Goal) selectedEntity;
            g.setExitLevel("levels/" + path + ".lvl");

            System.out.println("Set door path to: " + g.getExitLevel());
            save();
        }

    }

    public void dispose() {
        for (Entity e : entities)
            e.dispose();
        entities.clear();
        if (hero != null)
            hero.dispose();

        entityStats.dispose();
    }

    public void clear() {
        for (Entity e : entities)
            e.dispose();
        entities.clear();

        if (hero != null)
            hero.dispose();
        hero = null;
    }

    private void save() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter save name: ");
        String savePath = scan.next();
        filePath = "levels/" + savePath + ".lvl";
        System.out.println("Saving data to : " + filePath);
        // cannot write to internal file but can with local
        FileHandle handle = Gdx.files.local(filePath);
        handle.writeString("cam:" + Gfx.cam.position.x + ":" + Gfx.cam.position.y + ":" + Gfx.cam.zoom, false);
        handle.writeString("\nstart_rotation:" + hero.getRotation(), true);
        handle.writeString("\nstart_height:" + hero.getHeight(), true);
        System.out.println(entities.size());

        for (Entity e : entities) {

            if (e.getAlive()) {
                String saveName = e.getClass().toString();
                String saveData;

                // WATER //
                if (e.getClass() == Water.class) {
                    Water w = (Water) e;
                    saveData = "water:" + e.getPos().x + ":" + e.getPos().y + ":" + w.getBody().radius;
                    // GOOMBA //
                } else if (e.getClass() == Goomba.class) {
                    Goomba g = (Goomba) e;
                    saveData = "goomba" + ":" + g.getRotation() + ":" + g.getRight();
                    // DART SHOOTER //
                } else if (e.getClass() == DartShooter.class) {
                    DartShooter d = (DartShooter) e;
                    saveData = "dartshooter" + ":" + d.getRotation() + ":" + (d.getHeight() - firstPlanet.getRadius()) + ":" + d.getRight();
                    // GOAL //
                } else if (e.getClass() == Goal.class) {
                    Goal g = (Goal) e;
                    saveData = "goal:" + e.getPos().x + ":" + e.getPos().y + ":" + e.getPoly().getRotation() + ":" + g.getExitLevel();
                    System.out.println("goal save");
                    // PLANET //
                } else if (e.getClass() == Planet.class) {
                    Planet p = (Planet) e;
                    saveName = saveName.replace("class entities.", "").toLowerCase();
                    saveData = saveName + ":" + e.getPos().x + ":" + e.getPos().y + ":" + p.getRadius();
                } else if (e.getClass() == Lever.class) {
                    Lever l = (Lever) e;
                    saveData = "lever:" + e.getPos().x + ":" + e.getPos().y + ":" + e.getPoly().getRotation() + ":" + l.getId();
                } else if (e.getClass() == SpikeWall.class) {
                    SpikeWall sw = (SpikeWall) e;
                    saveData = "spikewall:" + e.getPos().x + ":" + e.getPos().y + ":" + e.getPoly().getRotation() + ":" + e.getId();

                    // OTHER //
                } else {
                    saveName = saveName.replace("class entities.worldObjects.", "").toLowerCase(); // removes extra text
                    saveData = saveName + ":" + e.getPos().x + ":" + e.getPos().y + ":" + e.getPoly().getRotation();
                }

                handle.writeString("\n" + saveData, true);
            }
        }
        System.out.println("Save Complete!");
    }

}
