import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Gravitational_Slingshot_5 extends PApplet {

/**
This program will plot slingshot paths around 'plants' using Newton's law
of universal gravitation. This will use a version of gradient descent
that uses approximated gradients to calculate 'direction' for the two 
parameters. The parameters for the ship that must be optimized include:

  1. Angle of exit
  2. Speed of exit
  
The 'time' or number of frames after which the position of the satellite
will be observed will be chosen at the beginning.

This is a quick-and-dirty version of what should eventually be more user
friendly and customizable. The class structure will be:

  1. Main Driver Class
  2. Stationary 'planet' class
  3. Ship class
  
The basic logic steps for the program will include:

  1. Randomly generate planets
  2. Randomly initialize a ship and calculate aproximated partial 
     derivatives using precision value beta for both parameters
     J(theta) = abs(distance from ship at time to the objective planet).
  3. Move each parameter PDiv*alpha units over, and repeat 
     from step 2 until max iterations has been reached OR ship
     reaches the 
  4. Show user the path on the screen on repeat.

Development Steps:

  1. Design planet class (done)
  2. Design ship class (done)
  3. Implement random creation of planets. (done)
  4. Test ship class with random values for speed and angle. (done)
  5. Create function to find PDivs for speed and angle given 
     current ship data, planet data, beta or h val, and gravitational constant
     value. (done)
  6. Create updateShip function that takes current ship data
     and PDivs and alpha value to change the ship's data to learn
     from the last test. (done)
  7. Create loop system to repeatedly update (descend using gradients)
     the ship.
  8. See if ship reaches the objective planet after that. 

General Notes:

  * The objective planet will be the last one in the planets array and will
    be colored differently
  * The 2 dimensional vector that represents the Partial Derivatives for the Ship parameters will
    be in order of [speed, angle]
    
Customizable Parameters:

  0. Gravitational Constant (0.01-30)
  1. Number of Planets (2-40)
  2. Maximum Size of Planets (7-30)
  3. Time given for voyage (50-1000)
  4. Max Ship Speed (5-20);
  5. Speed variability (5-100)
  6. Angle variability (3-180)

*/

//Slider(int px, int py, int wx, float maV, float miV, float v)

Slider sG = new Slider(WIDTH/2, HEIGHT/7, WIDTH-100, 30, 0.1f, 2);
Slider sPlanets = new Slider(WIDTH/2, HEIGHT/7 + HEIGHT/7, WIDTH-100, 40, 2, 20);
Slider sTime = new Slider(WIDTH/2, HEIGHT/7 + 2*HEIGHT/7, WIDTH-100, 2000, 50, 200);
Slider sSpeed = new Slider(WIDTH/2, HEIGHT/7 + 3*HEIGHT/7, WIDTH-100, 20, 2, 10);
Slider sSpeedVar = new Slider(WIDTH/2, HEIGHT/7 + 4*HEIGHT/7, WIDTH-100, 100, 5, 4);
Slider sAngleVar = new Slider(WIDTH/2, HEIGHT/7 + 5*HEIGHT/7, WIDTH-100, 180, 2, 100);

/*
Slider sG;
Slider sPlanets;
Slider sTime;
Slider sSpeed;
Slider sSpeedVar;
Slider sAngleVar;

sG;
sPlanets;
sTime;
sSpeed;
sSpeedVar;
sAngleVar;
*/


public static float G = 2;//gravitational constant
public static float NUM_PLANETS = 20;
public static final int WIDTH = 1280;
public static final int HEIGHT = 720;
public static int maxSize = 10;//diameter of planet
public static int FPS = 180;
public static int MAX_TIME = 200;

public static ArrayList<Planet> planets;
public static Ship ship;

public static float shipVarSpeed = 10;
public static float shipVarAngle = 100;
public static float shipMaxSpeed = 10;//added to ship class

public static ArrayList<float[]> trail;

int BGC = color(245);

public static final int numSteps = 100;

Button go = new Button(WIDTH/2 - (300/2), HEIGHT-75, 300, 60, "Start Evolution!");

public void setup(){
 
  //General settings
  frameRate(FPS);
  background(BGC);
  
  //size(2000, 2000);
  
  noStroke();
  
  for(int i = 0; i < 100; i++){
    //boolean esc = ship.step(MAX_TIME, planets, G); 
    //if(esc){
    //  System.out.println("Cost: " + ship.cost(MAX_TIME, planets, G) + " at " + i + " evolutions."); 
    //}
    //if(i % 20 == 0){
    //  System.out.println("Just passed generation number " + i + "/200"); 
    //}
  }
  
}

//time integer
int tick = 0;
int screen = 0;

int numEv = 0;
int numGens = 0;
public void draw(){
  
  noStroke();
  smooth();
  
  if(screen == 0){
    background(200);
    textAlign(CENTER, CENTER);
    
    fill(50);
    textSize(40);
    text("Welcome", WIDTH/2, HEIGHT/2 - 20);
    textSize(20);
    text("Click to Continue", WIDTH/2, HEIGHT/2 + 10);
    textSize(15);
    text("This program basically uses evolution to optimize a ship's path through a randomly generated planetary system.", WIDTH/2, HEIGHT/2 - 100); 
    text("The blue circle represents the ship, the light grey circles represent planets, and the black circle is the goal planet", WIDTH/2, HEIGHT/2 - 80);
    text("Enjoy playing around with this! The next page lets you play around with some fun settings.", WIDTH/2, HEIGHT/2 -60);
    fill(100);
    text("Created by Aman, special thanks to Mr. Anderson for the \'challenge\'", WIDTH/2, HEIGHT - 100);
  }
  
  else if(screen == 1){
    background(200);
    fill(0);
    
    textAlign(CENTER, CENTER);
    textSize(40);
    text("Some Fun Parameters", WIDTH/2, 20);
    
    textSize(20);
    
    textAlign(LEFT, CENTER);
    fill(0);
    text("Gravitational Constant: " + sG.val, sG.posx, sG.posy-20);
    sG.drawThis();
    G = sG.val;
    fill(0);
    text("Number of Planets: " + sPlanets.val, sPlanets.posx, sPlanets.posy-20);
    sPlanets.drawThis();
    NUM_PLANETS = sPlanets.val;
    fill(0);
    text("Maximum Time for Ship to Reach Planet: " + sTime.val, sTime.posx, sTime.posy-20);
    sTime.drawThis();
    MAX_TIME = (int) (sTime.val);
    fill(0);
    text("Maximum Ship Speed: " + sSpeed.val, sSpeed.posx, sSpeed.posy-20);
    sSpeed.drawThis();
    shipMaxSpeed = sSpeed.val;
    fill(0);
    text("Ship Speed Variability: " + sSpeedVar.val, sSpeedVar.posx, sSpeedVar.posy-20);
    sSpeedVar.drawThis();
    shipVarSpeed = sSpeedVar.val;
    fill(0);
    text("Ship Angle Variability: " + sAngleVar.val, sAngleVar.posx, sAngleVar.posy-20);
    sAngleVar.drawThis();
    shipVarAngle = sAngleVar.val;
    
    drawButton(go);
  }
  else if(screen == 2){
    setupEverything();
    screen++;
  }
  
  else{
    tick++;
  
    background(BGC);
    
    
    drawPlanets();
    
    fill(100, 100, 255);
    
    float[] shipco = ship.findWhere((tick), planets, G);
    float curShipX = shipco[0];
    float curShipY = shipco[1];
    
    ellipse(curShipX, curShipY, 20, 20);
    
    float[] tmp = {curShipX, curShipY};
    trail.add(tmp);
    drawTrail();
    
    if(tick == MAX_TIME){
      tick = 0; 
      ship.reset();
      
      boolean esc = false;
      while(!esc){
        numGens++;
        esc = ship.step(MAX_TIME, planets, G); 
        if(esc){
          System.out.println("Cost: " + ship.cost(MAX_TIME, planets, G) + " at " + numEv + " evolutions."); 
        }
        if(numEv % 1 == 0){
          System.out.println("Just passed generation number " + numEv); 
        }
        numEv ++;
      }
      //System.out.println("Cost: " + ship.cost(MAX_TIME, planets, G));
    } 
    
    textSize(20);
    fill(0);
    text("Just passed generation number " + numGens, WIDTH-200, HEIGHT-20);
  }
    
  
}
/*

;
;
;
;
*/
public void mouseClicked(){
  if(screen == 0){
    screen++;
  }
  else if(screen == 1){
    //if(){//code for checking if mouse is within button dimensions
      
    //}
    screen++; 
  }
  
}
public void mousePressed() {
  if(sG.onSlider(mouseX,mouseY)) {
    sG.val = ((mouseX - sG.posx)/((float)(sG.widthx))) * (sG.maxVal-sG.minVal) + sG.minVal;
  }
  if(sPlanets.onSlider(mouseX,mouseY)) {
    sPlanets.val = ((mouseX - sPlanets.posx)/((float)(sPlanets.widthx)))*(sPlanets.maxVal-sPlanets.minVal) + sPlanets.minVal;
  }
  if(sTime.onSlider(mouseX,mouseY)) {
    sTime.val = ((mouseX - sTime.posx)/((float)(sTime.widthx)))*(sTime.maxVal-sTime.minVal) + sTime.minVal;
  }
  if(sSpeed.onSlider(mouseX,mouseY)) {
    sSpeed.val = ((mouseX - sSpeed.posx)/((float)(sSpeed.widthx)))*(sSpeed.maxVal-sSpeed.minVal) + sSpeed.minVal;
  }
  if(sSpeedVar.onSlider(mouseX,mouseY)) {
    sSpeedVar.val = ((mouseX - sSpeedVar.posx)/((float)(sSpeedVar.widthx)))*(sSpeedVar.maxVal-sSpeedVar.minVal) + sSpeedVar.minVal;
  }
  if(sAngleVar.onSlider(mouseX,mouseY)) {
    sAngleVar.val = ((mouseX - sAngleVar.posx)/((float)(sAngleVar.widthx)))*(sAngleVar.maxVal-sAngleVar.minVal) + sAngleVar.minVal;
  }
}

public void mouseDragged() {
  if(sG.onSlider(mouseX,mouseY)) {
    sG.val = ((mouseX - sG.posx)/((float)(sG.widthx)))*(sG.maxVal-sG.minVal) + sG.minVal;
  }
  if(sPlanets.onSlider(mouseX,mouseY)) {
    sPlanets.val = ((mouseX - sPlanets.posx)/((float)(sPlanets.widthx)))*(sPlanets.maxVal-sPlanets.minVal) + sPlanets.minVal;
  }
  if(sTime.onSlider(mouseX,mouseY)) {
    sTime.val = ((mouseX - sTime.posx)/((float)(sTime.widthx)))*(sTime.maxVal-sTime.minVal) + sTime.minVal;
  }
  if(sSpeed.onSlider(mouseX,mouseY)) {
    sSpeed.val = ((mouseX - sSpeed.posx)/((float)(sSpeed.widthx)))*(sSpeed.maxVal-sSpeed.minVal) + sSpeed.minVal;
  }
  if(sSpeedVar.onSlider(mouseX,mouseY)) {
    sSpeedVar.val = ((mouseX - sSpeedVar.posx)/((float)(sSpeedVar.widthx)))*(sSpeedVar.maxVal-sSpeedVar.minVal) + sSpeedVar.minVal;
  }
  if(sAngleVar.onSlider(mouseX,mouseY)) {
    sAngleVar.val = ((mouseX - sAngleVar.posx)/((float)(sAngleVar.widthx)))*(sAngleVar.maxVal-sAngleVar.minVal) + sAngleVar.minVal;
  }
}


public void setupEverything(){
  //initializing planets
  planets = new ArrayList();
  for(int i = 0; i < NUM_PLANETS; i++){
    if(i == NUM_PLANETS-1){
      Planet tmp = new Planet(WIDTH/5, HEIGHT, maxSize);
      tmp.posx += WIDTH*0.8f;
      planets.add(tmp);
    }
    else{
      Planet tmp = new Planet(WIDTH, HEIGHT, maxSize);
      planets.add(tmp);
    }
  }
  
  //initializing ship and trail
  ship = new Ship();
  
  trail = new ArrayList();
  
  float[] tmp = {ship.curPosX, ship.curPosY};
  trail.add(tmp);
  
  drawTrail(); 
}

boolean isEvolving = false;



int trailColor = 120;

public void drawTrail(){
  for(int i = 0; i < trail.size(); i++){
    fill(color(trailColor));
    float[] tmp = trail.get(i);
    ellipse(tmp[0], tmp[1], 10, 10);
  }
}

public void drawPlanets(){
  for(int i = 0; i < planets.size(); i++){
    Planet tmp = planets.get(i);
    tmp.drawThis(i == planets.size()-1);
  }
}

public void drawButton(Button b){
  stroke(2);
  fill(b.getColor());
  rect(b.posx, b.posy, b.dimx, b.dimy);
  
  textAlign(CENTER, CENTER);
  textSize(32);
  fill(255, 255, 255);
  text(b.text, b.posx + (b.dimx/2), b.posy + (b.dimy/2) - 3);
  
}
public class Button{
  
  private final int OFF_COLOR = color(200);
  private final int ON_COLOR = color(100);
  
  private float posx;
  private float posy;
  private float dimx;
  private float dimy;
  private String text;//it's on you to make sure that the text fits on the button
  private boolean val = false;//starts on false;
  
  public Button(float x, float y, float w, float h, String t){
    posx = x;
    posy = y;
    dimx = w;
    dimy = h;
    text = t;
  }
  public float getx(){
    return posx;
  }
  public float gety(){
    return posy;
  }
  public float getWidth(){
    return dimx;
  }
  public float getHeight(){
    return dimy;
  }
  public String getText(){
    return text;
  }
  public int getColor(){
    if(val){
      return ON_COLOR;
    }
    return OFF_COLOR;
  }
  public boolean getVal(){
    return val; 
  }
  public void click(){
    val = !val;
  }
  /*
  fill(button.getColor());
  */
  
}


public class Planet{
  float posx;
  float posy;
  float size;//diameter and weight value
  
  public Planet(float x, float y, float s){
    posx = x;
    posy = y;
    size = s;
  }
  
  public Planet(int W, int H, int maxSize){
    posx = (float) (Math.random()*(double)W);
    posy = (float) (Math.random()*(double)H);
    size = (float) (Math.random()*(double)maxSize)+20;
  }
  
  public void drawThis(boolean objective){
     if(objective){
       fill(0);
     }
     else{
       fill(180);
     }
     
     //posy++;
     
     ellipse(posx, posy, size, size);
  }
 
}
public class Ship{
  
  float initPosX;
  float initPosY;
  float curPosX;
  float curPosY;
  
  float initSpeed;
  float initAngle;//(between 0 [straight up] and 180 [straight down])
  float size;
  
  float velox;
  float veloy;

  int ix = 10;
  int iy = 360;
  
  int myColor = color(224,255,255);
  
  public final float VARIABILITY_SPEED = Gravitational_Slingshot_5.shipVarSpeed;
  public final float VARIABILITY_ANGLE = Gravitational_Slingshot_5.shipVarAngle;
  
  //public float MAX_SPEED = 5;
  public float MAX_SPEED = Gravitational_Slingshot_5.shipMaxSpeed;
  
  public Ship(){
    initPosX = ix;
    initPosY = iy;
    curPosX = initPosX;
    curPosY = initPosY;
    initSpeed = (float) (1.0f);
    initAngle = (float) (Math.random()*180.0f);
    size = 10;
    
    int mult = 1;
    
    float ang = 90-Math.abs(90-initAngle);
    
    if(initAngle > 90){
      mult = -1;
    }
    
    velox = (ang/90) * initSpeed;
    veloy = (initSpeed-velox) * mult;
  }
  
  public Ship(Ship other){
    initPosX = ix;
    initPosY = iy;
    
    this.curPosX = this.initPosX;
    this.curPosY = this.initPosY;
    
    this.initSpeed = other.initSpeed + (float)Math.random()*VARIABILITY_SPEED - (float)Math.random()*VARIABILITY_SPEED;
    if(this.initSpeed > MAX_SPEED){
      this.initSpeed = MAX_SPEED; 
    }
    this.initAngle = other.initAngle + (float)Math.random()*VARIABILITY_ANGLE - (float)Math.random()*VARIABILITY_ANGLE;
    this.size = other.size;
    
    int mult = 1;
    
    float ang = 90-Math.abs(90-initAngle);
    
    if(initAngle > 90){
      mult = -1;
    }
    
    velox = (ang/90) * initSpeed;
    veloy = (initSpeed-velox) * mult;
  }
  
  public void reset(){//checked sept 20
    curPosX = initPosX;
    curPosY = initPosY;
    
    int mult = 1;
    
    float ang = 90-Math.abs(90-initAngle);
    
    if(initAngle > 90){
      mult = -1;
    }
    
    velox = (ang/90) * initSpeed;
    veloy = (initSpeed-velox) * mult;
  }
  
  public void drawThis(){
    fill(myColor);
    ellipse(curPosX, curPosY, size, size);
  }
  
  public boolean step(int time, ArrayList<Planet> planets, float G){
    
    Ship otha =  new Ship(this);//mutation constructor
    
    boolean retval = false;
    
    if(this.cost(time, planets, G) > otha.cost(time, planets, G)){
      this.initAngle = otha.initAngle;
      this.initSpeed = otha.initSpeed;
      retval = true;
    }
    
    return retval;
  }
  
  public float curCost(int time, ArrayList<Planet> planets, float G){
    float[] endPos = findWhere(time, planets, G);
 
    Planet obj = planets.get(planets.size()-1);
    
    float planetx = obj.posx;
    float planety = obj.posy;
    
    float futx = endPos[0];
    float futy = endPos[1];
    
    float dist = (float) Math.sqrt(Math.pow((planetx - futx), 2) + Math.pow((planety - futy), 2));
    
    return dist;
  }
  
  public float cost(int time, ArrayList<Planet> planets, float G){//should work sept 20
    float[] endPos = findWhere(0, planets, G);
 
    Planet obj = planets.get(planets.size()-1);
    
    float planetx = obj.posx;
    float planety = obj.posy;
    
    float futx = endPos[0];
    float futy = endPos[1];
    
    float dist = (float) Math.sqrt(Math.pow((planetx - futx), 2) + Math.pow((planety - futy), 2));
    
    float minDist = dist;
    
    int i = 0;
    for(i = 0; i < time; i++){
      endPos = findWhere(i, planets, G);
      
      futx = endPos[0];
      futy = endPos[1];
      
      dist = (float) Math.sqrt( Math.pow((planetx - futx), 2) + Math.pow((planety - futy), 2) );
      
      if(dist < minDist){
        minDist = dist; 
      }
      
      updatePos(planets, G);
      updateVelo(planets, G);
    }
    
    reset();
    
    
    
    return minDist*i;
    
  }
  
  public float[] findWhere(int time, ArrayList<Planet> planets, float G){
    
    for(int i = 0; i < time; i++){
      updatePos(planets, G);
      updateVelo(planets, G);
    }
    
    float[] where = {curPosX, curPosY};
    
    reset();
    
    return where;
  }
  
  //THIS IS WHERE I GOT TO SEPT 20 11:26p
  
  public boolean collide(ArrayList<Planet> planets, float G){//used each frame
    
    //for(int i = 0; i < time; i++){
      //float[] selfAtTime = findWhere(time, planets, G);
      
      for(int p = 0; p < planets.size(); p++){
        Planet other = planets.get(p);
        
        float dist = getDist(curPosX, other.posx, curPosY, other.posy);
        
        if(dist < this.size/2 + other.size/2){
          return true;
        }
        
      }
      
    //}
    
    return false;
  }
  
  public float getDist(float x1, float x2, float y1, float y2){
    
    float xdiff = x2-x1;
    float ydiff = y2-y1;
    
    float dist = (float)(Math.sqrt(xdiff*xdiff + ydiff*ydiff));
    
    return dist;
  }
  
  public void updatePos(ArrayList<Planet> planets, float G){
    
    //curPosX += velox;
    //curPosY += veloy; 
    
    if( collide(planets, G) ){
      curPosX -= 0;
      curPosY -= 0;
    }
    
    else{
      curPosX += velox;
      curPosY += veloy; 
    }
  }
  
  public void updateVelo(ArrayList<Planet> planets, float G){
    float accx = 0.0f;
    float accy = 0.0f;
    
    for(int i = 0; i < planets.size(); i++){
        
      Planet other = planets.get(i);
      
      float distx = curPosX - other.posx;
      float disty = curPosY - other.posy;
      
      distx = Math.abs(distx);
      disty = Math.abs(disty);
      
      float totalDist = (float)Math.sqrt( Math.pow(distx, 2.0f) + Math.pow(disty, 2.0f) );
      
      float totalForce = (float) ( (G*other.size*this.size)/( Math.pow(totalDist, 2.0f) ) );
      
      float curAngle = (float)Math.atan((double)(distx/disty));
      
      float forcex = cos(curAngle)*totalForce;
      float forcey = sin(curAngle)*totalForce;

      forcex = totalForce * (distx/(totalDist));
      forcey = totalForce * (disty/(totalDist));

      //forcex = Math.abs(forcex);
      //forcey = Math.abs(forcey);
      
      if(other.posy < this.curPosY){
        forcey*=-1; 
      }
      if(other.posx < this.curPosX){
        forcex*=-1; 
      }
 
      accx += forcex;
      accy += forcey;
    
    }
    
    velox += accx;
    veloy += accy;
    
    //curPosX += velox;
    //curPosY += veloy;
    
  }
    
  
}
public class Slider{
  int posx;//this is the top left corner
  int posy;//this is the top left corner
  int widthx;
  int heighty = 20;
  float maxVal;//value of the slider when it's at the right side
  float minVal;//value of the slider when it's at the left side
  float val;//value that the slider currently has (relative to min/maxVal)
  
  float boxPos;//left edge of the box that slides around
  
  //color bgColor = color(200, 180, 120);
  //color boxColor = color(100, 20, 20);
  int bgColor = color(225);
  int boxColor = color(150);
  
  
  
  public Slider(int px, int py, int wx, float maV, float miV, float v){//user should input central X coordinate
    posx = (int)(px - ((float)wx)/2);
    posy = py;
    widthx = wx;
    
    maxVal = maV;
    minVal = miV;
    val = v;
    if(val < minVal || val > maxVal){
      val = minVal; 
    }
    
    boxPos = posx + val/(maxVal-minVal);
  }
  
  public void drawThis(){
    boxPos = posx + (widthx)*(val/(maxVal-minVal));
    
    fill(bgColor);
    rect(posx, posy, widthx, heighty);
    fill(boxColor);
    rect(boxPos, posy, heighty, heighty);
  }
  
  public boolean onSlider(int mx, int my){
    if(mx >= (posx) && mx <= (posx+widthx)){
      if(my >= posy && my <= posy+heighty){
        return true; 
      }
    }
    return false;
  }
  
  public void drag(float imx, float imy, float cmx, float cmy){//initial mouse x/y, current mouse x/y
    
  }
  
  
  
  
  
}
  public void settings() {  size(1280, 720);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Gravitational_Slingshot_5" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
