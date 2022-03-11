package com.deekol.protRenderFarm.server.Service;

import com.deekol.protRenderFarm.server.domain.UserEntity;
import com.deekol.protRenderFarm.server.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*
Секретный ключ выступает как тестовый.
 */

@Slf4j
@Service
public class UserService {
    //todo: переделать секретный ключ (тестовый вариант)
    private static final SecretKeySpec sk = new SecretKeySpec("1212121212121212".getBytes(), "AES");

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    //Регистрация пользователя
    public void reg(String s) throws Exception {
        UserEntity userEntity = new UserEntity(s.split("\\s", 3)[1], encrypt(s.split("\\s", 3)[2].trim()));
        userRepo.save(userEntity);
    }

    //Поиск пользователя в бд
    public UserEntity findUser(String s) throws Exception {
        UserEntity user = userRepo.findByUsername(s.split("\\s", 3)[1]);
        if (user != null && decrypt(user.getPassword()).equals(s.split("\\s", 3)[2].trim())) {
            return userRepo.findByUsername(s.split("\\s", 3)[1]);
        } else {
            return null;
        }
    }

    //todo: создать кодер/декодер в utils
    //Шифрование пароля
    String encrypt(String pass) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, UserService.sk);
        byte[] bytes = cipher.doFinal(pass.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }
    //Расшифровка пароля
    String decrypt(String pass) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, UserService.sk);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(pass));
        return new String(bytes);
    }
}
