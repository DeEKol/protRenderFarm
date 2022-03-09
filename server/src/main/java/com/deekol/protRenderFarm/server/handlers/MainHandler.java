package com.deekol.protRenderFarm.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/*
Главный обработчик.
 */

@Slf4j
public class MainHandler extends SimpleChannelInboundHandler<String> {
    private Channel channelClient;

    //Активация канала
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client connected");
        channelClient = ctx.channel();
        channelClient.writeAndFlush("Connected" + System.lineSeparator());
        channelClient.writeAndFlush(helper + System.lineSeparator());
    }

    //Чтение канала
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Message received: " + s);
    }

    //Ошибка канала
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Client leave");
        ctx.close();
    }

    //help сообщение
    String helper = "Usage:" + System.lineSeparator() + "  -h, --help        Show help message" + System.lineSeparator()
            + "  -l, --login       Log in (-l user password)" + System.lineSeparator()
            + "  -r, --reg         Registration (-r user password)" + System.lineSeparator()
            + "  -n, --newtask     Add new task" + System.lineSeparator()
            + "  -t, --tasks       Get all task" + System.lineSeparator();
}
