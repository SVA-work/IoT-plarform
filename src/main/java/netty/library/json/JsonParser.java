package netty.library.json;

import io.netty.buffer.ByteBuf;

public interface JsonParser {

    Object parse(String path, ByteBuf byteBuf, Class<?> type);
}
