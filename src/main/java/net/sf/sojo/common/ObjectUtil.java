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
package net.sf.sojo.common;

import java.net.URL;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.sf.sojo.core.Conversion;
import net.sf.sojo.core.ConversionHandler;
import net.sf.sojo.core.ConversionIterator;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptorHandler;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.Iterateable2IterateableConversion;
import net.sf.sojo.core.conversion.IterateableMap2BeanConversion;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.conversion.SimpleFormatConversion;
import net.sf.sojo.core.conversion.interceptor.SimpleKeyMapperInterceptor;
import net.sf.sojo.core.conversion.interceptor.ThrowableConverterInterceptor;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandler;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;

/**
 * The central class of common object methods, how compare, clone and so on.
 *
 * @author linke
 *
 */
public final class ObjectUtil {

	private Converter converter = new Converter();
	private SimpleKeyMapperInterceptor simpleKeyMapperInterceptor = new SimpleKeyMapperInterceptor();

	private IterateableMap2MapConversion map2MapConversion = new IterateableMap2MapConversion();
	private IterateableMap2BeanConversion map2BeanConversion = new IterateableMap2BeanConversion();
	private ComplexBean2MapConversion bean2MapConversion = new ComplexBean2MapConversion();
	private Iterateable2IterateableConversion iterateable2IterateableConversion = new Iterateable2IterateableConversion();
	private SimpleFormatConversion simpleFormatConversion = new SimpleFormatConversion();
	private Simple2SimpleConversion simpleURL2StringConversion = new Simple2SimpleConversion(URL.class, String.class);

	private ThrowableConverterInterceptor throwableConverterInterceptor = new ThrowableConverterInterceptor();


	private boolean withSimpleKeyMapper = true;

	public ObjectUtil() {
		this(true);
	}

	public ObjectUtil(boolean pvWithSimpleKeyMapper) {
		setWithSimpleKeyMapper(pvWithSimpleKeyMapper);
		if (getWithSimpleKeyMapper()) {
			map2MapConversion.getConverterInterceptorHandler().addConverterInterceptor(simpleKeyMapperInterceptor);
		}
		converter.addConversion(map2MapConversion);
		converter.addConversion(map2BeanConversion);
		converter.addConversion(bean2MapConversion);
		converter.addConversion(iterateable2IterateableConversion);
		converter.addConversion(simpleURL2StringConversion);

		// add interceptor for wrap Throwable before and after converting
		converter.addConverterInterceptor(throwableConverterInterceptor);
	}

	public void addFormatterForType (Format pvFormat, Class<?> pvType) {
		simpleFormatConversion.addFormatter(pvType, pvFormat);
		if (getConverter().getConversionHandler().containsConversion(simpleFormatConversion) == false) {
			getConverter().addConversion(simpleFormatConversion);
		}
	}

	public void removeFormatterByType(Class<?> pvType) {
		simpleFormatConversion.removeFormatterByType(pvType);
		if (simpleFormatConversion.getFormatterSize() == 0) {
			getConverter().removeConversion(simpleFormatConversion);
		}
	}

	public void setIgnoreAllNullValues(boolean pvIgnoreNullValues) {
		map2MapConversion.setIgnoreNullValues(pvIgnoreNullValues);
		map2BeanConversion.setIgnoreNullValues(pvIgnoreNullValues);
		bean2MapConversion.setIgnoreNullValues(pvIgnoreNullValues);
		iterateable2IterateableConversion.setIgnoreNullValues(pvIgnoreNullValues);
	}

	public boolean getIgnoreAllNullValues() {
		return (map2MapConversion.getIgnoreNullValues() && map2BeanConversion.getIgnoreNullValues() && bean2MapConversion.getIgnoreNullValues() && iterateable2IterateableConversion.getIgnoreNullValues());
	}

	public void setClassPropertyFilterHandler(ClassPropertyFilterHandler pvClassPropertyFilterHandler) { converter.setClassPropertyFilterHandler(pvClassPropertyFilterHandler); }
	public ClassPropertyFilterHandler getClassPropertyFilterHandler() { return converter.getClassPropertyFilterHandler(); }


	public Converter getConverter() {
		return converter;
	}

	public void setWithSimpleKeyMapper(boolean pvWithSimpleKeyMapper) {
		withSimpleKeyMapper = pvWithSimpleKeyMapper;
		ConverterInterceptorHandler lvHandler = map2MapConversion.getConverterInterceptorHandler();
		if (getWithSimpleKeyMapper() == false) {
			lvHandler.removeConverterInterceptor(simpleKeyMapperInterceptor);
		} else {
			lvHandler.addConverterInterceptor(simpleKeyMapperInterceptor);
		}
	}
	public boolean getWithSimpleKeyMapper() { return withSimpleKeyMapper; }


	public boolean getWithCycleDetection() {
		boolean lvResult = false;
		ConversionHandler ch = converter.getConversionHandler();
		for (int i = 0; i < ch.size(); i++) {
			Conversion c = ch.getConversionByPosition(i);
			if (c instanceof ConversionIterator) {
				lvResult = ((ConversionIterator) c).getWithCycleDetection();
				if (lvResult == true) { break; }
			}
		}
		return lvResult;
	}

	public void setWithCycleDetection(boolean pvWithCycleDetection) {
		ConversionHandler ch = converter.getConversionHandler();
		for (int i = 0; i < ch.size(); i++) {
			Conversion c = ch.getConversionByPosition(i);
			if (c instanceof ConversionIterator) {
				((ConversionIterator) c).setWithCycleDetection(pvWithCycleDetection);
			}
		}
	}

	public Object makeSimple(final Object pvRootObject, String[] pvExcludedProperties) {

		// save exist filter
		ClassPropertyFilterHandler lvClassPropertyFilterHandler = this.getClassPropertyFilterHandler();

		if (pvRootObject != null && pvExcludedProperties != null && pvExcludedProperties.length > 0) {
			// set new temp filter
			ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter (pvRootObject.getClass());
			lvClassPropertyFilter.setSupport4AddClassProperty(true);
			lvClassPropertyFilter.addProperties(pvExcludedProperties);
			ClassPropertyFilterHandlerImpl lvTempFilterHandler = new ClassPropertyFilterHandlerImpl(lvClassPropertyFilter);
			this.setClassPropertyFilterHandler(lvTempFilterHandler);
		}

		Object lvReturn = null;
		try {
			lvReturn = makeSimple(pvRootObject);
		} finally {
			// set original filter back
			this.setClassPropertyFilterHandler(lvClassPropertyFilterHandler);
		}

		return lvReturn;
	}


	public Object makeSimple(final Object pvRootObject) {
		simpleKeyMapperInterceptor.setMakeSimple(true);
		converter.removeConversion(map2BeanConversion);
		Object lvSimple = converter.convert(pvRootObject);
		return lvSimple;
	}

	public Object makeComplex(final Object pvRootObject) {
		return makeComplex(pvRootObject, null);
	}

	public Object makeComplex(final Object pvRootObject, final Class<?> pvRootClass) {
		simpleKeyMapperInterceptor.setMakeSimple(false);
		converter.addConversion(map2BeanConversion);
		Object lvComplex = converter.convert(pvRootObject, pvRootClass);
		return lvComplex;
	}


	/**
	 * Copy of all values of the root object.
	 *
	 * @param pvRootObject Source object, that will be copy.
	 * @return The copy of the source object.
	 */
	public Object copy(final Object pvRootObject) {
		Object lvSimple = makeSimple(pvRootObject);
		Object lvComplex = makeComplex(lvSimple);
		return lvComplex;
	}

	/**
	 * Get the <code>hashCode</code> of all values.
	 * Start: <code>hashCode = 17</code>, <code>hashCode = hashCode * 37 + value.hashCode()</code>
	 * @param pvObject Source object
	 * @return The calculated hashCode
	 */
	public int hashCode(final Object pvObject) {
		long lvHashCode = 17;
		ObjectGraphWalker ogw = new ObjectGraphWalker(this.getClassPropertyFilterHandler());
		ogw.setIgnoreNullValues(true);
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setOnlySimpleProperties(true);
		lvInterceptor.setFilterUniqueIdProperty(true);
		ogw.addInterceptor(lvInterceptor);
		ogw.walk(pvObject);
		for (Object entry : lvInterceptor.getAllRecordedPaths().values()) {
      lvHashCode = lvHashCode * 37 + entry.hashCode();
    }
		return new Long(lvHashCode).intValue();
	}

	/**
	 * Test of equals.
	 *
	 * @param pvObject1 To comparing first value.
	 * @param pvObject2 To comparing second value.
	 * @return <code>true</code>, if Object1 and Object2 are equals, else <code>false</code>.
	 */
	public boolean equals(final Object pvObject1, final Object pvObject2) {
		boolean lvEquals = false;
		if (pvObject1 != null && pvObject2 != null) {
			CompareResult lvResult = compare(pvObject1, pvObject2);
			lvEquals = (lvResult == null);
		}
		return lvEquals;
	}

	/**
	 * Compare and is stopped by find the first different value.
	 *
	 * @param pvObject1 To comparing first value.
	 * @param pvObject2 To comparing second value.
	 * @return The different betwenn Object1 and Object2, or <code>null</code> if equals.
	 */
	public CompareResult compare(final Object pvObject1, final Object pvObject2) {
		CompareResult[] lvCompareResults = null;
		lvCompareResults = compareIntern(pvObject1, pvObject2, true);
		CompareResult lvResult = (lvCompareResults == null ? null : lvCompareResults[0]);
		return lvResult;
	}

	/**
	 * Compare and get <b>all</b> finded differents.
	 *
	 * @param pvObject1 To comparing first value.
	 * @param pvObject2 To comparing second value.
	 * @return The differents betwenn Object1 and Object2, or <code>null</code> if equals.
	 */
	public CompareResult[] compareAll(final Object pvObject1, final Object pvObject2) {
		CompareResult[] lvCompareResults = compareIntern(pvObject1, pvObject2, false);
		return lvCompareResults;
	}

	/**
	 * If parameter are <code>java.lang.Comparable</code> than delegate to the <code>compareTo</code> method
	 * of the first parameter object.
	 * <br>
	 * By JavaBean are all properties comparing and add the several compareTo results.
	 * They are one property <code>+1</code> and the second property <code>-1</code>,
	 * then is the complete value <code>0</code>.
	 *
	 * @param pvObject1 To comparing first value (must be unequals <code>null</code>).
	 * @param pvObject2 To comparing second value (must be unequals <code>null</code>).
	 * @return a negative integer, zero, or a positive integer as this object is less than,
	 * 			equal to, or greater than the specified object.
	 */
	public int compareTo(final Object pvObject1, final Object pvObject2) {
		int lvCompareToValue = 0;
		if (pvObject1 == null) {
			throw new NullPointerException("First arg by compareTo is Null");
		}
		if (pvObject2 == null) {
			throw new NullPointerException("Second arg by compareTo is Null");
		}

		if (pvObject1 != pvObject2) {
			CompareResult[] lvCompareResults = compareIntern(pvObject1, pvObject2, false);
			if (lvCompareResults != null) {
				for (int i = 0; i < lvCompareResults.length; i++) {
					int zw = lvCompareResults[i].getCompareToValue();
					lvCompareToValue = lvCompareToValue + zw;
				}
			}
		}
		return lvCompareToValue;
	}

	private CompareResult[] compareIntern(final Object pvObject1, final Object pvObject2, boolean pvBreakByFindDifferences) {
		CompareResult[] lvResult = null;

		if (pvObject1 != null && pvObject2 != null) {
			ObjectGraphWalker lvWalker1 = new ObjectGraphWalker(this.getClassPropertyFilterHandler());
			PathRecordWalkerInterceptor lvInterceptor1 = new PathRecordWalkerInterceptor();
			lvInterceptor1.setOnlySimpleProperties(true);
			lvInterceptor1.setFilterUniqueIdProperty(true);
			lvWalker1.addInterceptor(lvInterceptor1);

			lvWalker1.walk(pvObject1);
			Map<String, Object> lvPathes1 = lvInterceptor1.getAllRecordedPaths();

			ObjectGraphWalker lvWalker2 = new ObjectGraphWalker(this.getClassPropertyFilterHandler());
			PathRecordWalkerInterceptor lvInterceptor2 = new PathRecordWalkerInterceptor();
			lvInterceptor2.setOnlySimpleProperties(true);
			lvInterceptor2.setFilterUniqueIdProperty(true);
			lvWalker2.addInterceptor(lvInterceptor2);

			lvWalker2.walk(pvObject2);
			Map<String, Object> lvPathes2 = lvInterceptor2.getAllRecordedPaths();

			Set<String> paths = getAllPathsForMaps(lvPathes1.keySet(), lvPathes2.keySet());

			List<CompareResult> lvCompareResultsList = new ArrayList<>();
			compareTwoMaps(paths, lvPathes1, lvPathes2, pvBreakByFindDifferences, lvCompareResultsList);

			if (lvCompareResultsList.size() > 0) {
				lvResult = lvCompareResultsList.toArray(new CompareResult[lvCompareResultsList.size()]);
			}
		}

		return lvResult;
	}


	private static void compareTwoMaps(Set<String> mapsPaths, Map<String, Object> pvMap1, Map<String, Object> pvMap2, boolean pvBreakByFindDifferents,
		List<CompareResult> pvCompareResultsList) {

		for (String path : mapsPaths) {

			Object lvValue1 = pvMap1.get(path);
			Object lvValue2 = pvMap2.get(path);

			if (!Objects.equals(lvValue1, lvValue2)) {

				CompareResult lvCompareResult = new CompareResult();
				lvCompareResult.differentPath = path;
				lvCompareResult.differentValue1 = lvValue1;
				lvCompareResult.differentValue2 = lvValue2;
				lvCompareResult.numberOfRecursion = recursionFromPath(path);
				pvCompareResultsList.add(lvCompareResult);
				if (pvBreakByFindDifferents) {
					break;
				}

			}
		}
	}

	private static int recursionFromPath(String path) {
		int recursion = -1;
		if (path != null) {

			recursion = 1;
			int p = 0;
			for (p = path.indexOf('.', p); p >= 0; p = path.indexOf('.', p + 1)) {
				recursion++;
			}
		}
		return recursion;
	}

	private static Set<String> getAllPathsForMaps(Collection<String> lvPaths1, Collection<String> lvPaths2) {
        Set<String> distinctPaths = new HashSet<>();

        distinctPaths.addAll(lvPaths1);
        distinctPaths.addAll(lvPaths2);

        return distinctPaths;
    }
}
