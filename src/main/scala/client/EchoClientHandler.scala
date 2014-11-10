package client

import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.CharsetUtil

/**
 *
 * Created by chester on 11/9/14.
 */
@Sharable
class EchoClientHandler extends SimpleChannelInboundHandler[ByteBuf] {

  override def channelActive(ctx: ChannelHandlerContext) {
    ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8))
  }
  override def channelRead0(ctx: ChannelHandlerContext , in : ByteBuf ) {
      println("Client received: " + in.toString(CharsetUtil.UTF_8))
     // println("Client received: " + ByteBufUtil.hexDump(in))
  }
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
      cause.printStackTrace()
      ctx.close()
  }
}
