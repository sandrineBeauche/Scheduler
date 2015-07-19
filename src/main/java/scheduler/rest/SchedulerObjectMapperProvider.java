package scheduler.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import scheduler.engine.ScriptSnapshot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Provider
public class SchedulerObjectMapperProvider implements ContextResolver<ObjectMapper> {

	
	final ObjectMapper defaultObjectMapper;

    public SchedulerObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return defaultObjectMapper;
    }

    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.enable(SerializationFeature.INDENT_OUTPUT);
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        SimpleModule modScriptsnapshot = new SimpleModule("ScriptSnapshot Module");
        modScriptsnapshot.addSerializer(new ScriptSnapshotSerializer(ScriptSnapshot.class));	
        modScriptsnapshot.addDeserializer(ScriptSnapshot.class, new ScriptSnapshotDeserializer(ScriptSnapshot.class));
        
        result.registerModule(modScriptsnapshot);
        
        return result;
    }
    
    public static ObjectMapper getNewObjectMapper(){
    	return createDefaultMapper();
    }

   

}
