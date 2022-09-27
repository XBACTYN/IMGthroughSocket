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
    //private static BufferedReader in; // поток чтения из сокета
    //private static BufferedWriter out; // поток записи в сокет
    private static FileInputStream in;
    private static DataOutputStream out;
    private static String path;
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
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        BufferedImage image2 = addPepperSaltNoise(image, p);
        File outputFile = new File(trgPath);
        ImageIO.write(image2, "jpg", outputFile);
        return outputFile;
    }

    // сделать изображению с шумом путь, чтоб не затиралось

    // сделать медианный фильтр
    // входные параметры srcImage, bool[][] mask
    // на выход отфильтрованное изображение


    public static void main(String[] args) {
        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                clientSocket = new Socket("localhost", 4004); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // писать туда же
                //out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                path = "C:\\Users\\user\\Desktop\\Стеганография-2022\\Файлы для лабораторных\\Lab1\\leo.jpg";
                //in = new FileInputStream(new File(path));
                out = new DataOutputStream(clientSocket.getOutputStream());


                byte[] sendBytes = new byte[1024];
                int length = 0;
                int counter = 0;
                while(true) {
                    System.out.println("Введите команду image/exit: ");
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
                        System.out.println("Текущий путь изначальной картинки: "+path+"\nВвести новый путь yes/no?: ");
                        String choice = reader.readLine();
                        if (choice.equals("yes")){
                            System.out.println("Введите путь: ");
                            path = reader.readLine();
                            System.out.println("Новый путь изначальной картинки : "+path);
                        }
                        else {
                            System.out.println("Путь не меняется.");
                        }
                        trg_path =path;
                        trg_path= trg_path.replaceFirst(".jpg","_noise.jpg");
                        File file = fileImageWithNoise(path, trg_path, 0.5);
                        in = new FileInputStream(file);
                        while ((length = in.read(sendBytes, 0, sendBytes.length)) > 0) {
                            out.write(sendBytes, 0, length);
                            out.flush();
                        }

                        in.close();
                        System.out.println("Финиш");
                    }

                }

            /*

                while(true) {
                    System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                    // если соединение произошло и потоки успешно созданы - мы можем
                    //  работать дальше и предложить клиенту что то ввести
                    // если нет - вылетит исключение
                    String word = reader.readLine(); // ждём пока клиент что-нибудь
                    // не напишет в консоль

                    out.write(word + "\n"); // отправляем сообщение на сервер
                    out.flush();
                    String serverWord = in.readLine(); // ждём, что скажет сервер
                    System.out.println(serverWord); // получив - выводим на экран
                    if(word.equals("exit"))
                        break;
                }
                */

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