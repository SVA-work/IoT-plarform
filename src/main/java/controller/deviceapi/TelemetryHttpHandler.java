package controller.deviceapi;

import java.nio.charset.StandardCharsets;

import config.ServerConfig;
import dto.DbConnectionDto;
import dto.devices.MicroclimateSensor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import netty.library.json.JsonParser;
import netty.library.AbstractHttpMappingHandler;
import netty.library.annotation.Post;
import netty.library.annotation.RequestBody;
import service.TelemetryService;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class TelemetryHttpHandler extends AbstractHttpMappingHandler{
  TelemetryService telemetryService;
  private final JsonParser jsonParser;


  public TelemetryHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
    super(parser);
    this.jsonParser = parser;
    telemetryService = new TelemetryService(dbConnectionDto);
  }

  @Post(ServerConfig.SHORT_LINK_REPORT)
  public DefaultFullHttpResponse report(@RequestBody MicroclimateSensor message) {
    String uuid = message.getUuid();
    String base64Message = message.getBase64Message();
    String decodedMessage = telemetryService.decodeBase64(base64Message);
    ByteBuf byteBuf = Unpooled.copiedBuffer(decodedMessage, StandardCharsets.UTF_8);
    Object parsedObject = jsonParser.parse(decodedMessage, byteBuf, MicroclimateSensor.class);
    MicroclimateSensor deviceDto = (MicroclimateSensor) parsedObject;
    telemetryService.reportProcessing(uuid, deviceDto);
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer("", StandardCharsets.UTF_8));
  }
}
