package com.gdb;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Main extends PApplet {
    private final float STEP_SIZE = 3.0f;

    private PImage level = loadImage("level.png");
    private Hero hero = new Hero();

    private PFont font = loadFont("Ziggurat.vlw");
    private ArrayList<Light> lights = new ArrayList<Light>();
    int INFINITE = MAX_INT;
    Light light;

    public void setup() {
	size(480, 320);
	loop();
	frameRate(25);
	textFont(font);
	lights.add(new Light(200, 150, 0.1f, true, 100));
	lights.add(new Light(230, 210, 0.1f, false, 100));
	light = lights.get(0);
    }

    public void draw() {
	background(level);
	for (Light test_light : lights) {
	    if (test_light != light)
		test_light.draw();
	}
	light.draw();
	
	if (!light.isLocked())
	    hero.draw();

	boolean locked = false;
	for (Light test_light : lights) {
	    if (hitsLight(hero.getPosition(), test_light.getPosition())) {
		light.setLocked(false);
		light.setShining(false);
		test_light.setLocked(true);
		test_light.setShining(true);
		light = test_light;
		locked = true;
	    }
	}
	
	if (locked == false) {
	    if (hitsDarkness(hero.getContact(), light.getCenter(), light.getAngle(), light.getLightAngle(), light.getDistance())) {
		fill(255, 0, 0);
		text("dead", 40, 40);
	    }

	    if (keyPressed) {
		if (keyCode == UP || keyCode == DOWN || keyCode == LEFT || keyCode == RIGHT)
		    hero.nextFrame();
	    }
	}
    }

    public void keyPressed() {
	if (keyCode == UP) {
	    hero.updateSpeed(0, -STEP_SIZE);
	    if (light.isLocked()) {
		hero.setOut(light.getFrontPos((hero.getPosition().getWidth() + light.getPosition().getWidth()) / 2.0f));
		light.setLocked(false);
	    }
	    hero.setDirection(3);
	} else if (keyCode == DOWN) {
	    hero.updateSpeed(0, STEP_SIZE);
	    hero.setDirection(1);
	} else if (keyCode == LEFT) {
	    light.turnLeft(true);
	    hero.updateSpeed(-STEP_SIZE, 0);
	    hero.setDirection(2);
	} else if (keyCode == RIGHT) {
	    light.turnRight(true);
	    hero.updateSpeed(STEP_SIZE, 0);
	    hero.setDirection(0);
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
	float maxDistance = round(hero_rect.getWidth() / 4.0f + light_rect.getWidth() / 2.0f);
	int distanceX = round(abs(hero_rect.getCenterX() - light_rect.getX()));
	int distanceY = round(abs(hero_rect.getY() + hero_rect.getHeight() - light_rect.getY()));
	float distance = sqrt(pow(distanceX, 2) + pow(distanceY, 2));
	return (distance < maxDistance);
    }

    public boolean hitsDarkness(Point contact, Point center, float angle, float cone_angle, int maxDistance) {
	float x = contact.x - center.x;
	float y = contact.y - center.y;
	float distance = sqrt(x * x + y * y);
	float left = cos(cone_angle) * distance;
	float right = cos(angle) * (x) + sin(angle) * (y);
	if (left > right)
	    return true;
	if (right > (float) maxDistance)
	    return true;

	return false;
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
	private int frame = 0;
	private ArrayList<PImage> heroLeft = new ArrayList<PImage>();
	private ArrayList<PImage> heroUp = new ArrayList<PImage>();
	private ArrayList<PImage> heroRight = new ArrayList<PImage>();
	private ArrayList<PImage> heroDown = new ArrayList<PImage>();
	private ArrayList<PImage> heroIm = heroRight;

	Hero() {
	    heroLeft.add(loadImage("char_w1.png"));
	    heroLeft.add(loadImage("char_w2.png"));
	    heroLeft.add(loadImage("char_w1.png"));
	    heroLeft.add(loadImage("char_w3.png"));
	    heroUp.add(loadImage("char_n1.png"));
	    heroUp.add(loadImage("char_n2.png"));
	    heroUp.add(loadImage("char_n1.png"));
	    heroUp.add(loadImage("char_n3.png"));
	    heroRight.add(loadImage("char_e1.png"));
	    heroRight.add(loadImage("char_e2.png"));
	    heroRight.add(loadImage("char_e1.png"));
	    heroRight.add(loadImage("char_e3.png"));
	    heroDown.add(loadImage("char_s1.png"));
	    heroDown.add(loadImage("char_s2.png"));
	    heroDown.add(loadImage("char_s1.png"));
	    heroDown.add(loadImage("char_s3.png"));
	    position = new Rectangle(220, 130, heroLeft.get(0).width, heroLeft.get(0).height);
	}

	public void draw() {
	    position.setX(position.getX() + moveX);
	    position.setY(position.getY() + moveY);
	    image(heroIm.get(frame), position.getX(), position.getY());
	}

	public void setDirection(int new_direction) {
	    if (new_direction == 0)
		heroIm = heroRight;
	    else if (new_direction == 1)
		heroIm = heroDown;
	    else if (new_direction == 2)
		heroIm = heroLeft;
	    else if (new_direction == 3)
		heroIm = heroUp;
	}

	public void nextFrame() {
	    frame = (frame + 1) % 4;
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

	public void setOut(Point point) {
	    position.setX(point.x - position.getHeight());
	    position.setY(point.y - position.getWidth() * 1.25f);
	}

	public Point getContact() {
	    return new Point(round(position.getCenterX()), round(position.getCenterY() + position.getHeight() * 0.40f));
	}
    }

    public class Light {
	private Rectangle position;
	private final int BLUR_WIDHT = 100;
	private final int BLUR_HEIGHT = BLUR_WIDHT;
	private final float LIGHT_ANGLE = 0.5f;

	private int distance;
	private float angle;
	private PImage source_empty;
	private PImage source_habited;
	private boolean shining = false;
	private boolean locked = false;
	private boolean turnLeft = false;
	private boolean turnRight = false;

	Light(int x, int y, float angle, boolean on, int distance) {
	    source_empty = loadImage("light.png");
	    source_habited = loadImage("light_used.png");
	    this.angle = angle;
	    this.shining = on;
	    this.distance = distance;
	    position = new Rectangle(x, y, source_empty.width, source_empty.height);
	}

	public void draw() {
	    if (locked)
		turn();

	    // Darkness
	    pushMatrix();
	    fill(0);
	    translate(position.getX(), position.getY());

	    if (shining) {
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
		translate(distance, -height);
		rect(0, 0, width, height * 2);
		popMatrix();

		
	    }
	    popMatrix();
	    // Source
	    pushMatrix();
	    translate(position.getX(), position.getY());

	    // Object
	    pushMatrix();
	    rotate(angle);
	    translate(-position.getWidth() / 2, -position.getHeight() / 2);
	    if (locked)
		image(source_habited, 0, 0);
	    else
		image(source_empty, 0, 0);
	    popMatrix();
	    if (shining) {
		// Blur
		pushMatrix();
		noStroke();
		fill(255, 255, 255, 5);
		ellipse(0, 0, BLUR_WIDHT, BLUR_HEIGHT);
		ellipse(0, 0, BLUR_WIDHT * 0.6f, BLUR_HEIGHT * 0.6f);
		ellipse(0, 0, BLUR_WIDHT * 0.4f, BLUR_HEIGHT * 0.4f);
		ellipse(0, 0, BLUR_WIDHT * 0.3f, BLUR_HEIGHT * 0.3f);
		popMatrix();
	    }

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

	public boolean isShining() {
	    return shining;
	}

	public void setShining(boolean shining) {
	    this.shining = shining;
	}

	public Rectangle getPosition() {
	    return position;
	}

	public Point getFrontPos(float distance) {

	    int x = round(position.getCenterX() + distance * cos(angle));
	    int y = round(position.getCenterY() + distance * sin(angle));

	    return new Point(x, y);
	}

	public Point getCenter() {
	    return new Point(round(position.getCenterX() - position.getWidth() / 2), round(position.getCenterY() - position.getHeight() / 2));
	}

	public float getAngle() {
	    return angle;
	}

	public int getDistance() {
	    return distance;
	}

	public float getLightAngle() {
	    return LIGHT_ANGLE;
	}
    }

    public class Rectangle {
	private float x;
	private float y;
	private float my_width;
	private float my_height;

	Rectangle(float _x, float _y, float _width, float _height) {
	    x = _x;
	    y = _y;
	    my_width = _width;
	    my_height = _height;
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
	    return my_width;
	}

	public void setWidth(float width) {
	    this.my_width = width;
	}

	public float getHeight() {
	    return my_height;
	}

	public void setHeight(float height) {
	    this.my_height = height;
	}

	public float getCenterX() {
	    return x + my_width / 2;
	}

	public float getCenterY() {
	    return y + my_height / 2;
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
