package program.utils.Server;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import program.utils.Pixel;


public class Server {

    private static Socket clientSocket; //сокет для общения
    private static ServerSocket server; // серверсокет
    //private static BufferedReader in; // поток чтения из сокета
    //private static BufferedWriter out; // поток записи в сокет
    private static DataInputStream in;
    private static FileOutputStream out;
    public static BufferedImage clone(BufferedImage bufferImage) {
        ColorModel colorModel = bufferImage.getColorModel();
        WritableRaster raster = bufferImage.copyData(null);
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }

    private static BufferedImage MedianFilter(BufferedImage srcImg,int [][] mask)
    {
        //Pixel [][] pixels = new Pixel [3][3];
        int size = 9;
        Pixel [] pixels = new Pixel [size];
        BufferedImage trgImg = clone(srcImg);
        int height = trgImg.getHeight();
        int width = trgImg.getWidth();
        for(int i=0;i<height;++i) {
            for (int j = 0; j < width; ++j) {

//                pixels[0][0] =new Pixel(j-1,i-1,new Color(trgImg.getRGB(j-1,i-1)));
//                pixels[0][1] =new Pixel(j,i-1,new Color(trgImg.getRGB(j,i-1)));
//                pixels[0][2] =new Pixel(j+1,i-1,new Color(trgImg.getRGB(j+1,i-1)));
//                pixels[1][0] =new Pixel(j-1,i,new Color(trgImg.getRGB(j-1,i)));
//                pixels[1][1] =new Pixel(j,i,new Color(trgImg.getRGB(j,i)));
//                pixels[1][2] =new Pixel(j+1,i,new Color(trgImg.getRGB(j+1,i)));
//                pixels[2][0] =new Pixel(j-1,i+1,new Color(trgImg.getRGB(j-1,i+1)));
//                pixels[2][1] =new Pixel(j,i+1,new Color(trgImg.getRGB(j,i+1)));
//                pixels[2][2] =new Pixel(j+1,i+1,new Color(trgImg.getRGB(j+1,i+1)));

                pixels[0] =new Pixel(j-1,i-1,new Color(trgImg.getRGB(j-1,i-1)));
                pixels[1] =new Pixel(j,i-1,new Color(trgImg.getRGB(j,i-1)));
                pixels[2] =new Pixel(j+1,i-1,new Color(trgImg.getRGB(j+1,i-1)));
                pixels[3] =new Pixel(j-1,i,new Color(trgImg.getRGB(j-1,i)));
                pixels[4] =new Pixel(j,i,new Color(trgImg.getRGB(j,i)));
                pixels[5] =new Pixel(j+1,i,new Color(trgImg.getRGB(j+1,i)));
                pixels[6] =new Pixel(j-1,i+1,new Color(trgImg.getRGB(j-1,i+1)));
                pixels[7] =new Pixel(j,i+1,new Color(trgImg.getRGB(j,i+1)));
                pixels[8] =new Pixel(j+1,i+1,new Color(trgImg.getRGB(j+1,i+1)));

                int[] redArray = new int[size];
                int[] greenArray = new int[size];
                int[] blueArray = new int[size];

                for(int k = 0; k < size; ++k){
                    redArray[k] = pixels[k].c.getRed();
                    greenArray[k] = pixels[k].c.getGreen();
                    blueArray[k] = pixels[k].c.getBlue();
                }
                Arrays.sort(redArray);
                Arrays.sort(greenArray);
                Arrays.sort(blueArray);

                int medianRed = redArray[size/2];
                int medianGreen = greenArray[size/2];
                int medianBlue = blueArray[size/2];

                Color newColor = new Color(medianRed, medianGreen, medianBlue);
                trgImg.setRGB(j, i, newColor.getRGB());
            }
        }


        return trgImg;
    }

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4004); // серверсокет прослушивает порт 4004
                System.out.println("Сервер запущен!"); // хорошо бы серверу
                //   объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться
                try { // установив связь и воссоздав сокет для общения с клиентом можно перейти
                    in = new DataInputStream(clientSocket.getInputStream());
                    String path="C:\\Users\\kozlo\\OneDrive\\Рабочий стол\\картинки\\";

                    byte[] inputByte = new byte[1024];
                    int length = 0;
                    int counter = 0;
                    String word = "";

                        while (true)
                        {
                            word =DataInputStream.readUTF(in);
                            if(word.equals("exit"))
                            {
                                System.out.println("not connect");
                                break;
                            }
                            if(word.equals("image"))
                            {
                                String new_path = path + String.valueOf(counter)+".jpg";
                                String new_path2 = path + "noise"+String.valueOf(counter)+".jpg";
                                out = new FileOutputStream(new_path);
                                System.out.println("Путь до сохраненного изображения :"+"\n"+new_path);
                                while ((length = in.read(inputByte, 0, inputByte.length)) > 0)
                                {
                                    System.out.println(length);
                                    out.write(inputByte, 0, length);
                                    out.flush();
                                }
                                System.out.println("Финиш");
                                out.close();
                            }
                            counter++;
                            //System.out.println("Counter = " + counter);
                        }


                    //System.out.println("Received " + image.getHeight() + "x" + image.getWidth() );
                    //ImageIO.write(image, "jpg", new File("C:\\Users\\user\\Desktop\\test_soket\\leo.jpg"));

                    /*
                    while(true) {
                        String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                        System.out.println(word);
                        // не долго думая отвечает клиенту
                        out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                        out.flush(); // выталкиваем все из буфера
                        if (word.equals("exit"))
                            break;
                    }
                */
                } finally { // в любом случае сокет будет закрыт
                    clientSocket.close();
                    System.out.println("Сокет закрыт!");
                    // потоки тоже хорошо бы закрыть
                    in.close();
                    //out.close();
                }
            } finally {
                System.out.println("Сервер закрыт!");
                server.close();
            }
        } catch (IOException e) {
            //System.out.println("Чето пошло не так");
            //System.err.println(e);
        }
    }
}