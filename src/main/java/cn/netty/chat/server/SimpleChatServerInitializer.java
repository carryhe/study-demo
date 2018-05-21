package cn.netty.chat.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author hexiagen
 * @date 2018/5/21 15:07
 * <p>
 * SimpleChatServerInitializer 用来增加多个的处理类到 ChannelPipeline 上，包括编码、解码、SimpleChatServerHandler 等。
 */
public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        channelPipeline.addLast("decoder", new StringDecoder());
        channelPipeline.addLast("encoder", new StringEncoder());
        channelPipeline.addLast("handler", new SimpleChatServerHandler());

        System.out.println("simpleChatClient:" + socketChannel.remoteAddress() + "连接上");

    }
}
