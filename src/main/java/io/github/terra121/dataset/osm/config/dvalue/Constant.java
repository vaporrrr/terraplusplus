package io.github.terra121.dataset.osm.config.dvalue;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import io.github.terra121.dataset.osm.config.JsonParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * Returns a single, constant value.
 *
 * @author DaPorkchop_
 */
@JsonAdapter(Constant.Parser.class)
@RequiredArgsConstructor
final class Constant implements DValue {
    protected final double value;

    @Override
    public double apply(@NonNull Map<String, String> tags) {
        return this.value;
    }

    static class Parser extends JsonParser<DValue> {
        @Override
        public DValue read(JsonReader in) throws IOException {
            return new Constant(in.nextDouble());
        }
    }
}
