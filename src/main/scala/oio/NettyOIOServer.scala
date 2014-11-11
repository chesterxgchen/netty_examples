package oio

import java.net.InetSocketAddress
import java.nio.charset.Charset

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel._
import io.netty.channel.oio.OioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.oio.OioServerSocketChannel

/**
 * Blocking Networking with Old IO
 *
 */
class NettyOIOServer {

  def server(port: Int) {
    val buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")))
    val group: EventLoopGroup = new OioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(group) //2
        .channel(classOf[OioServerSocketChannel])
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer[SocketChannel]() {
        override def initChannel(ch: SocketChannel) {
          ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
              override def channelActive(ctx: ChannelHandlerContext) {
                ctx.writeAndFlush(buf.duplicate())
                  .addListener(ChannelFutureListener.CLOSE); //5
              }
            }
          )
        }
      })

      val f: ChannelFuture = b.bind().sync() //6
      f.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully().sync()
    }

  }


}
