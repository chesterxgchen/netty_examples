package client


import java.net.InetSocketAddress

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelFuture, ChannelInitializer, EventLoopGroup}

/**
 * Listing 2.5  of <i>Netty in Action</i>
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
object EchoClient {
  def main(args: Array[String]) {
    if (args.length != 2) {
      System.err.println("Usage: " + classOf[EchoClient].getSimpleName + " <host> <port>")
      return
    }
    val host: String = args(0)
    val port: Int = args(1).toInt
    new EchoClient(host, port).start()
  }
}

class EchoClient(private val host:String, private val port: Int) {
  def start() {
    val group: EventLoopGroup = new NioEventLoopGroup
    try {
      val b: Bootstrap = new Bootstrap
      b.group(group).channel(classOf[NioSocketChannel])
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer[SocketChannel] {
                              override def initChannel(ch: SocketChannel) {
                                ch.pipeline.addLast(new EchoClientHandler())
                              }
                            }
                    )

      val f: ChannelFuture = b.connect.sync
      f.channel.closeFuture.sync
    }
    finally {
      group.shutdownGracefully.sync
    }
  }

}


