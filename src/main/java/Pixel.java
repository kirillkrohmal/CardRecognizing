import java.awt.Color;
import java.awt.image.BufferedImage;

public class Pixel {
    private Color color;
    private int x;
    private int y;
    private final static int TOO_DARK = 127;

    Pixel(Color color, int x, int y){
        this.color = color;
        this.x = x;
        this.y = y;
    }

    Pixel(BufferedImage bufferedImage, int x, int y){
        this.color = new Color(bufferedImage.getRGB(x, y));
        this.x = x;
        this.y = y;
    }

    boolean isDarkerThan(Pixel other){
        return this.getRed() < other.getRed() && this.getGreen() < other.getGreen() && this.getBlue() < other.getBlue();
    }

    boolean isTooDark(){
        return this.getRed() < TOO_DARK && this.getGreen() < TOO_DARK && this.getBlue() < TOO_DARK;
    }

    int getRed(){return this.color.getRed();}
    int getGreen(){return this.color.getGreen();}
    int getBlue(){return this.color.getBlue();}
    int getX(){return this.x;}
    int getY(){return this.y;}
    Color getColor(){return this.color;}
}
