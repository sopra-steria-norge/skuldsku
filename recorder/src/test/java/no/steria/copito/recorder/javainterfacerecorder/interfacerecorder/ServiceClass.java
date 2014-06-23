package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.copito.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ServiceClass implements ServiceInterface {
    public String doSimpleService(String input) {
        return "Hello " + input;
    }

    @Override
    public String doWithPara(ServiceParameterClass para) {
        if (para == null) {
            return null;
        }
        return para.getInfo();
    }

    public List<String> returnList(ClassWithSimpleFields simple) {
        if (simple == null) {
            return Arrays.asList("This","is","null");
        }
        return Arrays.asList("This","is","not","null");
    }

    @Override
    public String readAFile(String prefix,File file) {
        try (Reader reader = new FileReader(file)) {
            int c;
            StringBuilder res = new StringBuilder(prefix);
            while ((c = reader.read()) != -1) {
                res.append((char) c);
            }
            return res.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
