package test.controller;

import test.DTO.Message;
import test.entity.*;
import test.library.AbstractHttpMappingHandler;
import test.library.annotation.*;
import test.library.json.JsonParser;
import test.service.UserInfoService;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HelloHttpHandler extends AbstractHttpMappingHandler {
  private static Message currentMessage;
  private static User currentUser = new User();
  private static UserInfoService user = new UserInfoService();

  public HelloHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get("/test/get/userData")
  public DefaultFullHttpResponse userData() {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.userData(currentMessage), StandardCharsets.UTF_8));
  }

  @Get("/test/get/listOfDevices")
  public DefaultFullHttpResponse deviceInformation() {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.ListOfDevices(), StandardCharsets.UTF_8));
  }

  @Post("/test/post")
  public DefaultFullHttpResponse save(@RequestBody Message message) {
    if (currentMessage != null) {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer("Вы уже зарегистрировались.", StandardCharsets.UTF_8));
    } else {
      currentMessage = message;
      System.out.println(user.successfulRegistration(currentMessage));
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer(user.successfulRegistration(currentMessage), StandardCharsets.UTF_8));
    }
  }

  @Post("/test/post/entry")
  public DefaultFullHttpResponse entery(@RequestBody Message message) {
    if (user.userVerification(message, currentMessage)) {
      currentUser.setVerified(true);
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer(user.successfulEntry(), StandardCharsets.UTF_8));
    } else {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer(user.failedEntry(), StandardCharsets.UTF_8));
    }
  }

  @Post("/test/post/addDevice")
  public DefaultFullHttpResponse addDevices(@RequestBody Message message) {
    if (user.verified(currentUser)) {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer(user.addDevice(message.getDeviceId()), StandardCharsets.UTF_8));
    } else {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
          Unpooled.copiedBuffer(user.actionsWithoutLogin(), StandardCharsets.UTF_8));
    }
  }

  @Post("/test/post/deleteDevice")
  public DefaultFullHttpResponse deleteDevices(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.deleteDevice(message.getDeviceId()), StandardCharsets.UTF_8));
  }
}
