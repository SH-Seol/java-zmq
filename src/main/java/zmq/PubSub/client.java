package zmq.PubSub;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class client {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.SUB);

            System.out.println("Colleecting updates from weather server...");
            socket.connect("tcp://*:5556");

            String zip_filter = (args.length > 0) ? args[0] : "10001";
            socket.subscribe(zip_filter.getBytes(ZMQ.CHARSET));

            int totalTemp = 0;
            for(int updateNbr = 0; updateNbr < 20; updateNbr++) {
                String string = socket.recvStr(0);
                String[] message = string.split(" ");
                int zipcode = Integer.parseInt(message[0]);
                int temperature = Integer.parseInt(message[1]);
                int relhumidity = Integer.parseInt(message[2]);

                totalTemp += temperature;

                String formattedMessage1 = String.format("Received tempearure for zipcode '%s' was %d F", zip_filter, temperature);
                String formattedMessage2 = String.format("Average tempearure for zipcode '%s' was %d F", zip_filter, totalTemp/(updateNbr+1));

                System.out.println(formattedMessage1);
                System.out.println(formattedMessage2);
            }
        }
    }
}
