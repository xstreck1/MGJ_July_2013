package com.gdb;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;
import processing.core.PImage;

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
	light.draw();
	hero.draw();
	if (hitsLight(hero.getPosition(), light.getPosition())) {
	    light.setLocked(true);
	} else 
	    light.setLocked(false);
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

    public boolean hitsLight(Rectangle hero_rect, Rectangle light_rect) {
	float maxDistance = round(hero_rect.getWidth() / 2.0f + light_rect.getWidth() / 2.0f);
	int distanceX = round(abs(hero_rect.getCenterX() - light_rect.getX()));
	int distanceY = round(abs(hero_rect.getCenterY() - light_rect.getY()));	
	float distance = sqrt(pow(distanceX,2) + pow(distanceY,2));
	return (distance < maxDistance);
    }

    /*
     * public void overlap(Rectangle rect, Triangle traingle) {
     * 
     * }
     */

    public class Hero {
	private Rectangle position;
	private float moveX = 0.0f;
	private float moveY = 0.0f;
	private float angle = 0;
	private PImage heroImg;

	Hero() {
	    heroImg = loadImage("hero.png");
	    position = new Rectangle(100, 100, heroImg.width, heroImg.height);
	}
	
	public void draw() {
	    position.setX(position.getX() + moveX);
	    position.setY(position.getY() + moveY);
	    image(heroImg, position.getX(), position.getY());
	}

	public void updateSpeed(float x, float y) {
	    moveX = x;
	    moveY = y;
	}

	public Rectangle getPosition() {
	    return position;
	}

	public void setPosition(Rectangle position) {
	    this.position = position;
	}
    }

    public class Light {
	private Rectangle position;
	private final int BLUR_WIDHT = 100;
	private final int BLUR_HEIGHT = BLUR_WIDHT;
	private final float LIGHT_ANGLE = 0.5f;
	private final int DISTANCE = 200;

	private float angle = 0;
	private PImage source_empty;
	private PImage source_habited;
	private boolean locked = false;
	private boolean turnLeft = false;
	private boolean turnRight = false;

	Light() {
	    source_empty = loadImage("light.png");
	    source_habited = loadImage("light2.png");
	    position = new Rectangle(200,150,source_empty.width, source_empty.height);
	}
	
	public void draw() {
	    turn();

	    // Darkness
	    pushMatrix();
	    fill(0);
	    translate(position.getX(), position.getY());

	    // Left corner
	    pushMatrix();
	    rotate(angle - LIGHT_ANGLE);
	    pushMatrix();
	    translate(0, -height);
	    rect(-width, -height, width * 3, height * 2);
	    popMatrix();

	    // Right corner
	    rotate(LIGHT_ANGLE * (2));
	    rect(-width, 0, width * 3, height * 2);

	    // Front
	    rotate(LIGHT_ANGLE * (-1));
	    translate(DISTANCE, -height);
	    rect(0, 0, width, height * 2);
	    popMatrix();

	    popMatrix();

	    // Source
	    pushMatrix();
	    translate(position.getX(), position.getY());

	    // Object
	    pushMatrix();
	    rotate(angle);
	    translate(-position.getWidth() / 2, -position.getHeight() / 2);
	    image(source_empty, 0, 0);
	    popMatrix();

	    // Blur
	    pushMatrix();
	    noStroke();
	    fill(255, 255, 255, 5);
	    ellipse(0, 0, BLUR_WIDHT, BLUR_HEIGHT);
	    ellipse(0, 0, BLUR_WIDHT * 0.6f, BLUR_HEIGHT * 0.6f);
	    ellipse(0, 0, BLUR_WIDHT * 0.4f, BLUR_HEIGHT * 0.4f);
	    ellipse(0, 0, BLUR_WIDHT * 0.3f, BLUR_HEIGHT * 0.3f);
	    popMatrix();

	    popMatrix();
	}

	public void setPos(int x, int y, float _angle) {
	    position.setX(x);
	    position.setY(y);
	    angle = _angle;
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

	public boolean isLocked() {
	    return locked;
	}

	public void setLocked(boolean locked) {
	    this.locked = locked;
	}

	public Rectangle getPosition() {
	    return position;
	}
    }

    public class Rectangle {
	private float x;
	private float y;
	private float width;
	private float height;

	Rectangle(float _x, float _y, float _width, float _height) {
	    x = _x;
	    y = _y;
	    width = _width;
	    height = _height;
	}

	public float getX() {
	    return x;
	}

	public void setX(float x) {
	    this.x = x;
	}

	public float getY() {
	    return y;
	}

	public void setY(float y) {
	    this.y = y;
	}

	public float getWidth() {
	    return width;
	}

	public void setWidth(float width) {
	    this.width = width;
	}

	public float getHeight() {
	    return height;
	}

	public void setHeight(float height) {
	    this.height = height;
	}

	public float getCenterX() {
	    return x + width / 2;
	}

	public float getCenterY() {
	    return y + height / 2;
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
