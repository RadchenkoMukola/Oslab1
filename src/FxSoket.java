import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;


public class FxSoket {
    private static ServerSocket server;

    public static void main(String[] args) {
        try {
            server = new ServerSocket(12346);
            System.out.println("Сервер Fx працює!");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Клієнт Fx підключився!");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                int x = Integer.parseInt(in.readLine());
                System.out.println("Сервер Fx отримав x = " + x);

                Optional<Double> fxOptional = Computation.compfunc(x, 0);
                if (fxOptional.isPresent()) {
                    double gx = fxOptional.get();
                    out.write(gx + "\n");
                } else {
                    out.write("Критична помилка в обчисленнях f(x).\n");
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
        return Computation.getNonCriticalErrorsfx();
    }
}

