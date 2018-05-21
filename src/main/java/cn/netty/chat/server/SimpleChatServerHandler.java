package cn.netty.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author hexiagen
 * @date 2018/5/21 14:48
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {


    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel incoming = ctx.channel();

        for (Channel channel : channels) {

            channel.writeAndFlush("[server]-" + incoming.remoteAddress() + "用户加入 \n");
        }

        channels.add(ctx.channel());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        for (Channel ch : channels) {
            ch.writeAndFlush("[server]-" + incoming.remoteAddress() + "离开 \n");
        }

        channels.remove(ctx.channel());

    }
    /**
     * 接受客户端发来的信息，将此信息进行转发给别的用户，进行聊天
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

        Channel incoming = channelHandlerContext.channel();

        for (Channel chan : channels) {
            if (chan != incoming) {
                chan.writeAndFlush("[" + chan.remoteAddress() + "]说:" + s + "\n");
            } else {
                chan.writeAndFlush("[我说]" + s + "\n");
            }

        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("simplechatClient :" + incoming.remoteAddress() + "在线");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        System.out.println("simpleChatClient:" + channel.remoteAddress() + "掉线");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();

        System.out.println("simpleChatClient:" + channel.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();


    }
}
