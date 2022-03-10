package com.deekol.protRenderFarm.server.handlers;

import com.deekol.protRenderFarm.server.domain.TaskEntity;
import com.deekol.protRenderFarm.server.domain.UserEntity;
import com.deekol.protRenderFarm.server.repo.TaskRepo;
import com.deekol.protRenderFarm.server.repo.UserRepo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/*
Главный обработчик.
Секретный ключ выступает как тестовый.
Изменение статуса происходит через задержку исполнения и Math.random, от 1 до 5 минут
 */

@Slf4j
public class MainHandler extends SimpleChannelInboundHandler<String> {
    //todo: переделать секретный ключ (тестовый вариант)
    private SecretKeySpec sk = new SecretKeySpec("1212121212121212".getBytes(), "AES");

    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    public MainHandler(UserRepo userRepo, TaskRepo taskRepo) {
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
    }

    private Channel channelClient;
    private UserEntity profile;

    //Активация канала
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client connected");
        channelClient = ctx.channel();
        channelClient.writeAndFlush("Connected" + System.lineSeparator());
        helper();
    }

    //Чтение канала
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Message received: " + s);
        if (s.trim().equals("-h") || s.trim().equals("--help")) {
            helper();
        } else if (s.startsWith("-l") || s.startsWith("--login")) {
            System.out.println("Start command: -l, --login");
            String[] commandSplit = s.split("\\s", 3); //Разбиваем полученную строку
            UserEntity user = userRepo.findByUsername(commandSplit[1]); //Ищем юзера в бд
            if (user != null && decrypt(user.getPassword(), sk).equals(commandSplit[2].trim())) {
                profile = user;
                log.info("Prifile in: " + profile.getUsername());
                channelClient.writeAndFlush("Prifile in: " + profile.getUsername() + System.lineSeparator());
            } else {
                System.out.println("Invalid login or password!");
                channelClient.writeAndFlush("Invalid login or password!" + System.lineSeparator());
            }
        } else if (s.startsWith("-r") || s.startsWith("--reg")) {
            System.out.println("Start command: -r, --reg");
            String[] commandSplit = s.split("\\s", 3);
            UserEntity userEntity = new UserEntity(commandSplit[1], encrypt(commandSplit[2].trim(), sk));
            userRepo.save(userEntity);
            log.info("Registered user: " + commandSplit[1]);
            channelClient.writeAndFlush("Registered user: " + commandSplit[1] + System.lineSeparator());
        } else if (s.trim().equals("-n") || s.trim().equals("--newtask")) {
            System.out.println("Start command: -n, --newtask");
            if (profile != null) {
                TaskEntity taskEntity = new TaskEntity(profile);
                taskRepo.save(taskEntity);
                System.out.println("User " + profile.getUsername() + " create new task №" + taskEntity.getId());
                System.out.println("Status: RENDERING");
                channelClient.writeAndFlush("User " + profile.getUsername() + " create new task №" + taskEntity.getId() + System.lineSeparator());
                channelClient.writeAndFlush("Status: RENDERING" + System.lineSeparator());
                changeTaskStatus(taskEntity);
            } else {
                System.out.println("Need sign in!");
                channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
            }
        } else if (s.trim().equals("-t") || s.trim().equals("--tasks")) {
            System.out.println("Start command: -t, --tasks");
            if (profile != null) {
                List<TaskEntity> tasks = taskRepo.findByUserEntity(profile);
                channelClient.writeAndFlush(profile.getUsername() + "'s tasks:" + System.lineSeparator());
                for(TaskEntity e : tasks) {
                    channelClient.writeAndFlush(e.getId() + ", " + e.getStatus() + System.lineSeparator());
                }
            } else {
                System.out.println("Need sign in!");
                channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
            }
        } else if (s.trim().equals("-c") || s.trim().equals("--currenttasks")) {
            System.out.println("Start command: -c, --currenttasks");
            if (profile != null) {
                List<TaskEntity> tasks = taskRepo.findByStatus("RENDERING");
                channelClient.writeAndFlush(profile.getUsername() + "'s current tasks:" + System.lineSeparator());
                for(TaskEntity e : tasks) {
                    channelClient.writeAndFlush(e.getId() + ", " + e.getStatus() + System.lineSeparator());
                }
            } else {
                System.out.println("Need sign in!");
                channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
            }
        } else if (s.trim().equals("-s") || s.trim().equals("--statushistory")) {
            System.out.println("Start command: -s, --statushistory");
            if (profile != null) {
                List<TaskEntity> tasks = taskRepo.findByUserEntity(profile);
                channelClient.writeAndFlush(profile.getUsername() + "'s tasks history:" + System.lineSeparator());
                channelClient.writeAndFlush("№ | status  | start render     | finish render     " + System.lineSeparator());
                for(TaskEntity e : tasks) {
                    String fr;
                    if (e.getFinishRender() != null) {
                        fr = calOut(e.getFinishRender());
                    } else {
                        fr = "Still RENDERING";
                    }
                    channelClient.writeAndFlush(e.getId() + ", " + e.getStatus() + ", "
                            + calOut(e.getStartRender()) + ", "
                            + fr + System.lineSeparator());
                }
            } else {
                System.out.println("Need sign in!");
                channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
            }
        } else {
            helper();
        }
    }

    //Ошибка канала
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.info("Client leave");
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

    //Метод для смены статуса задачи с RENDERING на COMPLETE
    public void changeTaskStatus(TaskEntity taskEntity) {
        //Рандомное число от 1 до 5 минут
        int randomFromOneToFiveMinute = (int) (60000 + 240000 * Math.random());
        System.out.println("RENDERING finish in " + randomFromOneToFiveMinute + " ms");

        //Задержка исполнения (от 1 до 5 минут)
        new java.util.Timer().schedule(
                new TimerTask() {
                    public void run() {
                        taskEntity.setStatus("COMPLETE");
                        taskEntity.setFinishRender(Calendar.getInstance());
                        taskRepo.save(taskEntity);
                        System.out.println("Task№" + taskEntity.getId() + " status changed to COMPLETE");
                        channelClient.writeAndFlush("Task №" + taskEntity.getId() + " status changed to COMPLETE" + System.lineSeparator());
                    }
                },
                randomFromOneToFiveMinute);
    }

    //Шифрование пароля
    String encrypt(String pass, SecretKeySpec sk) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        byte[] bytes = cipher.doFinal(pass.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }
    //Расшифровка пароля
    String decrypt(String pass, SecretKeySpec sk) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(pass));
        return new String(bytes);
    }

    //Время для отправки клиенту
    String calOut(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-"
                + cal.get(Calendar.DAY_OF_MONTH) + " "
                + cal.get(Calendar.HOUR) + ":"
                + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);
    }
}
