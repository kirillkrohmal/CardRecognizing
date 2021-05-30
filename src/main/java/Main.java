import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                parseSingle();
                System.exit(0);
            }
            List<Path> paths = listFilesUsingDirectoryStream(args[0]);
            if(paths.size() == 0){
                System.out.println("Изображения не найдены");
                System.exit(1);
            }
            for(Path path : paths){
                InputStream inputStream = Files.newInputStream(path);
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                String fileName = path.getFileName().toString();
                System.out.println("Парсинг файла " + fileName);
                parseBufferedImage(bufferedImage);
                System.out.println("=====================");
            }
        }
        catch (IOException ex){
            System.out.println("Ошибка чтения. Убедитель, что правильно указали путь к директории с изображениями.");
        }
    }

    private static void parseSingle() throws IOException {
        InputStream inputStream = new Main().getClass().getClassLoader().getResourceAsStream("test.png");
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        parseBufferedImage(bufferedImage);
    }

    private static void parseBufferedImage(BufferedImage bufferedImage) throws IOException {
        List<Card> cards = findAllCards(bufferedImage);
        int c = 1;
        for(Card card : cards) {
            System.out.print(card.getRank());
            System.out.print(card.getSuit());
            //ImageIO.write(card.getImage(), "png", new File("card" + c + ".png"));
            c++;
        }
        System.out.println();
    }

    private static List<Card> findAllCards(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        /* Ширина карты составляет примерно 1/9 ширины поля; принудительно уменьшим до 1/10, чтобы исключить пограничные пиксели. */
        int proxyCardWidth = (int) Math.floor(width / 10);

        /* Интересна только нижняя половина карты. */
        int halfOfHeight = (int) Math.floor(height / 2) - 10; // Берем чуть выше, чтобы яркость уменьшалась.

        /* Отступив слева 2,5 ширины карты, попадаем на вертикаль, пересекающую карту. */
        int marginLeftTmp = (int) Math.floor(proxyCardWidth * 2.5);

        /* Находим самый яркий пиксель на этой вертикали. */
        Pixel brightest = getBrightestPixel(bufferedImage, halfOfHeight, marginLeftTmp);

        /* Находим верхнюю границу. */
        int marginTop = brightest.getY();
        Pixel current = brightest;
        for(int i = marginTop - 1; ; i--) {
            current = new Pixel(new Color(bufferedImage.getRGB(marginLeftTmp, i)), marginLeftTmp, i);
            if(current.isDarkerThan(brightest)) break;
            marginTop = current.getY();
        }

        List<Card> cards = new ArrayList<>();
        cards.add(findSingleCard(bufferedImage, proxyCardWidth, brightest, marginTop, marginLeftTmp));
        for(int i = marginLeftTmp + proxyCardWidth + 10; i < width - 2 * proxyCardWidth; i += proxyCardWidth + 10){
            brightest = getBrightestPixel(bufferedImage, halfOfHeight, i);
            if(brightest == null) break;
            cards.add(findSingleCard(bufferedImage, proxyCardWidth, brightest, marginTop, i));
        }
        return cards;
    }

    private static Pixel getBrightestPixel(BufferedImage bufferedImage, int halfOfHeight, int marginLeftTmp) {
        Pixel brightest;
        brightest = null;
        for(int i = halfOfHeight; i < halfOfHeight + 50; i++) {
            Pixel pixel = new Pixel(new Color(bufferedImage.getRGB(marginLeftTmp, i)), marginLeftTmp, i);
            if (pixel.isTooDark()) continue;
            if(brightest != null && pixel.isDarkerThan(brightest)) break;
            brightest = pixel;
        }
        return brightest;
    }

    private static Card findSingleCard(BufferedImage bufferedImage, int proxyCardWidth, Pixel brightest, int marginTop, int marginLeftTmp) {
        /* Находим левую границу карты. */

        int marginLeft = marginLeftTmp;
        for(int i = marginTop; i < marginTop + proxyCardWidth; i++) {
            int currentLeft = scanForLeft(bufferedImage, marginLeftTmp, i, brightest);
            if(currentLeft < marginLeftTmp) marginLeft = currentLeft;
        }

        /* Для распознавания карты достаточно фрагмента размером 40x50 пикселей. */
        return new Card(bufferedImage.getSubimage(marginLeft, marginTop, 40, 50), brightest);
    }

    private static int scanForLeft(BufferedImage bufferedImage, int x, int y, Pixel brightest /*, boolean bothSides */) {
        int marginLeft = x;
        for(int i = x - 1; !new Pixel(new Color(bufferedImage.getRGB(i, y)), i, y).isDarkerThan(brightest); i--) marginLeft--;
        return marginLeft;
    }

    private static List<Path> listFilesUsingDirectoryStream(String dir) throws IOException {
        List<Path> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if(Files.isDirectory(path)) continue;
                if(!path.getFileName().toString().endsWith(".png")) continue;
                fileList.add(path); //.getFileName().toString());
            }
        }
        //List<String> sortedList = new ArrayList<String>(fileList);
        //Collections.sort(sortedList);
        return fileList;
    }
}





