package zmq.ReqRep;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class client {
    public static void main(String[] args) {

        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555");

            for(int req = 0; req < 10; req++) {
                System.out.println("Sending request" + req);
                socket.send("hello".getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
