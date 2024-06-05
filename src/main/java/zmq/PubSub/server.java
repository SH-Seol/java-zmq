package zmq.PubSub;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class server {
    public static void main(String[] args) {
        System.out.println("Publishing updates at weather server...");

        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.PUB);
            socket.bind("tcp://*:5556");

            Random rand = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                int zipcode = rand.nextInt(1, 100000);
                int temperature = rand.nextInt(-80, 135);
                int relhumidity = rand.nextInt(10, 60);

                String message =   String.format("%d %d %d",zipcode,temperature,relhumidity);
                socket.send(message.getBytes(ZMQ.CHARSET), 0);

                
            }

        }
    }
}
