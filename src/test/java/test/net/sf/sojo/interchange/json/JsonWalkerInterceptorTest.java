/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */	
package test.net.sf.sojo.interchange.json;

import java.util.*;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectGraphWalker;
import net.sf.sojo.core.Constants;
import net.sf.sojo.interchange.SerializerException;
import net.sf.sojo.interchange.json.JsonWalkerInterceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;

public class JsonWalkerInterceptorTest extends TestCase {

	ObjectGraphWalker walker = new ObjectGraphWalker();
	JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();

	public JsonWalkerInterceptorTest() {
		walker.addInterceptor(jsonInterceptor);	
	}

	public void testReplaceDoubleQuoteWithDoubleQuoteAndBackslash() throws Exception {
		assertEquals("\"\"", JsonWalkerInterceptor.object2StringWithDoubleQuote(""));
		assertEquals("\"aaa\"", JsonWalkerInterceptor.object2StringWithDoubleQuote("aaa"));
		assertEquals("\"aa\\a\"", JsonWalkerInterceptor.object2StringWithDoubleQuote("aa\\a"));
		assertEquals("\"aa\na\"", JsonWalkerInterceptor.object2StringWithDoubleQuote("aa\na"));
		assertEquals("4711", JsonWalkerInterceptor.object2StringWithDoubleQuote(new Integer(4711)));
	}
	
	public void testHandleControlCharacter() throws Exception {
		assertNull(JsonWalkerInterceptor.handleControlCharacter(null));
		assertEquals("", JsonWalkerInterceptor.handleControlCharacter(""));
		assertEquals(new Integer("4711"), JsonWalkerInterceptor.handleControlCharacter(new Integer("4711")));
		assertEquals("aa \\b cc", JsonWalkerInterceptor.handleControlCharacter("aa \b cc"));
		assertEquals("aa \\f cc", JsonWalkerInterceptor.handleControlCharacter("aa \f cc"));
		assertEquals("aa \\r cc", JsonWalkerInterceptor.handleControlCharacter("aa \r cc"));
		assertEquals("aa \\n cc", JsonWalkerInterceptor.handleControlCharacter("aa \n cc"));
		assertEquals("aa \\t cc", JsonWalkerInterceptor.handleControlCharacter("aa \t cc"));
		assertEquals("aa \\\\ cc", JsonWalkerInterceptor.handleControlCharacter("aa \\ cc"));
		assertEquals("aa \\\\\\\\ cc", JsonWalkerInterceptor.handleControlCharacter("aa \\\\ cc"));
		assertEquals("aa \\/ cc", JsonWalkerInterceptor.handleControlCharacter("aa / cc"));
		assertEquals("aa \\\\\\/ cc", JsonWalkerInterceptor.handleControlCharacter("aa \\/ cc"));
		assertEquals("aa \\\\\\\\\\/ cc", JsonWalkerInterceptor.handleControlCharacter("aa \\\\/ cc"));
	}
	
	public void testHandleControlCharacterBack() throws Exception {
		assertNull(JsonWalkerInterceptor.handleControlCharacterBack(null));
		assertEquals("", JsonWalkerInterceptor.handleControlCharacterBack(""));
		char c = 0;
		assertEquals("", JsonWalkerInterceptor.handleControlCharacterBack(new Character(c).toString()));
		assertEquals("aa \n cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \n cc"));
		assertEquals("aa \r cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \r cc"));
		assertEquals("aa \t cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \t cc"));
		assertEquals("aa \b cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\b cc"));
		assertEquals("aa \f cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\f cc"));
		assertEquals("aa \r cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\r cc"));
		assertEquals("aa \n cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\n cc"));
		assertEquals("aa \t cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\t cc"));
		assertEquals("aa  cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\ cc"));
		assertEquals("aa \\ cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\\\ cc"));
		assertEquals("aa / cc", JsonWalkerInterceptor.handleControlCharacterBack("aa / cc"));
		assertEquals("aa / cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\/ cc"));
		assertEquals("aa \\/ cc", JsonWalkerInterceptor.handleControlCharacterBack("aa \\\\/ cc"));
	}
	
	public void testHandleControlCharacterAndBack() throws Exception {
		String lvOrigString = "aa \b cc";
		Object lvResult = JsonWalkerInterceptor.handleControlCharacter(lvOrigString);
		String lvString = JsonWalkerInterceptor.handleControlCharacterBack(lvResult.toString());
		assertEquals(lvOrigString, lvString);
		lvResult = JsonWalkerInterceptor.handleControlCharacter(lvString);
		lvString = JsonWalkerInterceptor.handleControlCharacterBack(lvResult.toString());
		assertEquals(lvOrigString, lvString);

		lvOrigString = "aa \\/ cc";
		lvResult = JsonWalkerInterceptor.handleControlCharacter(lvOrigString);
		lvString = JsonWalkerInterceptor.handleControlCharacterBack(lvResult.toString());
		assertEquals("aa \\/ cc", lvString);
		lvResult = JsonWalkerInterceptor.handleControlCharacter(lvString);
		lvString = JsonWalkerInterceptor.handleControlCharacterBack(lvResult.toString());
		assertEquals("aa \\/ cc", lvString);
	}
	

	public void testHandleJsonValue() throws Exception {
		assertEquals("", JsonWalkerInterceptor.handleJsonValue(null));
		assertEquals("\"aa \\t a\"", JsonWalkerInterceptor.handleJsonValue("aa \t a"));
		assertEquals("4711", JsonWalkerInterceptor.handleJsonValue(new Integer(4711)));
	}
	
	public void testJsonWalkerInterceptor() throws Exception {
		JsonWalkerInterceptor lvInterceptor = new JsonWalkerInterceptor();
		assertEquals("", lvInterceptor.getJsonString());
		
		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), -1, "", -1);
		assertEquals("", lvInterceptor.getJsonString());

		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), -1, "", Constants.ITERATOR_BEGIN);
		assertEquals("", lvInterceptor.getJsonString());

		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), Constants.ITERATOR_END, "", -1);
		assertEquals("", lvInterceptor.getJsonString());
				
		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), -1, "", Constants.ITERATOR_END);
		assertEquals("", lvInterceptor.getJsonString());
	}

	public void testSimpleJsonString() throws Exception {
		String s = "my test string";
		walker.walk(s);
		assertEquals("\"" + s + "\"", jsonInterceptor.getJsonString());
	}
	
	public void testSimpleJsonLong() throws Exception {
		Long l = new Long(4711);
		walker.walk(l);
		assertEquals(l.toString(), jsonInterceptor.getJsonString());
	}

	public void testSimpleJsonBoolean() throws Exception {
		walker.walk(Boolean.TRUE);
		assertEquals(Boolean.TRUE.toString(), jsonInterceptor.getJsonString());
		
		walker.walk(Boolean.FALSE);
		assertEquals(Boolean.FALSE.toString(), jsonInterceptor.getJsonString());
	}

	public void testSimpleJsonDouble() throws Exception {
		Double d = new Double("47.115");
		walker.walk(d);
		assertEquals(d.toString(), jsonInterceptor.getJsonString());
	}

	public void testEmptyJsonList() throws Exception {
		List<?> list = new ArrayList<Object>();
		walker.walk(list);
		assertEquals("[]", jsonInterceptor.getJsonString());
	}

	public void testSimpleJsonArray() throws Exception {
		boolean[] boolArray = new boolean[]{true,false,true};
		walker.walk(boolArray);
		assertEquals("[true,false,true]", jsonInterceptor.getJsonString());
	}
	
	public void testSimpleJsonList() throws Exception {
		List<String> list = new ArrayList<String>();
		list.add( "first" );
		list.add( "second" );

		walker.walk(list);
		assertEquals("[\"first\",\"second\"]", jsonInterceptor.getJsonString());
	}

	public void testEmptyJsonMap() throws Exception {
		Map<?, ?> map = new HashMap<Object, Object>();

		walker.walk(map);
		assertEquals("{}", jsonInterceptor.getJsonString());
	}

	public void __testSimpleJsonMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put( "name", "json" );
		map.put( "bool", Boolean.TRUE );
		map.put( "int", new Integer(1) );
		map.put( "arr", new String[]{"a","b"} );

		walker.walk(map);
		String s = "{\"arr\":[\"a\",\"b\"],\"int\":1,\"bool\":true,\"name\":\"json\"}";
		assertEquals(s, jsonInterceptor.getJsonString());
	}

	public void testWithNullValuesInMap() throws Exception {
		JsonWalkerInterceptor lvInterceptor = new JsonWalkerInterceptor();
		assertFalse(lvInterceptor.getWithNullValuesInMap());
		
		lvInterceptor.setWithNullValuesInMap(true);
		assertTrue(lvInterceptor.getWithNullValuesInMap());
	}
	
	public void testMapWithNullValue() throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put( "name", "json" );
		map.put( "null", null);

		jsonInterceptor.setWithNullValuesInMap(true); 
		walker.walk(map);
		jsonInterceptor.setWithNullValuesInMap(false); 
		
		String s1 = "{\"null\":null,\"name\":\"json\"}";
        String s2 = "{\"name\":\"json\",\"null\":null}";
		assertThat(jsonInterceptor.getJsonString(), either(equalTo(s1)).or(equalTo(s2)));
	}

	public void testMapWithOutNullValue() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put( "name", "json" );
		map.put( "null", null);

		jsonInterceptor.setWithNullValuesInMap(false);
		walker.walk(map);
		
		String s = "{\"name\":\"json\"}";
		assertEquals(s, jsonInterceptor.getJsonString());
	}

	public void testNullValue() throws Exception {
		jsonInterceptor.setWithNullValuesInMap(true); 
		walker.walk(null);

		assertEquals("null", jsonInterceptor.getJsonString());
	}

	public void testVisitElement() throws Exception {
		JsonWalkerInterceptor lvInterceptor = new JsonWalkerInterceptor();
		lvInterceptor.visitElement("Test-String", Constants.INVALID_INDEX, "value", Constants.TYPE_MAP, "(0)", 1);
		
		assertEquals("\"Test-String\":", lvInterceptor.getJsonString());
	}
	
	public void testVisitElementWithNoStringKey() throws Exception {
		JsonWalkerInterceptor lvInterceptor = new JsonWalkerInterceptor();
		try {
			lvInterceptor.visitElement(new Long(4711), Constants.INVALID_INDEX, "value", Constants.TYPE_MAP, "(0)", 1);
			fail("Key must be from type String and not Long.");
		} catch (SerializerException e) {
			assertNotNull(e);
		}
	}

	/*
    public void testExNullInMap() {
        Map<String, String> map = new LinkedHashMap<>();

        map.put("First Key", "First Value");
        map.put("Second Key", null);
        map.put("Third Key", "Third Value");
        Object serMap = walker.serialize(map);
        assertNotNull(serMap);

        Object deserMap = walker.deserialize(serMap);
        assertEquals(map, deserMap);
    }*/


}
