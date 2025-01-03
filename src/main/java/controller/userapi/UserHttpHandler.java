package controller.userapi;

import library.AbstractHttpMappingHandler;
import library.annotation.Post;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.UserService;
import java.nio.charset.StandardCharsets;
import dto.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class UserHttpHandler extends AbstractHttpMappingHandler {

  private static UserService user = new UserService();

  public UserHttpHandler(JsonParser parser) {
    super(parser);
  }
    
  @Post("/post/registration")
  public DefaultFullHttpResponse registration(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.registration(message), StandardCharsets.UTF_8));
  }

  @Post("/post/entry")
  public DefaultFullHttpResponse entery(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.userVerification(message), StandardCharsets.UTF_8));
  }
}
