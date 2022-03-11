package com.deekol.protRenderFarm.server.handlers;

import com.deekol.protRenderFarm.server.service.TaskService;
import com.deekol.protRenderFarm.server.domain.TaskEntity;
import com.deekol.protRenderFarm.server.domain.UserEntity;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

/*
Изменение статуса происходит через задержку исполнения и Math.random, от 1 до 5 минут.
 */

@Slf4j
@Component
public class TaskHandler {
    private final TaskService taskService;

    public TaskHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    //Создание новой задачи
    public void createNewTask(Channel channelClient, UserEntity profile) {
        log.info("Start command: -n, --newtask");
        if (profile != null) {
            TaskEntity taskEntity = new TaskEntity(profile);
            taskService.save(taskEntity);
            log.info("User " + profile.getUsername() + " create new task №" + taskEntity.getId() + "; Status: RENDERING");
            channelClient.write("User " + profile.getUsername() + " create new task №" + taskEntity.getId() + System.lineSeparator());
            channelClient.write("Status: RENDERING" + System.lineSeparator());
            channelClient.flush();
            changeTaskStatus(channelClient, taskEntity);
        } else {
            System.out.println("Need sign in!");
            channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
        }
    }

    //Отправка всех задач
    public void sendAllTasks(Channel channelClient, UserEntity profile) {
        log.info("Start command: -t, --tasks");
        if (profile != null) {
            List<TaskEntity> tasks = taskService.findByUserEntity(profile);
            channelClient.write(profile.getUsername() + "'s tasks:" + System.lineSeparator());
            for(TaskEntity e : tasks) {
                channelClient.write(e.getId() + ", " + e.getStatus() + System.lineSeparator());
            }
            channelClient.flush();
        } else {
            System.out.println("Need sign in!");
            channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
        }
    }

    //Отправка текущих задач
    public void sendCurrentTasks(Channel channelClient, UserEntity profile) {
        log.info("Start command: -c, --currenttasks");
        if (profile != null) {
            List<TaskEntity> tasks = taskService.findByStatusAndUserEntity("RENDERING", profile);
            channelClient.write(profile.getUsername() + "'s current tasks:" + System.lineSeparator());
            for(TaskEntity e : tasks) {
                channelClient.write(e.getId() + ", " + e.getStatus() + System.lineSeparator());
            }
            channelClient.flush();
        } else {
            System.out.println("Need sign in!");
            channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
        }
    }

    //Отправка истории изменения статуса задач
    public void sendStatusHistory(Channel channelClient, UserEntity profile) {
        log.info("Start command: -s, --statushistory");
        if (profile != null) {
            List<TaskEntity> tasks = taskService.findByUserEntity(profile);
            channelClient.write(profile.getUsername() + "'s tasks history:" + System.lineSeparator());
            channelClient.write("№ | status  | start render     | finish render     " + System.lineSeparator());
            for(TaskEntity e : tasks) {
                String fr;
                if (e.getFinishRender() != null) {
                    fr = calOut(e.getFinishRender());
                } else {
                    fr = "Still RENDERING";
                }
                channelClient.write(e.getId() + ", " + e.getStatus() + ", "
                        + calOut(e.getStartRender()) + ", "
                        + fr + System.lineSeparator());
            }
            channelClient.flush();
        } else {
            System.out.println("Need sign in!");
            channelClient.writeAndFlush("Need sign in!" + System.lineSeparator());
        }
    }


    //Метод для смены статуса задачи с RENDERING на COMPLETE
    void changeTaskStatus(Channel channelClient, TaskEntity taskEntity) {
        //Рандомное число от 1 до 5 минут
        int randomFromOneToFiveMinute = (int) (60000 + 240000 * Math.random());
        log.info("RENDERING finish in " + randomFromOneToFiveMinute + " ms");

        //Задержка исполнения (от 1 до 5 минут)
        new java.util.Timer().schedule(
                new TimerTask() {
                    public void run() {
                        taskEntity.setStatus("COMPLETE");
                        taskEntity.setFinishRender(Calendar.getInstance());
                        taskService.save(taskEntity);
                        log.info("Task №" + taskEntity.getId() + " status changed to COMPLETE");
                        channelClient.writeAndFlush("Task №" + taskEntity.getId() + " status changed to COMPLETE" + System.lineSeparator());
                    }
                },
                randomFromOneToFiveMinute);
    }

    //Форматирование времени для отправки клиенту
    private String calOut(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-"
                + cal.get(Calendar.DAY_OF_MONTH) + " "
                + cal.get(Calendar.HOUR) + ":"
                + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);
    }
}
