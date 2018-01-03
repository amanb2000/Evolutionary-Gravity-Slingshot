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

Slider sG = new Slider(WIDTH/2, HEIGHT/7, WIDTH-100, 30, 0.1, 2);
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

color BGC = color(245);

public static final int numSteps = 100;

Button go = new Button(WIDTH/2 - (300/2), HEIGHT-75, 300, 60, "Start Evolution!");

public void setup(){
 
  //General settings
  frameRate(FPS);
  background(BGC);
  size(1280, 720);
  //size(2000, 2000);
  smooth();
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
void mouseClicked(){
  if(screen == 0){
    screen++;
  }
  else if(screen == 1){
    //if(){//code for checking if mouse is within button dimensions
      
    //}
    screen++; 
  }
  
}
void mousePressed() {
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

void mouseDragged() {
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


void setupEverything(){
  //initializing planets
  planets = new ArrayList();
  for(int i = 0; i < NUM_PLANETS; i++){
    if(i == NUM_PLANETS-1){
      Planet tmp = new Planet(WIDTH/5, HEIGHT, maxSize);
      tmp.posx += WIDTH*0.8;
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