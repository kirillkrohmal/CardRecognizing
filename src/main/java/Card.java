import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Card {

    private String rank;
    private char suit = '?';
    private List<Integer> spaces = new ArrayList<>(); // отступы слева
    private List<Boolean> gapes = new ArrayList<>(); // наличие пустот
    private BufferedImage bufferedImage;

    public Card(BufferedImage card, Pixel brightest) {
        this.bufferedImage = card;
        for(int i = 0; i < card.getHeight(); i++){
            int count = 0;
            for(int j = 0; j < card.getWidth(); j++) {
                Pixel pixel = new Pixel(card, j, i);
                if(pixel.isDarkerThan(brightest)) break;
                count++;
            }
            if(count == card.getWidth() && i < 10) continue;
            if(count == 0) continue;
            spaces.add(count);
            boolean up = false;
            boolean down = false;
            for(int j = count; j < card.getWidth(); j++){
                Pixel pixel = new Pixel(card, j, i);
                if(up && pixel.isDarkerThan(brightest)) down = true;
                if(!up && !pixel.isDarkerThan(brightest)) up = true;
            }
            gapes.add(up && down);
        }
        Color suitColor = new Color(card.getRGB(16, 40));
        boolean isRed = suitColor.getRed() > suitColor.getBlue() * 2 && suitColor.getRed() > suitColor.getGreen() * 2;
        if(isRed) suit = new Color(card.getRGB(16, 34)).equals(brightest.getColor()) ? 'h' : 'd';
        else{
            int space1 = spaces.get(28);
            int space2 = spaces.get(29);
            int space3 = spaces.get(30);
            suit = space1 > space2 && space2 > space3 ? 's' : 'c';
        }
    }

    String getRank(){
        int[] spaces = new int[this.spaces.size()];
        int i = 0;
        for(int space : this.spaces){
            spaces[i] = space;
            i++;
        }
        boolean[] gapes = new boolean[this.gapes.size()];
        i = 0;
        for(boolean gape : this.gapes){
            gapes[i] = gape;
            i++;
        }
        if(spaces[0] > spaces[4] && spaces[4] > spaces[9] && spaces[9] > spaces[14] && spaces[14] > spaces[19] && gapes[9] && gapes[19] && !gapes[14]) return "A";
        if(spaces[0] == spaces[4] && spaces[4] == spaces[9] && spaces[9] == spaces[14] && spaces[14] == spaces[20] && gapes[4] && gapes[20]) return "K";
        if(spaces[1] == spaces[4] && spaces[4] == spaces[9] && spaces[1] > spaces[20]) return "J";
        if(spaces[10] > spaces[3] && spaces[15] == spaces[10] && spaces[10] == spaces[20]) return "10";
        if(spaces[0] > spaces[1] && spaces[1] > spaces[2] && spaces[2] > spaces[3] && gapes[6] && gapes[7] && gapes[8] && (!gapes[15] || !gapes[16])) return "9";
        if(!gapes[3] && gapes[6] && gapes[7] && gapes[8] && !gapes[11] && gapes[15] && !gapes[20]) return "8";
        if(spaces[5] > spaces[0] && spaces[15] < spaces[5] && spaces[20] < spaces[15]) return "7";
        if(!gapes[11] &&gapes[14] && gapes[15] && gapes[16] && gapes[17]) return "6";
        if(spaces[5] == spaces[1] && spaces[11] < spaces[14] && spaces[17] < spaces[14]) return "5";
        if(spaces[11] < spaces[0] && spaces[20] > spaces[11]  && spaces[20] == spaces[17] && gapes[11] && !gapes[17]) return "4";
        if(spaces[6] > spaces[1] && spaces[6] > spaces[10]) return "3";
        if(spaces[4] < spaces[8] && spaces[12] < spaces[8] && spaces[18] < spaces[12] && gapes[5]) return "2";
        return "Q";
    }

    BufferedImage getImage(){return bufferedImage;}

    char getSuit() {
        return suit;
    }
}
