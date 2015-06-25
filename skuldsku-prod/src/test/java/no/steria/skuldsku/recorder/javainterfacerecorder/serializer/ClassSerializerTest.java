package no.steria.skuldsku.recorder.javainterfacerecorder.serializer;

import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

// Apparently you cannot use internal classes for testing this. I don't know why, it just doesn't work.
public class ClassSerializerTest {

    private final ClassSerializer serializer = new ClassSerializer();

    @Test
    public void shouldReturnClassNameForEmptyClass() throws Exception {
        assertThat(serializer.asString(new EmptyClass())).isEqualTo("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.EmptyClass>");
    }

    @Test
    public void shouldHandleNullObjects() throws Exception {
        assertThat(serializer.asString(null)).isEqualTo("<null>");
    }

    @Test
    public void shouldDeserialiseNull() throws Exception {
        assertThat(serializer.asObject(serializer.asString(null))).isNull();
    }

    @Test
    @Ignore // TODO Fix this
    public void shouldReturnClass() throws Exception {
        EmptyClass emptyClass = (EmptyClass) serializer.asObject(serializer.asString(new EmptyClass()));
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

        ClassWithArrayOfPrimitives cloned = (ClassWithArrayOfPrimitives) serializer.asObject(serialized);

        assertThat(cloned.getArrayOfLongs()).containsOnly(0l, 1l, 2l);
        assertThat(cloned.getArrayOfChars()).containsOnly('a', 'b', 'c');
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

        assertThat(serialized).isEqualTo("<array;<java.lang.Long;0>;<java.lang.Long;1>>");
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
        final String actual = serializer.asString(Arrays.asList("asdf", "asdf"));

        assertThat(serializer.asObject(actual)).isEqualTo(Arrays.asList("asdf", "asdf"));
    }

    @Test
    public void shouldParseSimpleStringField() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval("pedro");
        assertThat(serializer.asString(simpleFields)).isEqualTo("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;stringval=pedro;intval=0;anotherVar=false>");
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

        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;stringval=a&amp&semi&lt&gt&percentbc;intval=0;anotherVar=false>");
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
        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.DummyEnum;TWO>");
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
        assertThat(serialized).isEqualTo("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithLoop;value=a;other=<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithLoop;value=b;other=<duplicate;0>>>");

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
    public void shouldHandleSubclasses() throws Exception {
        SubClass subClass = new SubClass();

        subClass.setSupervalue("a");
        subClass.setSubvalue("b");

        String asString = serializer.asString(subClass);

        assertThat(asString)
                .startsWith("<no.steria.skuldsku.recorder.javainterfacerecorder.serializer.SubClass")
                .contains("supervalue=a")
                .contains("subvalue=b");

        SubClass duplicate = (SubClass) serializer.asObject(asString);

        assertThat(duplicate.getSupervalue()).isEqualTo("a");

    }
}
