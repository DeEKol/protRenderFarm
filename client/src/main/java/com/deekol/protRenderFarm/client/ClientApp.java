package com.deekol.protRenderFarm.client;

import com.deekol.protRenderFarm.client.handlers.MainHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
Клиентское приложение на Netty.
Не стал подключать SpringBoot ради application.properties
 */

public class ClientApp {
    //todo: вынести host и port в properties
    private final String host;
    private final int port;

    public ClientApp(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        new ClientApp("localhost", 8000).run();
    }

    public void run() {
        //Настройка и запуск клиента
        EventLoopGroup group = new NioEventLoopGroup(1);

        try {
            //Преднастройка клиента
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler()); //Добавляем декодер, энкодер и обработчик
                        } //Инициализация клиента
                    });
            //Подключение
            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                channel.writeAndFlush(in.readLine() + "\r\n"); //Отправка сообщения на сервер
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Закрытие пулов потоков
            group.shutdownGracefully();
        }
    }
}
