package controller.deviceapi;

import java.nio.charset.StandardCharsets;
import dto.MicroclimateSensor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import library.json.JsonParser;
import library.AbstractHttpMappingHandler;
import library.annotation.Post;
import library.annotation.RequestBody;
import service.TelemetryService;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class TelemetryHttpHandler extends AbstractHttpMappingHandler{
  TelemetryService telemetryService = new TelemetryService();
  private final JsonParser jsonParser;


  public TelemetryHttpHandler(JsonParser parser) {
    super(parser);
    this.jsonParser = parser;
  }

  @Post("/post/report")
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
