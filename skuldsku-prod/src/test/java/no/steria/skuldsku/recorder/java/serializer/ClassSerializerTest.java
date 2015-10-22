package no.steria.skuldsku.recorder.java.serializer;

import static no.steria.skuldsku.recorder.java.recorder.JavaCallPersisterRunner.store;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.AsyncMode;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;
import no.steria.skuldsku.recorder.java.recorder.JavaCallRecorderConfig;
import no.steria.skuldsku.recorder.recorders.StreamRecorderCommunicator;

import org.junit.Ignore;
import org.junit.Test;


public class ClassSerializerTest {

    private final ClassSerializer serializer = new ClassSerializer();

    @Test
    public void shouldReturnClassNameForEmptyClass() throws Exception {
        assertThat(serializer.asString(new EmptyClass())).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.EmptyClass>");
    }

    @Test
    public void shouldHandleNullObjects() throws Exception {
        assertThat(serializer.asString(null)).isEqualTo("<null>");
    }

    @Test
    public void shouldDeserialiseNull() throws Exception {
        final String serializedValue = serializer.asString(null);
        assertThat(serializer.asObject(serializedValue)).isNull();
    }

    @Test
    public void shouldReturnClass() throws Exception {
        final String serializedEmptyClass = serializer.asString(new EmptyClass());
        assertThat(serializedEmptyClass).isEqualTo("<" + EmptyClass.class.getName() + ">");
        final EmptyClass emptyClass = (EmptyClass) serializer.asObject(serializedEmptyClass);
        assertThat(emptyClass.getClass()).isEqualTo(EmptyClass.class);
    }

    @Test
    public void shouldHandleArraysOfPrimitives() throws Exception {
        long arrval[] = {0l,1l,2l};
        char bytval[] = {'a', 'b', 'c'};
        ClassWithArrayOfPrimitives classWithCollection = new ClassWithArrayOfPrimitives();
        classWithCollection.setArrayOfLongs(arrval);
        classWithCollection.setArrayOfChars(bytval);

        String serialized = serializer.asString(classWithCollection);
        assertThat(serialized).isEqualTo("<" + ClassWithArrayOfPrimitives.class.getName()
                + ";arrayOfLongs=<array;[J;<java.lang.Long;0>;<java.lang.Long;1>;<java.lang.Long;2>>"
                + ";arrayOfChars=<array;[C;<java.lang.Character;a>;<java.lang.Character;b>;<java.lang.Character;c>>"
                + ">");
        ClassWithArrayOfPrimitives cloned = (ClassWithArrayOfPrimitives) serializer.asObject(serialized);

        assertThat(cloned.getArrayOfLongs()).containsOnly(0l, 1l, 2l);
        assertThat(cloned.getArrayOfChars()).containsOnly('a', 'b', 'c');
    }
    
    @Test
    public void shouldHandleArraysWithNull() throws Exception {
        final Object[] a = {"a", null, "b"};
        final String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<array;[Ljava.lang.Object&semi;<java.lang.String;a>;&null;<java.lang.String;b>>");
        final Object[] cloned = (Object[]) serializer.asObject(serialized);
        assertThat(cloned).isEqualTo(a);
    }
    
    @Test
    public void shouldHandleMixedArray() throws Exception {
        // Need to specify array class in serialized format.
        final Object[] a = {"a", null, 5};
        final String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<array;[Ljava.lang.Object&semi;<java.lang.String;a>;&null;<java.lang.Integer;5>>");
        Object[] cloned = (Object[]) serializer.asObject(serialized);
        assertThat(cloned[0]).isEqualTo("a");
        assertThat(cloned[1]).isNull();
        assertThat(cloned[2]).isEqualTo(5);
    }
    
    @Test
    public void shouldHandleArraysWithNullAndCorrectType() throws Exception {
        final String[] a = {"a", null, "b"};
        final String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<array;[Ljava.lang.String&semi;<java.lang.String;a>;&null;<java.lang.String;b>>");
        final String[] cloned = (String[]) serializer.asObject(serialized);
        assertThat(cloned).isEqualTo(a);
    }
    
    @Test
    public void shouldHandleArraysBeginningWithNull() throws Exception {
        final String[] a = {null, "a", "b"};
        final String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<array;[Ljava.lang.String&semi;&null;<java.lang.String;a>;<java.lang.String;b>>");
        final Object[] cloned = (Object[]) serializer.asObject(serialized);
        assertThat(cloned).isEqualTo(a);
    }
    
    @Test
    public void shouldHandleArraysBeginningWithNullAndCorrectType() throws Exception {
        final String[] a = {null, "a", "b"};
        final String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<array;[Ljava.lang.String&semi;&null;<java.lang.String;a>;<java.lang.String;b>>");
        final String[] cloned = (String[]) serializer.asObject(serialized);
        assertThat(cloned).isEqualTo(a);
    }
    
    @Test
    public void shouldHandleArrayWithOnlyNull() { 
        final String serializedValue = serializer.asString(new Object[] {null});
        assertThat(serializedValue).isEqualTo("<array;[Ljava.lang.Object&semi;&null>");
        Object[] a = (Object[]) serializer.asObject(serializedValue);
        assertThat(a.length).isEqualTo(1);
        assertThat(a[0]).isEqualTo(null);
    }

    @Test
    public void shouldHandleEmptyArray() {
        final String value = serializer.asString(new Object[0]);
        assertThat(value).isEqualTo("<array;[Ljava.lang.Object&semi>");
    }
    
    @Test
    public void shouldSerializeAndDeserializeArray() throws Exception {
        final String actual = serializer.asString(new String[]{"asdf", "asdf "});
        assertThat(serializer.asObject(actual)).isEqualTo(new String[]{"asdf", "asdf "});
    }
    
    @Test
    public void shouldBoxPrimitivesWhenSerializing() throws Exception {
        final long[] primitives = new long[] { 0l,1l };
        final String serialized = serializer.asString(primitives);
        assertThat(serialized).isEqualTo("<array;[J;<java.lang.Long;0>;<java.lang.Long;1>>");
    }

    @Test
    public void shouldSerializeAndDeserializeArrayOfPrimitives() throws Exception {
        final long[] primitives = new long[] { 0l,1l };
        final String serialized = serializer.asString(primitives);

        final Object deserialized = serializer.asObject(serialized);
        assertThat(deserialized).isEqualTo(new long[]{0l,1l});
    }

    @Test
    public void shouldSerializeAndDeserializeList() throws Exception {
        final String actual = serializer.asString(Arrays.asList(new String("a"), new String("b")));
        assertThat(actual).isEqualTo("<list;<java.lang.String;a>;<java.lang.String;b>>");
        assertThat(serializer.asObject(actual)).isEqualTo(Arrays.asList("a", "b"));
    }
    
    @Test
    public void shouldSerializeAndDeserializeListWithDuplicates() throws Exception {
        ClassSerializer.removeNonDuplicationClass(String.class);
        final String testString = "asdf";
        final String actual = serializer.asString(Arrays.asList(testString, testString));

        assertThat(actual).isEqualTo("<list;<java.lang.String;asdf>;<duplicate;1>>");
        
        assertThat(serializer.asObject(actual)).isEqualTo(Arrays.asList(testString, testString));
        ClassSerializer.addNonDuplicationClass(String.class);
    }

    @Test
    public void shouldParseSimpleStringField() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval("pedro");
        assertThat(serializer.asString(simpleFields)).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=pedro;intval=0;anotherVar=false>");
    }

    @Test
    public void sholdDeserializeObjectWithFields() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval("pedro").setIntval(42);
        ClassWithSimpleFields cloned = (ClassWithSimpleFields) serializer.asObject(serializer.asString(simpleFields));

        assertThat(cloned.getIntval()).isEqualTo(42);
        assertThat(cloned.getStringval()).isEqualTo("pedro");
    }

    @Test
    public void shouldHandleNullInFields() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval(null).setIntval(42);
        ClassWithSimpleFields cloned = (ClassWithSimpleFields) serializer.asObject(serializer.asString(simpleFields));

        assertThat(cloned.getStringval()).isNull();

    }

    @Test
    public void shouldHandleControlCharacters() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval("a&;<>%bc");
        String serialized = serializer.asString(simpleFields);
        ClassWithSimpleFields cloned = (ClassWithSimpleFields) serializer.asObject(serialized);

        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=a&amp&semi&lt&gt&percentbc;intval=0;anotherVar=false>");
        assertThat(cloned.getStringval()).isEqualTo("a&;<>%bc");
    }

    @Test
    public void shouldHandleArraysAndCollections() throws Exception {
        String arrval[] = {"a","b","c"};
        ClassWithCollection classWithCollection = new ClassWithCollection().setArrVal(arrval).setNumbers(Arrays.asList(1, 2, 4));

        String serialized = serializer.asString(classWithCollection);
        System.out.println(serialized);

        ClassWithCollection cloned = (ClassWithCollection) serializer.asObject(serialized);

        assertThat(cloned.getArrVal()).containsOnly("a", "b", "c");
        assertThat(cloned.getNumbers()).containsOnly(1, 2, 4);
    }

    @Test
    public void shouldDeSerializeListOfObjectsWithFields() {
        List<ClassWithSimpleFields> list = Arrays.asList(new ClassWithSimpleFields());
        String serialized = serializer.asString(list);
        assertThat(serialized).isEqualTo("<list;<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=<null>;intval=0;anotherVar=false>>");
        System.out.println(serialized);
        serializer.asObject(serialized);
    }
    
    @Test
    public void shouldDeSerializeListOfMultipleObjectsWithFields() {
        List<ClassWithSimpleFields> list = Arrays.asList(new ClassWithSimpleFields(), new ClassWithSimpleFields());
        String serialized = serializer.asString(list);
        assertThat(serialized).isEqualTo("<list;<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=<null>;intval=0;anotherVar=false>;<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=<null>;intval=0;anotherVar=<duplicate;2>>>");
        System.out.println(serialized);
        serializer.asObject(serialized);
    }

    @Test
    public void shouldHandleClassWithMap() throws Exception {
        ClassWithMap classWithMap = new ClassWithMap();
        Map<String,String> myMap = new HashMap<>();
        myMap.put("key2","value2");
        myMap.put("key1","value1");
        classWithMap.setMyMap(myMap);

        String serialized = serializer.asString(classWithMap);

        System.out.println(serialized);

        ClassWithMap cloned = (ClassWithMap) serializer.asObject(serialized);

        Map<String, String> clonedMap = cloned.getMyMap();
        assertThat(clonedMap.size()).isEqualTo(2);
        assertThat(clonedMap.get("key1")).isEqualTo("value1");
        assertThat(clonedMap.get("key2")).isEqualTo("value2");
    }

    @Test
    public void shouldHandleClassWithOtherClassInside() throws Exception {
        ClassWithOtherClass classWithOtherClass = new ClassWithOtherClass().setMyProperty(Arrays.asList(new ClassWithSimpleFields().setIntval(72).setStringval("Anders"),
                new ClassWithSimpleFields().setIntval(17).setStringval("John F")));

        String serializedValue = serializer.asString(classWithOtherClass);
        System.out.println(serializedValue);
        ClassWithOtherClass duplicate = (ClassWithOtherClass) serializer.asObject(serializedValue);

        assertThat(duplicate.getMyProperty()).hasSize(2);
        ClassWithSimpleFields classWithSimpleFields = duplicate.getMyProperty().get(0);
        assertThat(classWithSimpleFields.getStringval()).isEqualTo("Anders");

    }

    @Test
    public void shouldHandleLangClasses() throws Exception {
        String serialized = serializer.asString("abc");
        assertThat(serialized).isEqualTo("<java.lang.String;abc>");

        String duplicate = (String) serializer.asObject(serialized);

        assertThat(duplicate).isEqualTo("abc");
    }

    @Test
    public void shouldHandleEnum() throws Exception {
        String serialized = serializer.asString(DummyEnum.TWO);
        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.DummyEnum;TWO>");
        DummyEnum dummyEnum = (DummyEnum) serializer.asObject(serialized);
        assertThat(dummyEnum).isEqualTo(DummyEnum.TWO);
    }

    @Test
    public void shouldHandleEnumAsField() throws Exception {
        ClassWithEnum classWithEnum = new ClassWithEnum();
        classWithEnum.setMyEnum(DummyEnum.THREE).setMyText("hello");

        String serialized = serializer.asString(classWithEnum);

        ClassWithEnum duplicate = (ClassWithEnum) serializer.asObject(serialized);

        assertThat(duplicate).isNotNull();
        assertThat(duplicate.getMyEnum()).isEqualTo(DummyEnum.THREE);
        assertThat(duplicate.getMyText()).isEqualTo("hello");
    }

    @Test
    public void shouldDeDeSerializeBooleanSeparately(){
        Boolean bool = false;
        String serializedBool = serializer.asString(bool);
        bool = (Boolean) serializer.asObject(serializedBool);
        assertNotNull(bool);
    }

    @Test
    public void shouldHandleLoops() throws Exception {
        ClassWithLoop a = new ClassWithLoop();
        a.setValue("a");
        ClassWithLoop b = new ClassWithLoop();
        b.setValue("b");
        a.setOther(b);
        b.setOther(a);

        String serialized = serializer.asString(a);
        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassWithLoop;value=a;other=<no.steria.skuldsku.recorder.java.serializer.ClassWithLoop;value=b;other=<duplicate;0>>>");

        ClassWithLoop acopy = (ClassWithLoop) serializer.asObject(serialized);
        assertThat(acopy).isNotNull();
        assertThat(acopy.getValue()).isEqualTo("a");
        assertThat(acopy.getOther()).isNotNull();
        assertThat(acopy.getOther().getValue()).isEqualTo("b");
        assertThat(acopy.getOther().getOther()).isSameAs(acopy);
    }

    @Test
    public void shouldHandleLinechanges() throws Exception {
        ClassWithSimpleFields classWithSimpleFields = new ClassWithSimpleFields();
        classWithSimpleFields.setStringval("Noe\nMer");

        String asString = serializer.asString(classWithSimpleFields);

        assertThat(asString).doesNotContain("\n");

        ClassWithSimpleFields dupl = (ClassWithSimpleFields) serializer.asObject(asString);

        assertThat(dupl.getStringval()).isEqualTo("Noe\nMer");
    }
    
    @Test
    public void shouldHandleSelfReferenceToList() {
        List<Object> l = new ArrayList<>();
        l.add(l);
        
        final String serializedValue = serializer.asString(l);
        assertThat(serializedValue).isEqualTo("<list;<duplicate;0>>");
        
        final Object result = serializer.asObject(serializedValue);
        @SuppressWarnings("unchecked")
        List<Object> resultList = (List<Object>) result;
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0)).isSameAs(resultList);
    }

    @Test
    public void shouldHandleDataWithSemicolon() throws IOException {
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JavaCallPersister persister = new StreamRecorderCommunicator(os);
        JavaCallRecorderConfig config = JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create();

        ClassWithSimpleFields classWithSimpleFields = new ClassWithSimpleFields();
        classWithSimpleFields.setStringval("Noe;Mer");
        store(persister, "", "myTestClass", "MyTestMethod", new Object[0], classWithSimpleFields, null, 0, 0, config);

        os.flush();
        String serialized = os.toString();
        System.out.println(serialized);

        JavaCall call = (JavaCall) serializer.asObject(serialized.substring(7));

        assertEquals("<no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;stringval=Noe&semiMer;intval=0;anotherVar=false>", call.getResult());
    }
    
    public static class SuperClass {
        private String superValue;

        public String getSupervalue() {
            return superValue;
        }

        public void setSupervalue(String superValue) {
            this.superValue = superValue;
        }
    }
    
    public static class SubClass extends SuperClass {
        private String subValue;

        public String getSubvalue() {
            return subValue;
        }

        public void setSubvalue(String subValue) {
            this.subValue = subValue;
        }
    }
    
    @Test
    public void shouldHandleSubClasses() throws Exception {
        SubClass subClass = new SubClass();

        subClass.setSupervalue("a");
        subClass.setSubvalue("b");

        String asString = serializer.asString(subClass);
        System.out.println(asString);
        assertThat(subClass.getSubvalue()).isNotEqualTo(subClass.getSupervalue());
        assertThat(asString)
                .startsWith("<" + SubClass.class.getName())
                .contains("superValue=a")
                .contains("subValue=b");

        SubClass duplicate = (SubClass) serializer.asObject(asString);

        assertThat(duplicate.getSubvalue()).isNotEqualTo(duplicate.getSupervalue());
        assertThat(duplicate.getSubvalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isEqualTo("a");
        assertThat(duplicate.getSubvalue()).isEqualTo("b");

    }

    public static class SuperClass2 {
        private String value;

        public String getSupervalue() {
            return value;
        }

        public void setSupervalue(String value) {
            this.value = value;
        }
    }
    
    public static final class SubClass2 extends SuperClass2 {
        private String value;

        public String getSubvalue() {
            return value;
        }

        public void setSubvalue(String value) {
            this.value = value;
        }
    }
    
    @Test
    @Ignore("TODO: Implement support for fields having the same name in super and sub classes.")
    public void shouldHandleSubclassesShadowing() throws Exception {
        SubClass2 subClass = new SubClass2();

        subClass.setSupervalue("a");
        subClass.setSubvalue("b");

        String asString = serializer.asString(subClass);

        assertThat(subClass.getSubvalue()).isNotEqualTo(subClass.getSupervalue());
        assertThat(asString)
                .startsWith("<" + SubClass2.class.getName())
                .contains(SuperClass2.class.getName() + ".value=a")
                .contains("value=b");

        SubClass2 duplicate = (SubClass2) serializer.asObject(asString);

        assertThat(duplicate.getSubvalue()).isNotEqualTo(duplicate.getSupervalue());
        assertThat(duplicate.getSubvalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isEqualTo("a");
        assertThat(duplicate.getSubvalue()).isEqualTo("b");

    }
    
    @Test
    @Ignore("TODO: Add support for classes with a reference to the outer object (instance classes etc)")
    public void shouldHandleInnerClasses() throws Exception {
        class SuperClass3 {
            private String superValue;

            public String getSupervalue() {
                return superValue;
            }

            public void setSupervalue(String superValue) {
                this.superValue = superValue;
            }
        }
        
        class SubClass3 extends SuperClass3 {
            private String subValue;

            public String getSubvalue() {
                return subValue;
            }

            public void setSubvalue(String subValue) {
                this.subValue = subValue;
            }
        }

        SubClass3 subClass = new SubClass3();

        subClass.setSupervalue("a");
        subClass.setSubvalue("b");

        String asString = serializer.asString(subClass);

        assertThat(subClass.getSubvalue()).isNotEqualTo(subClass.getSupervalue());
        assertThat(asString)
                .startsWith("<" + SubClass3.class.getName())
                .contains("superValue=a")
                .contains("subValue=b");

        SubClass3 duplicate = (SubClass3) serializer.asObject(asString);

        assertThat(duplicate.getSubvalue()).isNotEqualTo(duplicate.getSupervalue());
        assertThat(duplicate.getSubvalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isNotNull();
        assertThat(duplicate.getSupervalue()).isEqualTo("a");
        assertThat(duplicate.getSubvalue()).isEqualTo("b");

    }
    
    @Test
    public void shouldHandleClassesWithNoEmptyConstructor() {
        final ClassSerializer cs = new ClassSerializer();

        Test1 test1 = new Test1(1, 2);
        test1.testField = "bar";
        
        Test2 test2 = new Test2(1, 2);
        test2.testField = "foo";
        test2.testRef = test1;
        
        final String serializedObject = cs.asString(test2);
        final Object deserializedObject = cs.asObject(serializedObject);
        
        assertThat(deserializedObject.getClass()).isEqualTo(Test2.class);
        final Test2 test2NewObject = (Test2) deserializedObject;
        assertTrue(test2NewObject != test2);
        assertThat(test2NewObject.testField).isEqualTo(test2.testField);
        assertTrue(test2NewObject.testRef != test1);
        assertThat(test2NewObject.testRef.testField).isEqualTo(test1.testField);
    }
    
    public final static class Test1 {
        private String testField;
        
        private Test1(int x, int y) {
        }
    }
    
    public final static class Test2 {
        private String testField;
        private Test1 testRef;
        
        private Test2(int x, int y) {
        }
    }
    
    
    public static class TestIgnoreSuper {
        protected String c;
    }
    
    public final static class TestIgnore extends TestIgnoreSuper {
        private String a;
        private String b;
    }
    
    @Test
    public void shouldIgnoreGivenFields() {
        final TestIgnore ti = new TestIgnore();
        ti.a = "foo";
        ti.b = "bar";
        ti.c = "42";
        
        ClassSerializer.addGlobalIgnoreField("no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestIgnore.b");
        ClassSerializer.addGlobalIgnoreField("no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestIgnoreSuper.c");
        final ClassSerializer cs = new ClassSerializer();
        final String serializedValue = cs.asString(ti);
        assertThat(serializedValue).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestIgnore;a=foo>");
        
        final TestIgnore tiDuplicate = (TestIgnore) cs.asObject(serializedValue);
        assertThat(tiDuplicate.a).isEqualTo(ti.a);
        assertThat(tiDuplicate.b).isEqualTo(null);
    }
    
    public static final class TestDucplicates {
        private List<TestDucplicates2> list = new ArrayList<TestDucplicates2>();
    }
    
    public static final class TestDucplicates2 {
        private String field;
        
        TestDucplicates2(String field) {
            this.field = field;
        }
    }
    
    @Test
    public void shouldHandleDuplicatesInLists() {
        final TestDucplicates td = new TestDucplicates();
        TestDucplicates2 td2 = new TestDucplicates2("OK");
        td.list.add(td2);
        td.list.add(td2);
        
        final ClassSerializer cs = new ClassSerializer();
        String serializedValue = cs.asString(td);
        Object o = cs.asObject(serializedValue);
    }
    
    public static final class TestDucplicates3 {
        private String field1;
        private String field2;
        
        TestDucplicates3(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }
    
    @Test
    public void shouldHandleDuplicateObjects() {
        ClassSerializer.removeNonDuplicationClass(String.class);
        final String s = "OK";
        TestDucplicates3 td3 = new TestDucplicates3(s, s);
        
        final ClassSerializer cs = new ClassSerializer();
        String serializedValue = cs.asString(td3);
        assertThat(serializedValue).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestDucplicates3;field1=OK;field2=<duplicate;1>>");
        
        Object o = cs.asObject(serializedValue);
        ClassSerializer.addNonDuplicationClass(String.class);
    }
    
    @Test
    public void equalsShouldNotMeanDuplicateGetsUsed() {
        TestDucplicates3 td3 = new TestDucplicates3(new String("OK"), new String("OK"));
        
        final ClassSerializer cs = new ClassSerializer();
        String serializedValue = cs.asString(td3);
        assertThat(serializedValue).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestDucplicates3;field1=OK;field2=OK>");
        
        cs.asObject(serializedValue);
    }
    
    public static final class TestDucplicates4 {
        private int field1;
        private int field2;
        
        TestDucplicates4(int field1, int field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }
    
    @Test
    public void primitiveTypesShouldNotUseDuplicate() {
        TestDucplicates4 td3 = new TestDucplicates4(42, 42);
        
        final ClassSerializer cs = new ClassSerializer();
        String serializedValue = cs.asString(td3);
        assertThat(serializedValue).isEqualTo("<no.steria.skuldsku.recorder.java.serializer.ClassSerializerTest$TestDucplicates4;field1=42;field2=42>");
        
        cs.asObject(serializedValue);
    }
    
    
    public static final class TestFieldSemiColonEscape {
        String field1 = "foo;;bar";
    }
    
    @Test
    public void shouldHandleSemiColonsInFields() {
        final ClassSerializer cs = new ClassSerializer();
        final String serializedValue = cs.asString(new TestFieldSemiColonEscape());
        assertThat(serializedValue).isEqualTo("<" + TestFieldSemiColonEscape.class.getName() +  ";field1=foo&semi&semibar>");
    }
}
