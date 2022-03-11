package com.deekol.protRenderFarm.server.handlers;

import com.deekol.protRenderFarm.server.domain.UserEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/*
Главный обработчик.
Изменение статуса происходит через задержку исполнения и Math.random, от 1 до 5 минут.
 */

@Slf4j
public class MainHandler extends SimpleChannelInboundHandler<String> {
    private final SecurityHandler securityHandler;
    private final TaskHandler taskHandler;

    public MainHandler(SecurityHandler securityHandler, TaskHandler taskHandler) {
        this.securityHandler = securityHandler;
        this.taskHandler = taskHandler;
    }

    private Channel channelClient;
    private UserEntity profile;

    //Активация канала
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connected");
        channelClient = ctx.channel();
        channelClient.writeAndFlush("Connected" + System.lineSeparator());
        helper();
    }

    //Чтение канала
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Message received: " + s);
        //Вызов списка команд
        if (s.trim().equals("-h") || s.trim().equals("--help")) {
            helper();
        }
        //Авторизация пользователя
        else if (s.startsWith("-l") || s.startsWith("--login")) {
            profile = securityHandler.auth(channelClient, s);
        }
        //Регистрация нового пользователя
        else if (s.startsWith("-r") || s.startsWith("--reg")) {
            securityHandler.regUser(channelClient, s);
        }
        //Создание новой задачи
        else if (s.trim().equals("-n") || s.trim().equals("--newtask")) {
            taskHandler.createNewTask(channelClient, profile);
        }
        //Отправка всех задач
        else if (s.trim().equals("-t") || s.trim().equals("--tasks")) {
            taskHandler.sendAllTasks(channelClient, profile);
        }
        //Отправка текущих задач
        else if (s.trim().equals("-c") || s.trim().equals("--currenttasks")) {
            taskHandler.sendCurrentTasks(channelClient, profile);
        }
        //Отправка истории изменения статуса задач
        else if (s.trim().equals("-s") || s.trim().equals("--statushistory")) {
            taskHandler.sendStatusHistory(channelClient, profile);
        } else {
            helper();
        }
    }

    //Ошибка канала
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.info("Client " + profile.getUsername() + " leave");
        ctx.close();
    }

    //Метод отправляет help сообщение клиенту
    void helper() {
        channelClient.writeAndFlush("Usage:" + System.lineSeparator()
                + "  -h, --help           Show help message" + System.lineSeparator()
                + "  -l, --login          Log in (-l user password)" + System.lineSeparator()
                + "  -r, --reg            Registration (-r user password)" + System.lineSeparator()
                + "  -n, --newtask        Add new task" + System.lineSeparator()
                + "  -t, --tasks          Get all task" + System.lineSeparator()
                + "  -c, --currenttasks   Get tasks where status is RENDERING" + System.lineSeparator()
                + "  -s, --statushistory  Get history change tasks" + System.lineSeparator()
     );
    }
}
