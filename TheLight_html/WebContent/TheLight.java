
    /* @pjs preload="background.png,char_e1.png,char_e2.png,char_e3.png,char_n1.png,char_n2.png,char_n3.png,char_s1.png,char_s2.png,char_s3.png,char_w1.png,char_w2.png,char_w3.png,door.png,level.png,light.png,light2.png,light_used.png,monster.png,sword.png"; */

    
    private final float STEP_SIZE = 3.0f;

    private PImage level;
    private Hero hero;
    private Door door;
    private ArrayList<Light> lights;

    private PFont font;

    private int INFINITE = MAX_INT;
    private Light light;
    private boolean blocked;
    private boolean dead;
    private int lvl_no;
    private int delay = 0;
    private boolean finished;
    private boolean ended;

    private final int LVL_COUNT = 5;

    public void readData(String filename) {
	lights = new ArrayList<Light>();
	String lines[] = loadStrings(filename);
	for (String line : lines) {
	    String line_data[] = split(line, ',');
	    if (line_data[0] == "light") {
		lights.add(new Light(parseInt(line_data[1]), parseInt(line_data[2]), parseFloat(line_data[3]), parseBoolean(line_data[4]), 16 + parseInt(line_data[5])));
	    } else if (line_data[0] == "hero") {
		hero = new Hero(parseInt(line_data[1]), parseInt(line_data[2]));
	    } else if (line_data[0] == "door") {

		door = new Door(parseInt(line_data[1]), parseInt(line_data[2]));
	    }
	}
    }

    public void setup() {
	size(480, 320);
	level = loadImage("background.png");
	font = loadFont("Ziggurat.vlw");
	loop();
	frameRate(25);

	lvl_no = 1;
	startNewLevel();

	textFont(font);
	ended = false;
    }

    public void startNewLevel() {
	if (lvl_no > LVL_COUNT) {
	    ended = false;
	    hero.updateSpeed(0, 0);
	    return;
	}
	readData("level" + (lvl_no).toString() + ".txt");
	light = lights.get(0);
	blocked = false;
	dead = false;
	finished = false;
    }

    public void setFinish() {
	blocked = true;
	if (!finished)
	    delay = 10;
	finished = true;
	drawText();
    }

    public void drawText() {
	textSize(80);
	fill(255, 0, 0);
	if (dead)
	    text("dead", 40, 240);
	else if (lvl_no <= LVL_COUNT)
	    text("success", 40, 240);
	else
	    text("game won", 40, 240);

    }

    public void draw() {
	if (delay-- > 0)
	    return;

	background(level);
	door.draw();
	for (Light test_light : lights) {
	    if (test_light != light)
		test_light.draw();
	}

	light.draw();
	if (!light.isLocked())
	    hero.draw();

	if (blocked) {
	    drawText();
	}

	if (hitsDoor(hero.getContact(), door.getPosition())) {
	    dead = false;
	    lvl_no++;
	    setFinish();
	}

	if (keyPressed) {

	    if (keyCode == UP || keyCode == DOWN || keyCode == LEFT || keyCode == RIGHT) {
		if (!blocked)
		    hero.nextFrame();
		else {
		    startNewLevel();
		}
	    }
	}

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
		dead = true;
		setFinish();
	    }
	}
    }

    public void keyPressed() {
	if (lvl_no > LVL_COUNT)
	    return;
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
	    light.turnLeftF(true);
	    hero.updateSpeed(-STEP_SIZE, 0);
	    hero.setDirection(2);
	} else if (keyCode == RIGHT) {
	    light.turnRightF(true);
	    hero.updateSpeed(STEP_SIZE, 0);
	    hero.setDirection(0);
	}
    }

    public void keyReleased() {
	if (lvl_no > LVL_COUNT)
	    return;
	if (keyCode == UP) {
	    hero.updateSpeed(0, 0);
	} else if (keyCode == DOWN) {
	    hero.updateSpeed(0, 0);
	} else if (keyCode == LEFT) {
	    light.turnLeftF(false);
	    hero.updateSpeed(0, 0);
	} else if (keyCode == RIGHT) {
	    hero.updateSpeed(0, 0);
	    light.turnRightF(false);
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
	float cone_proj = cos(cone_angle) * distance; // Scalar projection of
						      // distance onto vectors
						      // in "angle" angle from
						      // current position
						      // vector.
	float light_proj = cos(angle) * (x) + sin(angle) * (y); // Scalar
								// projection of
								// the distance
								// onto the
								// light
								// direction.
	if (cone_proj > light_proj) // The angle from the light direction is
				    // more than "angle" (the projection got
				    // smaller)
	    return true;
	if ((float) maxDistance < light_proj) // The projection onto the light
					      // direction is bigger than
					      // maximal distance, we are out of
					      // range.
	    return true;

	return false;
    }

    public boolean hitsDoor(Point contact, Rectangle door_Rect) {
	if (contact.getX() < door_Rect.getX())
	    return false;
	if (contact.getY() < door_Rect.getY())
	    return false;
	if (contact.getX() > door_Rect.getX() + door_Rect.getWidth())
	    return false;
	if (contact.getY() > door_Rect.getY() + door_Rect.getHeight())
	    return false;
	return true;
    }

    /*
     * public void overlap(Rectangle rect, Triangle traingle) {
     */

    public class Hero {
	private Rectangle position;
	private float moveX = 0.0f;
	private float moveY = 0.0f;
	private int frame = 0;
	private ArrayList<PImage> heroLeft;
	private ArrayList<PImage> heroUp;
	private ArrayList<PImage> heroRight;
	private ArrayList<PImage> heroDown;
	private ArrayList<PImage> heroIm;

	Hero(int x, int y) {
	    heroLeft = new ArrayList<PImage>();
	    heroUp = new ArrayList<PImage>();
	    heroRight = new ArrayList<PImage>();
	    heroDown = new ArrayList<PImage>();
	    heroIm = heroRight;
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
	    position = new Rectangle(x - heroLeft.get(0).width / 2, y - heroLeft.get(0).height / 2, heroLeft.get(0).width, heroLeft.get(0).height);
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

	public void turnLeftF(boolean turn) {
	    turnLeft = turn;
	}

	public void turnRightF(boolean turn) {
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

    public class Point {
	private int x;
	private int y;

	Point(int x, int y) {
	    this.x = x;
	    this.y = y;
	}

	public int getX() {
	    return x;
	}

	public void setX(int x) {
	    this.x = x;
	}

	public int getY() {
	    return y;
	}

	public void setY(int y) {
	    this.y = y;
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

    class Door {
	PImage my_image;
	Rectangle position;

	public Rectangle getPosition() {
	    return position;
	}

	Door(int my_x, int my_y) {
	    my_image = loadImage("door.png");
	    float my_width = my_image.width;
	    float my_height = my_image.height;
	    position = new Rectangle(my_x - my_width / 2, my_y - my_height / 2, my_width, my_height);
	}

	public void draw() {
	    image(my_image, position.getX(), position.getY());
	}
    }
