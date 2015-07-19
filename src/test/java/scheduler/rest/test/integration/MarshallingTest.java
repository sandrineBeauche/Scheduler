package scheduler.rest.test.integration;

import static scheduler.rest.test.ScriptSnapshotMatcher.equivalentSnapshot;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import scheduler.engine.ScriptScheduler;
import scheduler.engine.ScriptSnapshot;
import scheduler.engine.TaskStatus;
import scheduler.rest.SchedulerObjectMapperProvider;
import scheduler.rest.test.TestUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarshallingTest {

	
	protected ObjectMapper mapper;
	
	@Before
	public void setUp() throws Exception {
		mapper = SchedulerObjectMapperProvider.getNewObjectMapper();
	}
	
	
	protected String removeCharacters(String toBeCleaned){
		String result = toBeCleaned.replace('\r', ' ');
		result = result.replace('\n', ' ');
		return result;
	}
	
	protected String marshalUnmarshal(String filename) throws JsonParseException, JsonMappingException, IOException{
		File inputFile = new File(TestUtils.class.getClassLoader().getResource(filename).getFile());
		ScriptSnapshot snapshot = mapper.readValue(inputFile, ScriptSnapshot.class);
		
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, snapshot);
		String result = writer.toString();
		return removeCharacters(result);
	}
	
	@Test
	public void testMarshallingUnmarshallingComplexResult() throws JsonParseException, JsonMappingException, IOException {
		String result = marshalUnmarshal("complexResult.json");
		String expected = removeCharacters(TestUtils.readScriptFile("complexResult.json"));
		
		Assert.assertThat(result, equalToIgnoringWhiteSpace(expected));
	}

	
	@Test
	public void testMarshallingUnmarshallingNoResult() throws JsonParseException, JsonMappingException, IOException {
		String result = marshalUnmarshal("noResult.json");
		String expected = removeCharacters(TestUtils.readScriptFile("noResult.json"));
		
		Assert.assertThat(result, equalToIgnoringWhiteSpace(expected));
	}
}
