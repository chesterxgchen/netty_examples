package nio

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress
import java.nio.charset.Charset
import io.netty.util.concurrent.AbstractEventExecutorGroup

/**
 */
class NettyNIOServer {
  def server(port: Int) {
    val buf: ByteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")))
    // Note here I have to set the type as
    // AbstractEventExecutorGroup, otherwise, I will get
    /**
     * ambiguous reference to overloaded definition,
      [error] both method shutdownGracefully in class MultithreadEventExecutorGroup of type (x$1: Long, x$2: Long, x$3: java.util.concurrent.TimeUnit)io.netty.util.concurrent.Future[_]
      [error] and  method shutdownGracefully in class AbstractEventExecutorGroup of type ()io.netty.util.concurrent.Future[_]
      [error] match expected type ?
      [error]       group.shutdownGracefully.sync
     */
    val group: AbstractEventExecutorGroup = new NioEventLoopGroup
    try {
      val b: ServerBootstrap = new ServerBootstrap
      b.group(new NioEventLoopGroup, new NioEventLoopGroup)
        .channel(classOf[NioServerSocketChannel])
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer[SocketChannel] {
        override def initChannel(ch: SocketChannel) {
          ch.pipeline.addLast(new ChannelInboundHandlerAdapter {
            override def channelActive(ctx: ChannelHandlerContext) {
              ctx.write(buf.duplicate).addListener(ChannelFutureListener.CLOSE)
            }
          })
        }
      })
      val f: ChannelFuture = b.bind.sync
      f.channel.closeFuture.sync
    }
    finally {
      group.shutdownGracefully.sync
    }
  }
}
