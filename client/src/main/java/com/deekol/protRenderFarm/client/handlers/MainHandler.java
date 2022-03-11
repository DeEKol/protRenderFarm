package com.deekol.protRenderFarm.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//todo: cкрыть ввод пароля при авторизации/регистрации (picocli или средствами java с открытием отдельного потока)

public class MainHandler extends SimpleChannelInboundHandler<String> {

    //Активация канала
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    //Чтение канала
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) {
        System.out.println(s);
    }

    //Ошибка канала
    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
