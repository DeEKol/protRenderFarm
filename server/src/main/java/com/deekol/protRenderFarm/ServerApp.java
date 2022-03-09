package com.deekol.protRenderFarm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
Запуск приложения с настройкой SpringBoot(без web'а) и логами.
Настройка запуска сервера на Netty
 */

@SpringBootApplication
public class ServerApp implements CommandLineRunner {
    @Value("${port}")
    int port;

    private static Logger LOG = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ServerApp.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        //Вывод логов
        LOG.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }

        //Настройка и запуск сервера
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        try {
            //Преднастройка сервера
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder()); //Добавляем декодер и энкодер
                        } //Инициализация клиента
                    });
            //Запуск сервера
            ChannelFuture future = b.bind(port).sync();
            LOG.info("Starting server on port: 8000");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Закрытие пулов потоков
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
