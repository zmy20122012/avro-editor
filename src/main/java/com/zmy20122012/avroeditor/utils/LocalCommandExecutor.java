package com.zmy20122012.avroeditor.utils;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;

public class LocalCommandExecutor {

    static ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 3L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public ExecuteResult executeCommand(String command, long timeout) {
        //"/tmp/avro-editor/a.log"
        return executeCommand(command, timeout, null);
    }

    public ExecuteResult executeCommand(String command, long timeout, String outputFilePath) {
        Process process = null;
        InputStream pIn = null;
        InputStream pErr = null;
        StreamGobbler outputGobbler = null;
        StreamGobbler errorGobbler = null;
        Future<Integer> executeFuture = null;
        try {
            process = Runtime.getRuntime().exec(command);
            final Process p = process;

            // close process's output stream.
            p.getOutputStream().close();

            pIn = process.getInputStream();

            if (outputFilePath != null) {
                FileOutputStream fos = new FileOutputStream(outputFilePath);
                outputGobbler = new StreamGobbler(pIn, fos);
                outputGobbler.start();
            } else {
                outputGobbler = new StreamGobbler(pIn);
                outputGobbler.start();
            }

            pErr = process.getErrorStream();
            errorGobbler = new StreamGobbler(pErr);
            errorGobbler.start();


            executeFuture = pool.submit(() -> {
                p.waitFor();
                return p.exitValue();
            });
            int exitCode = executeFuture.get(timeout, TimeUnit.MILLISECONDS);
            return new ExecuteResult(exitCode, outputGobbler.getContent());

        } catch (IOException ex) {
            System.out.println("The command [" + command + "] execute failed.");
            ex.printStackTrace();
            return new ExecuteResult(-1, null);
        } catch (TimeoutException ex) {
            System.out.println("The command [" + command + "] timed out.");
            ex.printStackTrace();
            return new ExecuteResult(-1, null);
        } catch (ExecutionException ex) {
            System.out.println("The command [" + command + "] did not complete due to an execution error.");
            ex.printStackTrace();
            return new ExecuteResult(-1, null);
        } catch (InterruptedException ex) {
            System.out.println("The command [" + command + "] did not complete due to an interrupted error.");
            ex.printStackTrace();
            return new ExecuteResult(-1, null);
        } finally {
            if (executeFuture != null) {
                try {
                    executeFuture.cancel(true);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (pIn != null) {
                this.closeQuietly(pIn);
                if (outputGobbler != null && !outputGobbler.isInterrupted()) {
                    outputGobbler.interrupt();
                }
            }
            if (pErr != null) {
                this.closeQuietly(pErr);
                if (errorGobbler != null && !errorGobbler.isInterrupted()) {
                    errorGobbler.interrupt();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void closeQuietly(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}