package com.myjournal.journalApp.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    @Override
    public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // This is the standard serialization for when no type info is needed.
        gen.writeString(value.toHexString());
    }

    /**
     * This is the correct way to handle serialization when Jackson's global
     * default typing is enabled. It ensures that the custom string representation
     * of the ObjectId is correctly wrapped with the necessary type information.
     */
    @Override
    public void serializeWithType(ObjectId value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        // 1. Create a type ID for a simple scalar (a string in this case)
        WritableTypeId typeId = typeSer.typeId(value, JsonToken.VALUE_STRING);

        // 2. Write the type prefix (e.g., ["org.bson.types.ObjectId", ... )
        typeSer.writeTypePrefix(gen, typeId);

        // 3. Write the actual string value of the ObjectId yourself
        gen.writeString(value.toHexString());

        // 4. Write the type suffix (e.g., ... ] )
        typeSer.writeTypeSuffix(gen, typeId);
    }
}
