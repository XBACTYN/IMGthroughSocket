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

import javax.imageio.ImageIO;


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

    private static File MedianFilter(BufferedImage srcImg,int [][] mask, String trgPath) throws IOException {
        // маска квадратная
        int n = mask.length;
        int size = 0;
        for (int[] ints : mask) {
            for (int j = 0; j < n; ++j) {
                size += ints[j];
            }
        }
        Pixel [] pixels = new Pixel [size];
        BufferedImage trgImg = clone(srcImg);
        int height = trgImg.getHeight();
        int width = trgImg.getWidth();
        // идем по изображению
        for(int i=1;i<height-1;++i) {
            for (int j = 1; j < width - 1; ++j) {
                int cnt = 0;
                // идем по маске в изображении
                for (int k = -n/2; k <= n/2; ++k) {
                    for (int l = -n / 2; l <= n / 2; ++l) {
                        for (int r = 0; r < mask[k + n / 2][l + n / 2]; ++r) {
                            pixels[cnt] = new Pixel(j - l, i - k, new Color(trgImg.getRGB(j - l, i - k)));
//                            System.out.println("position: "+"("+k+";"+l+")");
//                            System.out.println("cnt: "+cnt+" valye: "+pixels[cnt].c);
                            cnt++;
                        }
                    }
                }


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

        File outputFile = new File(trgPath);
        ImageIO.write(trgImg, "jpg", outputFile);
        return outputFile;
    }

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4004); // серверсокет прослушивает порт 4004
                System.out.println("Server online!"); // хорошо бы серверу
                //   объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться
                try { // установив связь и воссоздав сокет для общения с клиентом можно перейти
                    in = new DataInputStream(clientSocket.getInputStream());
                    String folderPath = "C:\\Users\\kozlo\\IdeaProjects\\IMGthroughSocket\\src\\ServersPictures\\";

                    int length;
                    int counter = 0;
                    String word;

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
                                byte[] inputByte = new byte[1024];
                                String new_path = folderPath+ "noise" +counter + ".jpg";
                                out = new FileOutputStream(new_path);
                                System.out.println("Save path :"+"\n"+new_path);
                                int len = inputByte.length;
                                while ((length = in.read(inputByte, 0, len)) >0)
                                {

                                    out.write(inputByte, 0, length);
                                    out.flush();
                                    System.out.println(counter);
                                    if(length!=1024)
                                        break;
                                }
                                System.out.println("Before filter");
                                out.close();

                                String new_path2 = folderPath + "filtred"+counter+".jpg";
                                File file = new File(new_path);
                                BufferedImage noiseImg = ImageIO.read(file);
                                int mask0 [][] = {{1,1,1},{1,1,1},{1,1,1}};
                                int mask1 [][] = {{0,1,0},{1,1,1},{0,1,0}};
                                int mask2 [][] = {{0,1,0},{1,3,1},{0,1,0}};
                                int mask3 [][] = {{0,0,1,0,0},{0,0,2,0,0},{1,2,1,2,1},{0,0,2,0,0},{0,0,1,0,0}};
                                File file2 = MedianFilter(noiseImg, mask2, new_path2);

                                // тест на обработку несколько раз для лучшего качества
                                ++counter;
                                String new_path3 = folderPath + "filtred"+counter+".jpg";
                                File file3 = MedianFilter(noiseImg, mask0, new_path3);

                                BufferedImage filtredImg = ImageIO.read(file2);
                                ++counter;
                                String new_path4 = folderPath + "filtred"+counter+".jpg";
                                File file4 = MedianFilter(filtredImg, mask3, new_path4);

                                BufferedImage filtred2Img = ImageIO.read(file4);
                                ++counter;
                                String new_path5 = folderPath + "filtred"+counter+".jpg";
                                File file5 = MedianFilter(filtred2Img, mask1, new_path5);
                                System.out.println("Finish");
                                out.close();

                            }
                            counter++;
                            //System.out.println("Counter = " + counter);
                        }

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