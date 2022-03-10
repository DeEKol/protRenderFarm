package com.deekol.protRenderFarm.server;

import com.deekol.protRenderFarm.server.handlers.MainHandler;
import com.deekol.protRenderFarm.server.repo.TaskRepo;
import com.deekol.protRenderFarm.server.repo.UserRepo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
Запуск приложения с настройкой SpringBoot(без web'а) и логами.
Настройка запуска сервера на Netty.
Добавлен MainHandler. Подключаюсь к серверу с помощью telnet (PuTTY программы).
 */

@Slf4j
@SpringBootApplication
public class ServerApp implements CommandLineRunner {
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    public ServerApp(UserRepo userRepo, TaskRepo taskRepo) {
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
    }

    @Value("${port}")
    private int port;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(ServerApp.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        //Вывод логов
        log.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
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
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler(userRepo, taskRepo)); //Добавляем декодер и энкодер
                        } //Инициализация клиента
                    });
            //Запуск сервера
            ChannelFuture future = b.bind(port).sync();
            log.info("Starting server on port: 8000");
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
