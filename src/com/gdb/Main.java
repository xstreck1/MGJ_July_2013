package com.gdb;

import processing.core.*;

public class Main extends PApplet {
    private final int STEP_SIZE = 2;

    private PImage level = loadImage("level.png");
    private Hero hero = new Hero();
    private Light light = new Light();

    public void setup() {
	size(480, 320);
	loop();
	frameRate(25);
    }

    public void draw() {
	background(level);
	hero.draw();
	light.draw();
    }

    public void keyPressed() {
	if (keyCode == UP) {
	    hero.updateSpeed(0, -STEP_SIZE);
	} else if (keyCode == DOWN) {
	    hero.updateSpeed(0, STEP_SIZE);
	} else if (keyCode == LEFT) {
	    light.turnLeft(true);
	    hero.updateSpeed(-STEP_SIZE, 0);
	} else if (keyCode == RIGHT) {
	    light.turnRight(true);
	    hero.updateSpeed(STEP_SIZE, 0);
	} else if (key == ' ') {
	    light.locking();
	}
    }
    
    public void keyReleased() {
	if (keyCode == UP) {
	    hero.updateSpeed(0, 0);
	} else if (keyCode == DOWN) {
	    hero.updateSpeed(0, 0);
	} else if (keyCode == LEFT) {
	    light.turnLeft(false);
	    hero.updateSpeed(0, 0);
	} else if (keyCode == RIGHT) {
	    hero.updateSpeed(0, 0);
	    light.turnRight(false);
	}
    }

    public class Hero {
	private float posX = 100f;
	private float posY = 100f;
	private float moveX = 0.0f;
	private float moveY = 0.0f;
	private float angle = 0;
	private PImage heroImg = loadImage("hero.png");

	public void draw() {
	    posX += moveX;
	    posY += moveY;
	    image(heroImg, posX, posY);
	}

	public void updateSpeed(float x, float y) {
	    moveX = x;
	    moveY = y;
	}
    }

    public class Light {
	private final int SOURCE_WIDHT = 20;
	private final int SOURCE_HEIGHT = 20;
	private final float LIGHT_ANGLE = 0.5f;
	private final int DISTANCE = 200;

	private int posX = 50;
	private int posY = 50;
	private float angle = 0;
	private PImage light = loadImage("light.png");
	private boolean locked = false;
	private boolean turnLeft = false;
	private boolean turnRight = false;

	public void draw() {
	    turn();
	    // Light
	    fill(0);
	    pushMatrix();
	    translate(posX, posY);
	    
	    // Left corner
	    pushMatrix();
	    rotate(angle - LIGHT_ANGLE);
	    pushMatrix();
	    translate(0, -height);
	    rect(-width,-height,width*3,height*2);
	    popMatrix();
	    
	    // Right corner
	    rotate(LIGHT_ANGLE*(2));
	    rect(-width,0,width*3,height*2);
	    
	    // Front
	    rotate(LIGHT_ANGLE*(-1));
	    translate(DISTANCE, -height);
	    rect(0,0, width, height*2);
	    popMatrix();
	    
	    popMatrix();
	    
	    // Source
	    pushMatrix();
	    translate(posX, posX);
	    rotate(angle);
	    translate(-SOURCE_WIDHT / 2, -SOURCE_HEIGHT / 2);
	    image(light, 0, 0);
	    popMatrix();
	}

	public void setPos(int x, int y, float _angle) {
	    posX = x;
	    posY = y;
	    angle = _angle;
	}

	public void locking() {
	    locked = !locked;
	}

	private void turn() {
	    if (!locked)
		return;
	    if (turnLeft)
		angle -= 0.1;
	    else if (turnRight)
		angle += 0.1;
	}
	
	public void turnLeft(boolean turn) {
	    turnLeft = turn;
	}
	
	public void turnRight(boolean turn) {
	    turnRight = turn;
	}
    }
}

/*
 * public class Main extends PApplet { private PImage back; private PImage
 * front; private PImage mask; private PFont font; private PImage over;
 * 
 * private final int GAME_WIDTH = 800; private final int GAME_HEIGHT = 600;
 * 
 * public void setup() {
 * 
 * orientation(LANDSCAPE); size(GAME_WIDTH, GAME_HEIGHT);
 * 
 * back = loadImage("box.png"); front = loadImage("content.png"); mask =
 * createImage(GAME_WIDTH, GAME_HEIGHT, ARGB); mask.loadPixels();
 * front.loadPixels(); for (int i = 0; i < front.pixels.length; i++) { int
 * pix_color = front.pixels[i]; int a = (pix_color >> 24) & 0xFF; int r =
 * (pix_color >> 16) & 0xFF; // Faster way of getting red(argb) int g =
 * (pix_color >> 8) & 0xFF; // Faster way of getting // green(argb) int b =
 * pix_color & 0xFF; mask.pixels[i] = color(r, g, b, 2); } front =
 * createImage(GAME_WIDTH, GAME_HEIGHT, ARGB); mask.updatePixels();
 * front.set(0,0,mask); loop(); }
 * 
 * int delay = 0;
 * 
 * final int DIAMETER = 60; final int RADIUS = DIAMETER / 2; final int SQR_RAD =
 * (RADIUS) * (RADIUS);
 * 
 * public void draw() { image(back, 0, 0); mask.set(0,0,front); if
 * (mousePressed) { mask.loadPixels(); for (int x = max(0, mouseX - DIAMETER /
 * 2); x < min(GAME_WIDTH, mouseX + DIAMETER / 2); x++) { int span = (int)
 * Math.round(Math.sqrt(SQR_RAD - ((x - mouseX) * (x - mouseX)))); for (int y =
 * max(0, mouseY - span); y < min(GAME_HEIGHT, mouseY + span); y++) { int
 * position = y * 800 + x; int pix_color = mask.pixels[position]; int a =
 * (pix_color >> 24) & 0xFF; int r = (pix_color >> 16) & 0xFF; // Faster way of
 * getting // red(argb) int g = (pix_color >> 8) & 0xFF; // Faster way of
 * getting // green(argb) int b = pix_color & 0xFF; mask.pixels[position] =
 * color(r, g, b, 200); } } mask.updatePixels();
 * 
 * } image(mask, 0, 0); } }
 */
