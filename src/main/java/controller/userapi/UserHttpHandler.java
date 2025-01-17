package controller.userapi;

import dto.DbConnectionDto;
import library.AbstractHttpMappingHandler;
import library.annotation.Post;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.RuleService;
import service.UserService;
import java.nio.charset.StandardCharsets;

import config.ServerConfig;
import dto.entity.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class UserHttpHandler extends AbstractHttpMappingHandler {

  private static UserService user;

  public UserHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
    super(parser);
    user = new UserService(dbConnectionDto);
  }
    
  @Post(ServerConfig.SHORT_LINK_REGISTRATION)
  public DefaultFullHttpResponse registration(@RequestBody UserDto userDto) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.registration(userDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.SHORT_LINK_ENTRY)
  public DefaultFullHttpResponse entery(@RequestBody UserDto userDto) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.userVerification(userDto), StandardCharsets.UTF_8));
  }
}
