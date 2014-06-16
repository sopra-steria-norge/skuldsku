package no.steria.copito.recorder.httprecorder;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class RequestWrapper extends HttpServletRequestWrapper {
    private Map<String,List<String>> parameters;

    private final ReportObject reportObject;

    public RequestWrapper(HttpServletRequest request, ReportObject reportObject) {
        super(request);
        this.reportObject = reportObject;

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ServletInputStream servletIS = super.getInputStream();

        return new ProxyServletInputStream(servletIS,reportObject);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new ProxyBufferedReader(super.getReader(),reportObject);
    }

    private synchronized void readParams() {
        if (parameters != null) {
            return;
        }
        String inputStr;
        try {
            inputStr = toString(getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        parameters=new HashMap<>();
        if (inputStr == null || inputStr.isEmpty()) {
            return;
        }
        for (String keyvalue : inputStr.split("&")) {
            String characterEncoding = getCharacterEncoding();
            if (characterEncoding == null) {
                characterEncoding = "UTF-8";
            }

            int pos = keyvalue.indexOf("=");


            String value;
            String key;
            try {
                key = URLDecoder.decode(keyvalue.substring(0, pos), characterEncoding);
                value = URLDecoder.decode(keyvalue.substring(pos + 1), characterEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            List<String> parameterValues = parameters.get(key);

            if (parameterValues == null) {
                parameterValues = new ArrayList<>();
                parameters.put(key,parameterValues);
            }

            parameterValues.add(value);
        }

    }

    @Override
    public String getParameter(String name) {
        if (parameters == null) {
            readParams();
        }

        List<String> paraval = parameters.get(name);
        if (paraval == null || paraval.isEmpty() || paraval.get(0).isEmpty()) {
            return null;
        }
        return paraval.get(0);
    }


    public Map<String, String[]> getParameterMap() {
        if (parameters == null) {
            readParams();
        }
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String,List<String>> entry : parameters.entrySet()) {
            List<String> entryval = entry.getValue();
            String[] arrval = toArray(entryval);
            result.put(entry.getKey(),arrval);
        }
        return result;
    }

    private String[] toArray(List<String> entryval) {
        String[] arrval = new String[entryval.size()];
        for (int i=0;i< entryval.size();i++) {
            arrval[i]= entryval.get(i);
        }
        return arrval;
    }


    public Enumeration<String> getParameterNames() {
        if (parameters == null) {
            readParams();
        }
        final Iterator<String> iterator = parameters.keySet().iterator();
        return new Enumeration<String>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }



    public String[] getParameterValues(String name) {
        if (parameters == null) {
            readParams();
        }
        List<String> paraval = parameters.get(name);
        if (paraval == null) {
            return null;
        }
        return toArray(paraval);
    }




    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char)c);
            }
            return result.toString();
        }
    }



}
