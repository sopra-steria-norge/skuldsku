package no.steria.copito.recorder.httprecorder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * A PrintWriter that writes a copy of everything it writes to the StringWriter.
 */
public class PrintWriterCopier extends PrintWriter {
    private final PrintWriter copier;
    private final PrintWriter delegate;


    public PrintWriterCopier(StringWriter stringCopy, PrintWriter delegate) {
        super(new StringWriter());
        this.delegate = delegate;
        this.copier = new PrintWriter(stringCopy);
    }

    @Override
    public void flush() {
        copier.flush();
        delegate.flush();
    }

    @Override
    public void close() {
        copier.close();
        delegate.close();
    }

    @Override
    public boolean checkError() {
        copier.checkError();
        return delegate.checkError();
    }



    @Override
    public void write(int c) {
        copier.write(c);
        delegate.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        copier.write(buf, off, len);
        delegate.write(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        copier.write(buf);
        delegate.write(buf);
    }

    @Override
    public void write(String s, int off, int len) {
        copier.write(s, off, len);
        delegate.write(s, off, len);
    }

    @Override
    public void write(String s) {
        copier.write(s);
        delegate.write(s);
    }

    @Override
    public void print(boolean b) {
        copier.print(b);
        delegate.print(b);
    }

    @Override
    public void print(char c) {
        copier.print(c);
        delegate.print(c);
    }

    @Override
    public void print(int i) {
        copier.print(i);
        delegate.print(i);
    }

    @Override
    public void print(long l) {
        copier.print(l);
        delegate.print(l);
    }

    @Override
    public void print(float f) {
        copier.print(f);
        delegate.print(f);
    }

    @Override
    public void print(double d) {
        copier.print(d);
        delegate.print(d);
    }

    @Override
    public void print(char[] s) {
        copier.print(s);
        delegate.print(s);
    }

    @Override
    public void print(String s) {
        copier.print(s);
        delegate.print(s);
    }

    @Override
    public void print(Object obj) {
        copier.print(obj);
        delegate.print(obj);
    }

    @Override
    public void println() {
        copier.println();
        delegate.println();
    }

    @Override
    public void println(boolean x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(char x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(int x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(long x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(float x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(double x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(char[] x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(String x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public void println(Object x) {
        copier.println(x);
        delegate.println(x);
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
        copier.printf(format, args);
        delegate.printf(format, args);
        return this;
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
        copier.printf(l, format, args);
        delegate.printf(l, format, args);
        return this;
    }

    @Override
    public PrintWriter format(String format, Object... args) {
        copier.format(format, args);
        delegate.format(format, args);
        return this;
    }

    @Override
    public PrintWriter format(Locale l, String format, Object... args) {
        copier.format(l, format, args);
        delegate.format(l, format, args);
        return this;
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        copier.append(csq);
        delegate.append(csq);
        return this;
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        copier.append(csq, start, end);
        delegate.append(csq, start, end);
        return this;
    }

    @Override
    public PrintWriter append(char c) {
        copier.append(c);
        delegate.append(c);
        return this;
    }
}
