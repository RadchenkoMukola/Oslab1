import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class GxSoket {
    private static ServerSocket server;

    public static void main(String[] args) {
        try {
            server = new ServerSocket(12347);
            System.out.println("Сервер Gx працює!");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Клієнт Gx підключився!");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                int x = Integer.parseInt(in.readLine());
                System.out.println("Сервер Gx отримав x = " + x);

                Optional<Double> gxOptional = Computation.compfunc(x, 1);
                if (gxOptional.isPresent()) {
                    double gx = gxOptional.get();
                    out.write(gx + "\n");
                } else {
                    out.write("Критична помилка в обчисленнях g(x).\n");
                }


                out.flush();
                clientSocket.close();
                in.close();
                out.close();
            }

        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static int getNonCriticalErrors() {
        return Computation.getNonCriticalErrorsgx();
    }
}