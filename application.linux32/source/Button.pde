public class Button{
  
  private final color OFF_COLOR = color(200);
  private final color ON_COLOR = color(100);
  
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
  public color getColor(){
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