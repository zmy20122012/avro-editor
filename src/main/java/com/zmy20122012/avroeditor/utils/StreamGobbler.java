package com.zmy20122012.avroeditor.utils;

import java.io.*;

public class StreamGobbler extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private StringBuilder buf;
    private volatile boolean isStopped = false;

    public StreamGobbler(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.buf = new StringBuilder();
        this.isStopped = false;
    }

    public StreamGobbler(final InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.buf = new StringBuilder();
        this.isStopped = false;
    }

    @Override
    public void run() {
        try {
            PrintWriter pw = null;
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;

            if (outputStream != null) {
                pw = new PrintWriter(outputStream);
                while ((line = bufferedReader.readLine()) != null) {
                    pw.println(line);
                }
                pw.flush();
            } else {
                while ((line = bufferedReader.readLine()) != null) {
                    this.buf.append(line + "\n");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            this.isStopped = true;
            synchronized (this) {
                notify();
            }
        }
    }

    public String getContent() {
        if (!this.isStopped) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                }
            }
        }
        return this.buf.toString();
    }
}
