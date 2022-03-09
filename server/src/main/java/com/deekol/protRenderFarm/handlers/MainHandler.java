package com.deekol.protRenderFarm.handlers;

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
}
