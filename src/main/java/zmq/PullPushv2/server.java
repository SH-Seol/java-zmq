package zmq.PullPushv2;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class server {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
            publisher.bind("tcp://*:5557");
            ZMQ.Socket collector = context.createSocket(ZMQ.PULL);
            collector.bind("tcp://*:5558");

            while(!Thread.currentThread().isInterrupted()) {
                String message = collector.recvStr();
                System.out.println("Server: publishing update => "+ message);
                publisher.send(message.getBytes(ZMQ.CHARSET),0);
            }
        }
    }
}
