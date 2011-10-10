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
package test.net.sf.sojo;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.sojo.util.Util;
import test.net.sf.sojo.common.GenericComparatorTest;
import test.net.sf.sojo.common.IterateableUtilTest;
import test.net.sf.sojo.common.ObjectGraphWalkerTest;
import test.net.sf.sojo.common.ObjectUtilTest;
import test.net.sf.sojo.common.PathRecordWalkerInterceptorTest;
import test.net.sf.sojo.conversion.ComplexBean2MapConversionTest;
import test.net.sf.sojo.conversion.Iterateable2IterateableConversionTest;
import test.net.sf.sojo.conversion.IterateableMap2BeanConversionTest;
import test.net.sf.sojo.conversion.IterateableMap2MapConversionTest;
import test.net.sf.sojo.conversion.NotSupportedClassConversionTest;
import test.net.sf.sojo.conversion.NullConversionTest;
import test.net.sf.sojo.conversion.Simple2SimpleConversionTest;
import test.net.sf.sojo.conversion.interceptor.SimpleKeyMapperInterceptorTest;
import test.net.sf.sojo.filter.ClassPropertyFilterHandlerTest;
import test.net.sf.sojo.filter.ClassPropertyFilterTest;
import test.net.sf.sojo.interchange.csv.CsvParserTest;
import test.net.sf.sojo.interchange.csv.CsvSerializerTest;
import test.net.sf.sojo.interchange.json.JsonParserTest;
import test.net.sf.sojo.interchange.json.JsonSerializerTest;
import test.net.sf.sojo.interchange.json.JsonWalkerInterceptorTest;
import test.net.sf.sojo.interchange.object.ObjectSerializerTest;
import test.net.sf.sojo.interchange.xmlrpc.XmlRpcSerializerTest;
import test.net.sf.sojo.interchange.xmlrpc.XmlRpcWalkerInterceptorTest;
import test.net.sf.sojo.navigation.PathExecuterTest;
import test.net.sf.sojo.navigation.PathParserTest;
import test.net.sf.sojo.optional.filter.ClassPropertyFilterHanlderForAttributesTest;
import test.net.sf.sojo.reflect.ClassPropertiesCacheTest;
import test.net.sf.sojo.reflect.PropertyTest;
import test.net.sf.sojo.reflect.ReflectionFieldHelperTest;
import test.net.sf.sojo.reflect.ReflectionHelperTest;
import test.net.sf.sojo.reflect.ReflectionMethodHelperTest;
import test.net.sf.sojo.util.ArrayIteratorTest;
import test.net.sf.sojo.util.CycleDetectorTest;
import test.net.sf.sojo.util.TableTest;
import test.net.sf.sojo.util.ThrowableWrapperTest;
import test.net.sf.sojo.util.UtilTest;

public class AllTests {

	public static Test suite () {

		Util.initJdkLogger();
		
		TestSuite lvSuite = new TestSuite();
		
		// core
		lvSuite.addTestSuite(ConverterTest.class);
		lvSuite.addTestSuite(ConverterInterceptorHandlerTest.class);
		lvSuite.addTestSuite(UniqueIdGeneratorTest.class);
		lvSuite.addTestSuite(ConversionHandlerTest.class);
		lvSuite.addTestSuite(NonCriticalExceptionHandlerTest.class);
		
		
		// core - filter
		lvSuite.addTestSuite(ClassPropertyFilterTest.class);
		lvSuite.addTestSuite(ClassPropertyFilterHandlerTest.class);
		
     
		// core - conversion
		lvSuite.addTestSuite(Simple2SimpleConversionTest.class);
		lvSuite.addTestSuite(NullConversionTest.class);
		lvSuite.addTestSuite(Iterateable2IterateableConversionTest.class);
		lvSuite.addTestSuite(IterateableMap2MapConversionTest.class);
		lvSuite.addTestSuite(ComplexBean2MapConversionTest.class);
		lvSuite.addTestSuite(IterateableMap2BeanConversionTest.class);
		lvSuite.addTestSuite(NotSupportedClassConversionTest.class);
		
		// core - conversion - interceptor
		lvSuite.addTestSuite(SimpleKeyMapperInterceptorTest.class);
		

		// core - reflection tests
		lvSuite.addTestSuite(ReflectionHelperTest.class);
		lvSuite.addTestSuite(ReflectionMethodHelperTest.class);
		lvSuite.addTestSuite(ClassPropertiesCacheTest.class);
		lvSuite.addTestSuite(ReflectionFieldHelperTest.class);
		lvSuite.addTestSuite(PropertyTest.class);
		

		// util
		lvSuite.addTestSuite(ArrayIteratorTest.class);
		lvSuite.addTestSuite(UtilTest.class);
		lvSuite.addTestSuite(CycleDetectorTest.class);
		lvSuite.addTestSuite(TableTest.class);
		lvSuite.addTestSuite(ThrowableWrapperTest.class);
		
				
		// navigation
		lvSuite.addTestSuite(PathParserTest.class);
		lvSuite.addTestSuite(PathExecuterTest.class);
		
		// common
		lvSuite.addTestSuite(GenericComparatorTest.class);
		lvSuite.addTestSuite(ObjectUtilTest.class);
		lvSuite.addTestSuite(IterateableUtilTest.class);
		lvSuite.addTestSuite(ObjectGraphWalkerTest.class);
		lvSuite.addTestSuite(PathRecordWalkerInterceptorTest.class);
		
		
		// interchange - object
		lvSuite.addTestSuite(ObjectSerializerTest.class);
		
		// interchange - json
		lvSuite.addTestSuite(JsonWalkerInterceptorTest.class);
		lvSuite.addTestSuite(JsonParserTest.class);
		lvSuite.addTestSuite(JsonSerializerTest.class);
		
		// interchange - xml-rpc
		lvSuite.addTestSuite(XmlRpcWalkerInterceptorTest.class);
		lvSuite.addTestSuite(XmlRpcSerializerTest.class);
		
		// interchange - csv
		lvSuite.addTestSuite(CsvParserTest.class);
		lvSuite.addTestSuite(CsvSerializerTest.class);
		
		
		// ------------------- optional (extensions) tests --------------------
		lvSuite.addTestSuite(ClassPropertyFilterHanlderForAttributesTest.class);
		
		
        return lvSuite;
    }

}
