package controller.userapi;

import java.nio.charset.StandardCharsets;
import dto.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import library.AbstractHttpMappingHandler;
import library.annotation.Get;
import library.annotation.Post;
import library.annotation.QueryParam;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.RuleService;


import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class RulesHttpHandler extends AbstractHttpMappingHandler {
  private static RuleService rule = new RuleService();

  public RulesHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get("/get/allAvailableRules")
  public DefaultFullHttpResponse AvailableDeviceRules() {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.getAllAvailableRules(), StandardCharsets.UTF_8));
  }

  @Get("/get/deviceRules")
  public DefaultFullHttpResponse deviceRules(@QueryParam("login") String login, @QueryParam("token") String token) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.getDeviceRules(login, token), StandardCharsets.UTF_8));
  }

  @Post("/post/applyRule")
  public DefaultFullHttpResponse applyRule(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.applyRule(message), StandardCharsets.UTF_8));
  }

  @Post("/post/deleteDeviceRule")
  public DefaultFullHttpResponse deleteDeviceRule(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.deleteDeviceRule(message), StandardCharsets.UTF_8));
  }
}
