package cn.netty.websocketchat.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author hexiagen
 * @date 2018/5/21 16:11
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            String path = location.toURI() + "WebsocketChatClient.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
        }


    }


    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        if (wsUri.equalsIgnoreCase(fullHttpRequest.getUri())) {
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(fullHttpRequest)) {
                send100Continue(channelHandlerContext);                               //3
            }

            RandomAccessFile file = new RandomAccessFile(INDEX, "r");

            HttpResponse response = new DefaultFullHttpResponse(fullHttpRequest.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF_8");

            boolean keepAlive = HttpHeaders.isKeepAlive(fullHttpRequest);

            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

            }
            channelHandlerContext.writeAndFlush(response);
            if (channelHandlerContext.pipeline().get(SslHandler.class) == null) {
                channelHandlerContext.writeAndFlush(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                channelHandlerContext.writeAndFlush(new ChunkedNioFile(file.getChannel()));
            }

            ChannelFuture future = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);


            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }


            file.close();
        }


    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("client："+channel.remoteAddress()+ "异常");
    }
}