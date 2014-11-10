package echo.server

import java.net.InetSocketAddress

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelFuture, ChannelInitializer, EventLoopGroup}

/**
 *
 * Created by chester on 11/8/14.
 */

class EchoServer(private val port: Int) {



  def start(): Unit = {

     def chInitializer = new ChannelInitializer[SocketChannel]() {
         override def initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(new EchoServerHandler())
          }
     }

    val group:EventLoopGroup = new NioEventLoopGroup()  //3
    try {
      val  b = new ServerBootstrap()
      b.group(group)                                                  //4
      .channel(classOf[NioServerSocketChannel])                  //5
      .localAddress(new InetSocketAddress(port))              //6
      .childHandler(chInitializer)

      val f:ChannelFuture = b.bind().sync()                              //8
      f.channel().closeFuture().sync()                               //9
    } finally {
      group.shutdownGracefully().sync();                              //10
    }
  }

}
object EchoServer {

  def main(args: Array[String]): Unit = {
    val port = if (args.length > 0) args(0).toInt else 8888
    new EchoServer(port).start()
  }
}