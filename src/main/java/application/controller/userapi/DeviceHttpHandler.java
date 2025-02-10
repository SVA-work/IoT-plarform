package application.controller.userapi;

import application.config.ServerConfig;
import application.dto.DbConnectionDto;
import application.dto.objects.DeviceDto;
import application.dto.objects.UserDto;
import application.dto.request.DeviceRequestDto;
import application.netty.library.AbstractHttpMappingHandler;
import application.netty.library.annotation.Get;
import application.netty.library.annotation.Post;
import application.netty.library.annotation.QueryParam;
import application.netty.library.annotation.RequestBody;
import application.netty.library.json.JsonParser;
import application.service.DeviceService;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RestController
public class DeviceHttpHandler extends AbstractHttpMappingHandler {

    private static DeviceService device;

    public DeviceHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
        super(parser);
        device = new DeviceService(dbConnectionDto);
    }

    @Get(ServerConfig.SHORT_LINK_GET_DEVICE_INFORMATION)
    public DefaultFullHttpResponse deviceInformation(@QueryParam("login") String login) {

        UserDto userDto = new UserDto();
        userDto.setLogin(login);

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(device.listOfDevicesOfUser(userDto), StandardCharsets.UTF_8));
    }

    @Post(ServerConfig.SHORT_LINK_ADD_DEVICE)
    public DefaultFullHttpResponse addDevices(@RequestBody DeviceRequestDto deviceRequestDto) {

        UserDto userDto = new UserDto();
        userDto.setLogin(deviceRequestDto.getLogin());

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setToken(deviceRequestDto.getToken());
        deviceDto.setType(deviceRequestDto.getType());

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(device.addDevice(userDto, deviceDto), StandardCharsets.UTF_8));
    }

    @Post(ServerConfig.SHORT_LINK_DELETE_DEVICE)
    public DefaultFullHttpResponse deleteDevices(@RequestBody DeviceRequestDto deviceRequestDto) {

        UserDto userDto = new UserDto();
        userDto.setLogin(deviceRequestDto.getLogin());

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setToken(deviceRequestDto.getToken());

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(device.deleteDevice(userDto, deviceDto), StandardCharsets.UTF_8));
    }
}
