package com.anupcowkur.reservoir;

public interface ReservoirPutCallback {
    public void onSuccess();

    public void onFailure(Exception e);
}
