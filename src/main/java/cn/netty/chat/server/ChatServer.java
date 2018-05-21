package cn.netty.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author hexiagen
 * @date 2018/5/21 14:46
 */
public class ChatServer {

    private final int prot;

    public ChatServer(int prot) {
        this.prot = prot;
    }


    public void run() throws Exception {
        EventLoopGroup boossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("simpleChatServer 启动了");

            ChannelFuture future = bootstrap.bind(prot).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            workerGroup.shutdownGracefully();
            boossGroup.shutdownGracefully();

            System.out.println("服务器关闭了");
        }


    }

    public static void main(String[] args) throws Exception {
        int port;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }


        new ChatServer(port).run();
    }


}
