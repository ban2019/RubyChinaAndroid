package org.rubychinaandroid.api;


public interface RubyChinaApiListener<E> {

    public void onSuccess(E data);

    //public void onSuccess(E data, int totalPages, int currentPage);

    public void onFailure(String data);
}
