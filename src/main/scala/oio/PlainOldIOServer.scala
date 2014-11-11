package oio

import java.io.{OutputStream, IOException}
import java.net.ServerSocket
import java.nio.charset.Charset

/**
 * Blocking Networking without Netty
 *
 * Created by chester on 11/11/14.
 */
class PlainOldIOServer {
 def serve(port:Int) {
    val socket = new ServerSocket(port)
    try {
      while(true) {
        val clientSocket = socket.accept()
        System.out.println("Accepted connection from " + clientSocket)

        new Thread(new Runnable() {                             //3
          override def run(): Unit = {
            var out:OutputStream = null
            try {
              out = clientSocket.getOutputStream
              out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")))
              out.flush()
              clientSocket.close();
            } catch {
              case e: IOException => e.printStackTrace();
            }


            try {
              clientSocket.close()
            }
            catch {
              case e:IOException =>  //ignore on close
            }
          }
        }).start()

      }
    } catch  {
      case e:IOException =>
        e.printStackTrace()
    }
 }

}
