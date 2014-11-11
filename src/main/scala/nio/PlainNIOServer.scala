package nio

import java.io.IOException
import java.net.{InetSocketAddress, ServerSocket}
import java.nio.ByteBuffer
import java.nio.channels.{SocketChannel, SelectionKey, Selector, ServerSocketChannel}

import io.netty.buffer.ByteBuf

/**
 * Asynchronous Networking without Netty
 *
 * Created by chester on 11/11/14.
 *
 */
class PlainNIOServer {
  def serve( port: Int) {
    val serverChannel = ServerSocketChannel.open()

    serverChannel.configureBlocking(false)
    val ss: ServerSocket = serverChannel.socket()
    val address = new InetSocketAddress(port)
    ss.bind(address)
    val selector = Selector.open()
    serverChannel.register(selector, SelectionKey.OP_ACCEPT)
    val msg = ByteBuffer.wrap("Hi!\r\n".getBytes)
    var continue = true
    while (continue) {

      try {
        selector.select()
      }
      catch {
        case ex: IOException => ex.printStackTrace()
          continue = false
      }; //4

      if (continue) {
        import java.util.{Set => JSet}
        val readyKeys: JSet[SelectionKey] = selector.selectedKeys();
        //5
        val iterator = readyKeys.iterator()
        while (iterator.hasNext) {
          val key = iterator.next()
          iterator.remove()

          try {
            if (key.isAcceptable) {
              //6
              val server = key.channel().asInstanceOf[ServerSocketChannel]
              val client: SocketChannel = server.accept()
              client.configureBlocking(false)
              client.register(selector,
                SelectionKey.OP_WRITE |
                  SelectionKey.OP_READ,
                msg.duplicate()) //7

              System.out.println("Accepted connection from " + client)
              if (key.isWritable) {
                //8
                val client = key.channel().asInstanceOf[SocketChannel]
                val buffer = key.attachment().asInstanceOf[ByteBuffer]
                var hasRemaining = buffer.hasRemaining
                while (hasRemaining) {
                  hasRemaining =
                    if (client.write(buffer) == 0) {
                      //9
                      false
                    }
                    else
                      buffer.hasRemaining
                }
                client.close()
              }
            }
          }
          catch {
            case e: IOException =>
              key.cancel()
              try {
                key.channel().close()
              }
              catch {
                case ignore: IOException =>
              }
          }

        }
      }
    }
  }
}
