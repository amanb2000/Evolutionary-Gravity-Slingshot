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
  
  color myColor = color(224,255,255);
  
  public final float VARIABILITY_SPEED = Gravitational_Slingshot_5.shipVarSpeed;
  public final float VARIABILITY_ANGLE = Gravitational_Slingshot_5.shipVarAngle;
  
  //public float MAX_SPEED = 5;
  public float MAX_SPEED = Gravitational_Slingshot_5.shipMaxSpeed;
  
  public Ship(){
    initPosX = ix;
    initPosY = iy;
    curPosX = initPosX;
    curPosY = initPosY;
    initSpeed = (float) (1.0);
    initAngle = (float) (Math.random()*180.0);
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
    float accx = 0.0;
    float accy = 0.0;
    
    for(int i = 0; i < planets.size(); i++){
        
      Planet other = planets.get(i);
      
      float distx = curPosX - other.posx;
      float disty = curPosY - other.posy;
      
      distx = Math.abs(distx);
      disty = Math.abs(disty);
      
      float totalDist = (float)Math.sqrt( Math.pow(distx, 2.0) + Math.pow(disty, 2.0) );
      
      float totalForce = (float) ( (G*other.size*this.size)/( Math.pow(totalDist, 2.0) ) );
      
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