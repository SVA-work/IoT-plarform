package controller.userapi;

import config.ServerConfig;
import dto.DbConnectionDto;
import dto.objects.TelegramTokenDto;
import dto.objects.UserDto;
import dto.request.UserRegistrationRequestDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import netty.library.AbstractHttpMappingHandler;
import netty.library.annotation.Post;
import netty.library.annotation.RequestBody;
import netty.library.json.JsonParser;
import service.UserService;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class UserHttpHandler extends AbstractHttpMappingHandler {

    private static UserService user;

    public UserHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
        super(parser);
        user = new UserService(dbConnectionDto);
    }

    @Post(ServerConfig.SHORT_LINK_REGISTRATION)
    public DefaultFullHttpResponse registration(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {

        UserDto userDto = new UserDto();
        userDto.setLogin(userRegistrationRequestDto.getLogin());
        userDto.setPassword(userRegistrationRequestDto.getPassword());

        TelegramTokenDto telegramTokenDto = new TelegramTokenDto();
        telegramTokenDto.setTelegramToken(userRegistrationRequestDto.getTelegramToken());

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(user.registration(userDto, telegramTokenDto), StandardCharsets.UTF_8));
    }

    @Post(ServerConfig.SHORT_LINK_ENTRY)
    public DefaultFullHttpResponse entery(@RequestBody UserDto userDto) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(user.userVerification(userDto), StandardCharsets.UTF_8));
    }
}
