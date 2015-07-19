package scheduler.rest;

import java.io.IOException;
import java.util.HashMap;

import scheduler.engine.ScriptSnapshot;
import scheduler.engine.TaskStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ScriptSnapshotDeserializer extends StdDeserializer<ScriptSnapshot> {

	public ScriptSnapshotDeserializer(Class<ScriptSnapshot> t) {
        super(t);
    }

	@Override
	public ScriptSnapshot deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ScriptSnapshot snapshot = new ScriptSnapshot();
		String fieldName = p.nextFieldName();
		String statusString = p.nextTextValue();
		snapshot.setStatus(TaskStatus.valueOf(statusString));
		
		fieldName = p.nextFieldName();
		if(fieldName != null){
			String className = p.nextTextValue();
			fieldName = p.nextFieldName();
			JsonToken token = p.nextToken();
			
			try {
				Object result = p.readValueAs(Class.forName(className));
				snapshot.setResult(result);
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(null, null, e);
			}
		}
		return snapshot;
	}
	
	
}
