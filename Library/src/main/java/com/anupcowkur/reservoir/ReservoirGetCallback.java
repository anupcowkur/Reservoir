package com.anupcowkur.reservoir;

/**
 *
 */
public interface ReservoirGetCallback<T> {
    public void onComplete(Exception e, T object);
}
