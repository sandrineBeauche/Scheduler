package scheduler.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import scheduler.engine.ScriptSnapshot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A custom Object mapper provider that registers custom serializer and deserializer in order to support object mapping to JSON in the snapshots. 
 * @author Sandrine Ben Mabrouk.
 *
 */
@Provider
public class SchedulerObjectMapperProvider implements ContextResolver<ObjectMapper> {

	/**
	 * The used Jackson 2 object mapper.
	 */
	final ObjectMapper defaultObjectMapper;

	/**
	 * Creates a custom object mapper provider.
	 */
    public SchedulerObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    /**
     * Get the used object mapper.
     * @return the used object mapper.
     */
    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return defaultObjectMapper;
    }

    /**
     * Creates and configures the used object mapper with custom serializer and deserializer.
     * @return
     */
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
    
    /**
     * Creates an object mapper.
     * @return the new object mapper.
     */
    public static ObjectMapper getNewObjectMapper(){
    	return createDefaultMapper();
    }

   

}
