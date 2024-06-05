package zmq.ReqRep;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class server {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] message = socket.recv(0);
                System.out.println("Received request: " + new String(message, ZMQ.CHARSET));

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                String reply = "World";
                socket.send(reply.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
