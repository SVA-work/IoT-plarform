package test.controller;

import test.DTO.Message;
import test.entity.Device;
import test.library.AbstractHttpMappingHandler;
import test.library.annotation.*;
import test.library.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

// не должно быть никакой реализации, только вызов функций из класов service
public class HelloHttpHandler extends AbstractHttpMappingHandler {
    //  Эти переменные пока static(на данный момент они одинаковые для всех пользователей).
    private static Map<String, Device> userDevices = new HashMap<>();
    private static Message currentMessage;
    private static boolean userRegistration = false;
    private static boolean userVerified = false;
    public HelloHttpHandler(JsonParser parser) {
        super(parser);
    }

    @Get("/test/get/userData")
    public DefaultFullHttpResponse userData() {
        String login = currentMessage.getLogin();
        String password = currentMessage.getPassword();

        String responseContent = "{login: '" + login + "', password: '" + password + "'}";
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(responseContent, StandardCharsets.UTF_8));
    }

    @Get("/test/get/listOfDevices")
    public DefaultFullHttpResponse deviceInformation() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(currentMessage.deviceInformation(userDevices), StandardCharsets.UTF_8));
    }

    @Post("/test/post")
    public DefaultFullHttpResponse save(@RequestBody Message message) {
        if (userRegistration) {
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer("Вы уже зарегистрировались.", StandardCharsets.UTF_8));
        } else {
            userRegistration = true;
            currentMessage = message;
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(message.successfulRegistration(), StandardCharsets.UTF_8));
        }
    }

    @Post("/test/post/entry")
    public DefaultFullHttpResponse entery(@RequestBody Message message) {
        String currentPassword = currentMessage.getPassword();
        String currentLogin = currentMessage.getLogin();
        String verifiablePassword = message.getPassword();
        String verifiableLogin = message.getLogin();
        if (currentLogin.equals(verifiableLogin) && currentPassword.equals(verifiablePassword)) {
            userVerified = true;
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(currentMessage.successfulEntry(), StandardCharsets.UTF_8));
        } else {
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(message.failedEntry(), StandardCharsets.UTF_8));
        }
    }

    @Post("/test/post/addDevice")
    public DefaultFullHttpResponse addDevices(@RequestBody Device newDevice) {
        if (userVerified) {
            userDevices.put(newDevice.getId(), newDevice);
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(currentMessage.addDevice(userDevices), StandardCharsets.UTF_8));
        } else {
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(currentMessage.actionsWithoutLogin(), StandardCharsets.UTF_8));
        }
    }

    @Post("/test/post/deleteDevice")
    public DefaultFullHttpResponse deleteDevices(@RequestBody Device deleteDevice) {
        Device check = userDevices.remove(deleteDevice.getId());
        if (check == null) {
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer("У вас нет такого устройства.", StandardCharsets.UTF_8));
        } else {
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(currentMessage.deleteDevice(userDevices), StandardCharsets.UTF_8));
        }
    }
}
