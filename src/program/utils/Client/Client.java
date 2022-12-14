package program.utils.Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class Client {

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static FileInputStream in;
    private static DataOutputStream out;
    private static String folderPath;
    private static String fileName;
    private static String trg_path;

    public static BufferedImage clone(BufferedImage bufferImage) {
        ColorModel colorModel = bufferImage.getColorModel();
        WritableRaster raster = bufferImage.copyData(null);
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }


    public static BufferedImage addPepperSaltNoise(BufferedImage srcImg, double param)
        {
        int total = srcImg.getWidth() * srcImg.getHeight();
        int count = (int)(total * (1 - param));
        BufferedImage trgImg = clone(srcImg);
        Random random = new Random();
        Color black = Color.BLACK;
        Color white = Color.WHITE;
        for(int i = 0; i < count; i++)
        {
            int randomX = random.nextInt(srcImg.getWidth());
            int randomY = random.nextInt(srcImg.getHeight());
            int newColor = (random.nextInt(2) + 1) % 2 == 0 ? black.getRGB() : white.getRGB();
            trgImg.setRGB(randomX, randomY, newColor);
        }
        return trgImg;
    }

    public static File fileImageWithNoise(String srcPath, String trgPath, double p) throws IOException {
        File file = new File(srcPath);
        BufferedImage image = ImageIO.read(file);
        BufferedImage image2 = addPepperSaltNoise(image, p);
        File outputFile = new File(trgPath);
        ImageIO.write(image2, "jpg", outputFile);
        return outputFile;
    }


    public static void main(String[] args) {
        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                clientSocket = new Socket("localhost", 4004); // этой строкой мы запрашиваем
                // у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                folderPath = "C:\\Users\\kozlo\\IdeaProjects\\IMGthroughSocket\\src\\Pictures\\";
                fileName = "Naruto.jpg";
                out = new DataOutputStream(clientSocket.getOutputStream());


                byte[] sendBytes = new byte[1024];
                int length = 0;
                int counter = 0;
                while(true) {
                    System.out.println("Enter command image/exit: ");
                    String word = reader.readLine();
                    if (word.equals("exit")){
                        out.writeUTF(word);
                        out.flush();
                        break;
                    }
                    else if (word.equals("image"))
                    {
                        out.writeUTF(word);
                        out.flush();
                        System.out.println("Picture path: "+folderPath+fileName+ "\nChange path yes/no?: ");
                        String choice = reader.readLine();
                        if (choice.equals("yes")){
                            System.out.println("Enter new path: ");
                            fileName = reader.readLine();
                            System.out.println("New path now is : "+folderPath+fileName);
                        }
                        else {
                            System.out.println("Path not changed.");
                        }

                        File file = fileImageWithNoise(folderPath+fileName, folderPath+"noise"+fileName, 0.5);
                        in = new FileInputStream(file);
                        while ((length = in.read(sendBytes, 0, sendBytes.length)) > 0) {
                            counter +=length;
                            out.write(sendBytes, 0, length);
                            out.flush();

                        }
                        System.out.println(counter);

                        in.close();
                        //удаление файла с шумом для экономии места
                        System.out.println("Finish");
                    }

                }

            } finally { // в любом случае необходимо закрыть сокет и потоки
                clientSocket.close();
                in.close();
                out.close();
                System.out.println("Клиент был закрыт...");
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}