import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DatabaseNode {
    int key;
    int value;
    int port;
    String formatted;
    boolean isAlive = true;

    List<String> connected = new ArrayList<>();
    List<String> neighbours = new ArrayList<>();
    ServerSocket serverSocket;

    DatabaseNode() {
    }

    public int getValue() {
        return value;
    }


    public void setKV(int key, int value) {
        this.value = value;
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void createSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        formatted = "localhost" + ":" + port;
        System.out.println("SERWER O PORCIE " + port);
    }

    public int getKey() {
        return key;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public String getFormatted() {
        return formatted;
    }

    public static void main(String[] args) throws IOException {
        DatabaseNode databaseNode = new DatabaseNode();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-tcpport":
                    databaseNode.setPort(Integer.parseInt(args[++i]));
                    databaseNode.createSocket(databaseNode.port);
                    break;
                case "-record":
                    String[] newRecord = args[++i].split(":");
                    databaseNode.setKV(Integer.parseInt(newRecord[0]), Integer.parseInt(newRecord[1]));
                    break;
                case "-connect":
                    String connects = args[++i];
                    databaseNode.connected.add(connects);
                    System.out.println("dodano połaczenie: " + connects);
                    String[] gatewayArray = connects.split(":");
                    String gateway = gatewayArray[0];
                    int port = Integer.parseInt(gatewayArray[1]);
                    Socket netSocket = new Socket(gateway, port);
                    PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                    out.println("neighbour " + databaseNode.getFormatted());
                    out.close();
                    in.close();
                    netSocket.close();
                    break;
            }
        }
        while (true) {
            Socket klient = databaseNode.getServerSocket().accept();
            BufferedReader inKlient = new BufferedReader(new InputStreamReader(klient.getInputStream()));
            PrintWriter outKlient = new PrintWriter(klient.getOutputStream(), true);
            String command = inKlient.readLine();
            String[] order = command.split(" ");
            switch (order[0]) {
                case "set-value":
                    String[] KV = order[1].split(":");
                    String result = "ERROR";
                    System.out.println("Szukamy wartosci dla klucza: " + KV[0]);
                    System.out.println("KEY: "+KV[0]);
                    System.out.println("MY KEY: "+databaseNode.getKey());
                    if (databaseNode.getKey() == Integer.parseInt(KV[0])) {
                        databaseNode.setValue(Integer.parseInt(KV[1]));
                        outKlient.println("OK");
                        break;
                    } else
                        for (int i = 0; i < databaseNode.connected.size(); i++) {
                            if (databaseNode.isAlive) {
                                String[] gatewayArray = databaseNode.connected.get(i).split(":");
                                String gateway = gatewayArray[0];
                                int port = Integer.parseInt(gatewayArray[1]);
                                Socket netSocket = new Socket(gateway, port);
                                PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                                System.out.println("połączono z " + gateway + ":" + port);
                                out.println("set-value " + order[1]);
                                String response = in.readLine();
                                System.out.println("odpowiedz: " + response);
                                if (!response.equals("ERROR"))
                                    result = response;
                                out.close();
                                in.close();
                                netSocket.close();
                            }
                        }
                    System.out.println("Sending " + result);
                    outKlient.println(result);
                    break;
                case "get-value":
                    result = "ERROR";
                    System.out.println("Szukamy wartosci dla klucza: " + order[1]);
                    if (databaseNode.getKey() == Integer.parseInt(order[1])) {
                        result = databaseNode.getKey() + ":" + databaseNode.getValue();
                        outKlient.println(result);
                        break;
                    } else
                        for (int i = 0; i < databaseNode.connected.size(); i++) {
                            if (databaseNode.isAlive) {
                                String[] gatewayArray = databaseNode.connected.get(i).split(":");
                                String gateway = gatewayArray[0];
                                int port = Integer.parseInt(gatewayArray[1]);
                                Socket netSocket = new Socket(gateway, port);
                                PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                                System.out.println("połączono z " + gateway + ":" + port);
                                out.println("get-value " + order[1]);
                                String response = in.readLine();
                                System.out.println("odpowiedz: " + response);
                                if (!response.equals("ERROR")) {
                                    result = response;
                                    break;
                                }
                                out.close();
                                in.close();
                                netSocket.close();
                            }
                        }
                    System.out.println("Sending " + result);
                    outKlient.println(result);
                    break;
                case "find-key":
                    result = "ERROR";
                    System.out.println("Szukamy serwera dla klucza: " + order[1]);
                    if (databaseNode.getKey() == Integer.parseInt(order[1])) {
                        result = databaseNode.formatted;
                        outKlient.println(result);
                        break;
                    } else
                        for (int i = 0; i < databaseNode.connected.size(); i++) {
                            if (databaseNode.isAlive) {
                                String[] gatewayArray = databaseNode.connected.get(i).split(":");
                                String gateway = gatewayArray[0];
                                int port = Integer.parseInt(gatewayArray[1]);
                                Socket netSocket = new Socket(gateway, port);
                                PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                                System.out.println("połączono z " + gateway + ":" + port);
                                out.println("find-key " + order[1]);
                                String response = in.readLine();
                                System.out.println("odpowiedz: " + response);
                                if (!response.equals("ERROR")) {
                                    result = response;
                                    break;
                                }
                                out.close();
                                in.close();
                                netSocket.close();
                            }
                        }
                    outKlient.println(result);
                    break;
                case "get-max":
                    int maxKey = databaseNode.getKey();
                    int maxValue = databaseNode.getValue();
                    System.out.println("Szukamy maksimum");
                    for (int i = 0; i < databaseNode.connected.size(); i++) {
                        if (databaseNode.isAlive) {
                            String[] gatewayArray = databaseNode.connected.get(i).split(":");
                            String gateway = gatewayArray[0];
                            int port = Integer.parseInt(gatewayArray[1]);
                            Socket netSocket = new Socket(gateway, port);
                            PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                            System.out.println("połączono z " + gateway + ":" + port);
                            out.println("get-max");
                            String response = in.readLine();
                            System.out.println(response);
                            String[] data = response.split(":");
                            if (maxValue < Integer.parseInt(data[1])) {
                                maxKey = Integer.parseInt(data[0]);
                                maxValue = Integer.parseInt(data[1]);
                                System.out.println("current max: " + maxValue);
                            }

                            out.close();
                            in.close();
                            netSocket.close();
                        }
                    }
                    result = maxKey + ":" + maxValue;
                    outKlient.println(result);
                    break;
                case "get-min":
                    int minKey = databaseNode.getKey();
                    int minValue = databaseNode.getValue();
                    System.out.println("Szukamy minimum");
                    for (int i = 0; i < databaseNode.connected.size(); i++) {
                        if (databaseNode.isAlive) {
                            String[] gatewayArray = databaseNode.connected.get(i).split(":");
                            String gateway = gatewayArray[0];
                            int port = Integer.parseInt(gatewayArray[1]);
                            Socket netSocket = new Socket(gateway, port);
                            PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                            System.out.println("połączono z " + gateway + ":" + port);
                            out.println("get-min");
                            String response = in.readLine();
                            System.out.println(response);
                            String[] data = response.split(":");
                            if (minValue > Integer.parseInt(data[1])) {
                                minKey = Integer.parseInt(data[0]);
                                minValue = Integer.parseInt(data[1]);
                                System.out.println("current min: " + minValue);
                            }

                            out.close();
                            in.close();
                            netSocket.close();
                        }
                    }
                    result = minKey + ":" + minValue;
                    outKlient.println(result);
                    break;
                case "new-record":
                    KV = order[1].split(":");
                    databaseNode.setKV(Integer.parseInt(KV[0]), Integer.parseInt(KV[1]));
                    outKlient.println("OK");
                    break;
                case "terminate":
                    databaseNode.isAlive = false;
                    for (int i = 0; i < databaseNode.neighbours.size(); i++) {
                        String[] gatewayArray = databaseNode.neighbours.get(i).split(":");
                        String gateway = gatewayArray[0];
                        int port = Integer.parseInt(gatewayArray[1]);
                        Socket netSocket = new Socket(gateway, port);
                        PrintWriter out = new PrintWriter(netSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
                        System.out.println("połączono z " + gateway + ":" + port + " w celu zakończenia współpracy");
                        out.println("remove " + "localhost:" + databaseNode.port);
                        out.close();
                        in.close();
                        netSocket.close();
                    }
                    outKlient.println("OK");
                    System.exit(0);
                    break;
                case "neighbour":
                    databaseNode.neighbours.add(order[1]);
                    System.out.println("Dodano sąsiada");
                    break;
                case "remove":
                    databaseNode.connected.remove(order[1]);
                    System.out.println("zakończono współprace z " + order[1]);
                    break;
                default:
                    System.out.println(command);
                    outKlient.println("niepoprawny argument");
            }
        }

    }
}
