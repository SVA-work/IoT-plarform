package netty.library.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import netty.library.exception.JsonParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonParserDefault implements JsonParser {

    private final ObjectMapper mapper;

    public JsonParserDefault() {
        mapper = new ObjectMapper();
    }

    @Override
    public Object parse(String path, ByteBuf byteBuf, Class<?> type) {
        try {
            return mapper.readValue(byteBuf.toString(StandardCharsets.UTF_8), type);
        } catch (IOException e) {
            throw new JsonParseException(String.format("Json parse exception, path - '%s'", path), e);
        }
    }
}
