package no.steria.skuldsku.recorder.java.recorder;

import no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;

import java.io.File;
import java.util.List;

public interface ServiceInterface {
    public String doSimpleService(String input);
    public String doWithPara(ServiceParameterClass para);
    public List<String> returnList(ClassWithSimpleFields simple);
    public String readAFile(String prefix,File file);
}
