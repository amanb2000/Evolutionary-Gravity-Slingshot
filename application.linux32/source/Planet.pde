

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