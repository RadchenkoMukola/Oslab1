import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Manager {
    static double fxResult;
    static double gxResult;
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введіть значення x: ");
            int x = scanner.nextInt();

            Socket fxSocket = new Socket("localhost", 12346);
            Socket gxSocket = new Socket("localhost", 12347);

            BufferedReader fxIn = new BufferedReader(new InputStreamReader(fxSocket.getInputStream()));
            BufferedWriter fxOut = new BufferedWriter(new OutputStreamWriter(fxSocket.getOutputStream()));

            BufferedReader gxIn = new BufferedReader(new InputStreamReader(gxSocket.getInputStream()));
            BufferedWriter gxOut = new BufferedWriter(new OutputStreamWriter(gxSocket.getOutputStream()));



            Thread fxThread = new Thread(() -> {
                try {
                    fxOut.write(x + "\n");
                    fxOut.flush();

                    fxResult = Double.parseDouble(fxIn.readLine());
                    System.out.println("Результат обчислення f(x): " + fxResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread gxThread = new Thread(() -> {
                try {
                    gxOut.write(x + "\n");
                    gxOut.flush();

                    gxResult = Double.parseDouble(gxIn.readLine());
                    System.out.println("Результат обчислення g(x): " + gxResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            fxThread.start();
            gxThread.start();

            fxThread.join();
            gxThread.join();

           int fxInt = (int) fxResult;
           int gxInt = (int) gxResult;

            int result = fxInt ^ gxInt;
            System.out.println("Результат обчислення f(x) ^ g(x): " + result);

            fxSocket.close();
            gxSocket.close();
            fxOut.close();
            gxOut.close();

        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }
}


