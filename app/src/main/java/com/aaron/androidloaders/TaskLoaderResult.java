package com.aaron.androidloaders;

/**
 * Created by Aaron on 2016/2/25.
 */
public class TaskLoaderResult<T> {

    private Exception mException;
    private T mData;

    public TaskLoaderResult() {
    }

    public TaskLoaderResult(Exception exception, T data) {
        mException = exception;
        mData = data;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
