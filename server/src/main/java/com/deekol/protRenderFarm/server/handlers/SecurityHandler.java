package com.deekol.protRenderFarm.server.handlers;

import com.deekol.protRenderFarm.server.Service.UserService;
import com.deekol.protRenderFarm.server.domain.UserEntity;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityHandler {
    private final UserService userService;

    public SecurityHandler(UserService userService) {
        this.userService = userService;
    }

    //Авторизация пользователя
    public UserEntity auth(Channel channelClient, String s) throws Exception {
        log.info("Start command: -l, --login");
        UserEntity user = userService.findUser(s);
        if (user != null) {
            log.info("Profile in: " + user.getUsername());
            channelClient.writeAndFlush("Profile in: " + user.getUsername() + System.lineSeparator());
        } else {
            System.out.println("Invalid login or password!");
            channelClient.writeAndFlush("Invalid login or password!" + System.lineSeparator());
        }
        return user;
    }

    //Регистрация нового пользователя
    public void regUser(Channel channelClient, String s) throws Exception {
        log.info("Start command: -r, --reg");
        userService.reg(s);
        log.info("Registered user: " + s.split("\\s", 3)[1]);
        channelClient.writeAndFlush("Registered user: " + s.split("\\s", 3)[1] + System.lineSeparator());
    }
}
