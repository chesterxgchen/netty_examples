package server

import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

/**
 *
 * Created by chester on 11/8/14.
 */
@Sharable                                                               //1
class EchoServerHandler extends ChannelInboundHandlerAdapter {

  override def channelRead(ctx:ChannelHandlerContext , msg: Any) {
    val in: ByteBuf = msg.asInstanceOf[ByteBuf]
    println("Server received: " + in.toString(CharsetUtil.UTF_8)) //2
    ctx.write(in); //3
  }

  override def channelReadComplete(ctx: ChannelHandlerContext) {
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)                       //4
        .addListener(ChannelFutureListener.CLOSE)
    }

  override def exceptionCaught(ctx:ChannelHandlerContext, cause:Throwable) {
    cause.printStackTrace(); //5
    ctx.close(); //6
  }
}