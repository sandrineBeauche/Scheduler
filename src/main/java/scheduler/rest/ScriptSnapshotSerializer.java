package scheduler.rest;

import java.io.IOException;

import scheduler.engine.ScriptSnapshot;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ScriptSnapshotSerializer extends StdSerializer<ScriptSnapshot>{

	public ScriptSnapshotSerializer(Class<ScriptSnapshot> t) {
        super(t);
    }

	@Override
	public void serialize(ScriptSnapshot value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		jgen.writeStartObject();      
        jgen.writeStringField("status", value.getStatus().name());
        Object result = value.getResult();
        if(result != null){
        	Class clazz = value.getResult().getClass();
        	jgen.writeStringField("resultClass", clazz.getName());
        	jgen.writeObjectField("result", value.getResult());
        }
        jgen.writeEndObject();
	}

	
}
