import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    //private static BufferedReader in; // поток чтения из сокета
    //private static BufferedWriter out; // поток записи в сокет
    private static FileInputStream in;
    private static DataOutputStream out;
    private static String path;

    private String getIMG(String path)
    {
      return "0";
    }

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
                in = new FileInputStream(new File(path));
                out = new DataOutputStream(clientSocket.getOutputStream());


                byte[] sendBytes = new byte[1024];
                int length = 0;
                int counter = 0;
                while(true) {
                    System.out.println("Введите команду: ");
                    String word = reader.readLine();
                    if (word.equals("exit")){
                        out.writeUTF(word);
                        out.flush();
                        break;
                    }
                    else if (word.equals("image"))
                    {
                        System.out.println("Текущий путь картинки: "+path+"\nВведите новый путь: ");
                        //path = reader.readLine();
                        out.writeUTF(word);
                        out.flush();
                        while ((length = in.read(sendBytes, 0, sendBytes.length)) > 0) {
                            counter +=length;
                            out.write(sendBytes, 0, length);
                            out.flush();
                            System.out.println(length);
                        }
                        System.out.println("Counter = "+counter);
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