import java.io.*;
import java.net.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
                    System.out.println("Критична помилка в обчисленнях f(x).");
                }
            });

            Thread gxThread = new Thread(() -> {
                Optional<Double> gxOptional = Computation.compfunc(x,2);
                if (gxOptional.isPresent()) {
                    gx = gxOptional.get();
                    try {
                        out.write("g(x)="+x+"+7: " + gx + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Критична помилка в обчисленнях g(x).");
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

    public static Optional<Double> compfunc(int x_value,int n) {
        switch (n) {
            case 0:
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ie) {
                    // Тут можна обробити некритичну помилку, якщо вона виникла
                    System.err.println("Некритична помилка в обчисленнях f(x).");
                    return Optional.empty();
                }
                // Повертаємо правильне значення f(x)
                return Optional.of(x_value +10.00);

            case 1:
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    // Тут можна обробити некритичну помилку, якщо вона виникла
                    System.err.println("Некритична помилка в обчисленнях g(x).");
                    return Optional.empty();
                }
                attempt--;
                if (attempt != 0)
                    return Optional.empty();
                attempt = CASE1_ATTEMPTS;
                // Повертаємо правильне значення g(x)
                return Optional.of(x_value +5.00);

            case 2:
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ie) {
                    // Тут можна обробити некритичну помилку, якщо вона виникла
                    System.err.println("Некритична помилка в обчисленнях f(x).");
                    return Optional.empty();
                }
                // Повертаємо правильне значення f(x)
                return Optional.of(x_value + 7.00);

            default:
                // Обробка інших варіантів
                try { Thread.currentThread().join(); } catch (InterruptedException ie) {}
                return Optional.empty();
        }
    }
}

