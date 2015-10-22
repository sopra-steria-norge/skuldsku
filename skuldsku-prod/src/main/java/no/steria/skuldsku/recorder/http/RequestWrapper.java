package no.steria.skuldsku.recorder.http;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class RequestWrapper extends HttpServletRequestWrapper {
    private Map<String,List<String>> parameters;

    private final HttpCall httpCall;

    public RequestWrapper(HttpServletRequest request, HttpCall httpCall) {
        super(request);
        this.httpCall = httpCall;

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ServletInputStream servletIS = super.getInputStream();

        return new ProxyServletInputStream(servletIS, httpCall);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new ProxyBufferedReader(super.getReader(), httpCall);
    }

    private synchronized void readParams() {
        if (parameters != null) {
            return;
        }
        
        parameters = new HashMap<>();
        
        initializePostParameters();
        initializeGetParameters();
    }

    private void initializePostParameters() {
        String inputStr;
        try {
            inputStr = toString(getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (inputStr == null || inputStr.isEmpty()) {
            return;
        }
        fromParameterStringToMap(inputStr, parameters, getCharacterEncoding());
    }
    
    private void initializeGetParameters() {
        final String inputStr = getQueryString();
        if (inputStr != null) {
            /*
             * TODO: URL encoding should always be ASCII ... but legaacy system might
             *       expect UTF-8 characters to be allowed. We should not be using
             *       getCharacterEncoding() for queryParams (as implemented below),
             *       but have charset autodetection (or at least a separate configuration).
             */
            fromParameterStringToMap(inputStr, parameters, getCharacterEncoding());
        }
    }

    private static void fromParameterStringToMap(String inputStr, Map<String,List<String>> parameters, String charset) {
        for (String keyvalue : inputStr.split("&")) {
            String characterEncoding = charset;
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

    @Override
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

    @Override
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

    @Override
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
