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
  color bgColor = color(225);
  color boxColor = color(150);
  
  
  
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