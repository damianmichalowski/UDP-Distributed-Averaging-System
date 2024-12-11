import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class DAS {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: Invalid number of arguments.\nUsage: java DAS <port> <number>");
            return;
        }

        int port;
        int number;

        try {
            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Arguments <port> and <number> must be integers.");
            return;
        }

        try {
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println("MASTER is running on port: " + port);
            runMaster(socket, number);
        } catch (SocketException e) {
            System.out.println("SLAVE is running");
            runSlave(port, number);
        }
    }

    private static void runMaster(DatagramSocket socket, int initialNumber) {
        System.out.println("MASTER listening on port:  " + socket.getLocalPort());

        List<Integer> numbers = new ArrayList<>();
        numbers.add(initialNumber);

        byte[] receiveData = new byte[1024];

        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                if (receivedMessage.equals("-1")) {
                    System.out.println("Received -1. Sent to local network and shutting down");
                    sendMessageToLocalNetwork(socket, socket.getLocalPort(), "-1");
                    socket.close();
                    break;
                } else if (receivedMessage.equals("0")) {
                    int average = calculateAverage(numbers);
                    System.out.println("Calculated average: " + average);
                    sendMessageToLocalNetwork(socket, socket.getLocalPort(), String.valueOf(average));
                } else {
                    try {
                        int receivedValue = Integer.parseInt(receivedMessage);
                        System.out.println("Received: " + receivedValue);
                        numbers.add(receivedValue);
                    } catch (NumberFormatException e) {
                        System.out.println("Message not sent, because not integer value: " + receivedMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int calculateAverage(List<Integer> numbers) {
        double sum = 0;
        for (Integer number : numbers) {
            if (number != 0) {
                sum = sum + number;
            }
        }
        return (int) Math.floor(sum / numbers.size());

    }

    private static void sendMessageToLocalNetwork(DatagramSocket socket, int port, String message) throws Exception {
        InetAddress messageAddress = InetAddress.getByName("255.255.255.255");
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, messageAddress, port);
        socket.send(packet);
    }

    private static void runSlave(int port, int number) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress localHost = InetAddress.getLocalHost();
            String message = String.valueOf(number);
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localHost, port);
            clientSocket.send(sendPacket);

            System.out.println("Sent: " + number + " to port: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
