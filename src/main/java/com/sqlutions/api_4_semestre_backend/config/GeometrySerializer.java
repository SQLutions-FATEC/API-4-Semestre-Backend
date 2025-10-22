package com.sqlutions.api_4_semestre_backend.config;

import java.io.IOException;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(Geometry value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Serialize as WKT (Well-Known Text) format
            gen.writeString(value.toText());
        }
    }
}