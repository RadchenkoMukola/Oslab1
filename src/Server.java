import java.io.*;
import java.net.*;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Server {

    private static ServerSocket server;

    public static void main(String[] args) {
        try {
            server = new ServerSocket(12345);
            System.out.println("Сервер працює!");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Клієнт підключився!");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
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
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    private Double fx = null;
    private Double gx = null;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            int x = Integer.parseInt(in.readLine());
            System.out.println("Сервер отримав x = " + x);

            // Створення окремих потоків для f(x) та g(x)
            Thread fxThread = new Thread(() -> {
                Optional<Double> fxOptional = Computation.compfunc(x,0);
                if (fxOptional.isPresent()) {
                    fx = fxOptional.get();
                    try {
                        out.write("f(x)="+x+"+10: " + fx + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.err.println("Критична помилка в обчисленнях f(x).");
                }
            });

            Thread gxThread = new Thread(() -> {
                Optional<Double> gxOptional = Computation.compfunc(x,1);
                if (gxOptional.isPresent()) {
                    gx = gxOptional.get();
                    try {
                        out.write("g(x)="+x+"+7: " + gx + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.err.println("Критична помилка в обчисленнях g(x).");
                }
            });

            fxThread.start();
            gxThread.start();

            // Очікування завершення обчислень f(x) та g(x)
            try {
                fxThread.join();
                gxThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int fxi = fx.intValue();
            int gxi = gx.intValue();

            // Обчислення f(x) ^ g(x)
            int result = calculateResult(fxi, gxi);
            out.write("Успішне обчислення f(x) ^ g(x): " + result + "\n");
            out.write("Кількість некритичних помилок f(x): " + Computation.getNonCriticalErrorsfx() + "\n");
            out.write("Кількість некритичних помилок g(x): " + Computation.getNonCriticalErrorsgx() + "\n");

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int calculateResult(int fx, int gx) {
        // Побітовий XOR f(x) та g(x)
        return fx ^ gx;
    }
}
class Computation {
    final static int CASE1_ATTEMPTS = 3;
    static int attempt = CASE1_ATTEMPTS;

    static int nonCriticalErrorsfx = 0;

    static int nonCriticalErrorsgx = 0;
    static Random random = new Random();

    public static Optional<Double> compfunc(int x_value, int n) {
        switch (n) {
            case 0:
                while (attempt > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        attempt--;
                        if (random.nextDouble() < 0.3) {
                            nonCriticalErrorsfx++;
                            System.err.println("Некритична помилка в обчисленнях f(x).");
                        }
                        // Повертаємо правильне значення f(x)
                        return Optional.of(x_value + 10.00);
                    } catch (InterruptedException ie) {
                    }
                }
                return Optional.empty();

            case 1:
                while (attempt > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        attempt--;
                        if (random.nextDouble() < 0.5) {
                            nonCriticalErrorsgx++;
                            System.err.println("Некритична помилка в обчисленнях g(x).");
                        }
                        // Повертаємо правильне значення f(x)
                        else return Optional.of(x_value + 7.00);
                    } catch (InterruptedException ie) {
                    }
                }
                return Optional.empty();

            default:
                // Обробка інших варіантів
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException ie) {
                }
                return Optional.empty();
        }
    }
    public static int getNonCriticalErrorsfx() {
        return nonCriticalErrorsfx;
    }
    public static int getNonCriticalErrorsgx() {
        return nonCriticalErrorsgx;
    }
}



