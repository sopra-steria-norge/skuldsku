package no.steria.skuldsku.recorder.java;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

public class JavaCall implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String clientIdentifier = "";
    private String className;
    private String methodname;
    private String parameters;
    private String result;
    private String thrown;
    private String startTime;
    private String endTime;

    public JavaCall(String clientIdentifier, String className, String methodname, String parameters, String result, String thrown, String startTime, String endTime) {
        this.clientIdentifier = clientIdentifier;
        this.className = className;
        this.methodname = methodname;
        this.parameters = parameters;
        this.result = result;
        this.thrown = thrown;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public JavaCall(String clientIdentifier, String className, String methodname, String parameters, String result, String thrown, long startTime, long endTime) {
        this.clientIdentifier = clientIdentifier;
        this.className = className;
        this.methodname = methodname;
        this.parameters = parameters;
        this.result = result;
        this.thrown = thrown;
        
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.startTime = simpleDateFormat.format(startTime);
        this.endTime = simpleDateFormat.format(endTime);
    }
    
    public JavaCall(String clientIdentifier, String className, String methodname, Object[] args, Object result, Throwable thrown, long startTime, long endTime) {
        final ClassSerializer classSerializer = new ClassSerializer();

        this.clientIdentifier = clientIdentifier;
        this.className = className;
        this.methodname = methodname;
        this.parameters = classSerializer.asString(args);
        this.result = classSerializer.asString(result);
        this.thrown = classSerializer.asString(thrown);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.startTime = simpleDateFormat.format(startTime);
        this.endTime = simpleDateFormat.format(endTime);
    }

    public JavaCall() {

    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }
    
    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = (clientIdentifier != null) ? clientIdentifier : "";
    }
    
    /**
     * Gets the time the Java request was initiated.
     * @return The point in time using the format <code>yyyy-MM-dd'T'HH:mm:ss.SSSXXX</code>.
     */
    public String getStartTime() {
        return startTime;
    }
    
    /**
     * Gets the time the Java request ended.
     * @return The point in time using the format <code>yyyy-MM-dd'T'HH:mm:ss.SSSXXX</code>.
     */
    public String getEndTime() {
        return endTime;
    }
    
    public String getClassName() {
        return className;
    }

    public String getMethodname() {
        return methodname;
    }

    public String getParameters() {
        return parameters;
    }

    public String getResult() {
        return result;
    }
    
    public String getThrown() {
        return thrown;
    }
    
    public void setThrown(String thrown) {
        this.thrown = thrown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaCall that = (JavaCall) o;

        if (!className.equals(that.className)) return false;
        if (!methodname.equals(that.methodname)) return false;
        if (!parameters.equals(that.parameters)) return false;
        if (!result.equals(that.result)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = className.hashCode();
        result1 = 31 * result1 + methodname.hashCode();
        result1 = 31 * result1 + parameters.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }

    public static List<JavaCall> readJavaInterfaceCalls(String filename) {
        return new FileRecorderReader(filename).getJavaInterfaceCalls();
    }

    @Override
    public String toString() {
        return "[clientIdentifier=" + clientIdentifier + ", className=" + className + ", methodname="
                + methodname + ", parameters=\n" + parameters + "\nresult=\n" + result + "\nthrown=" + thrown + "]";
    }
}
