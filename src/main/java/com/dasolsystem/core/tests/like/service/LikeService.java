package com.dasolsystem.core.tests.like.service;

public interface LikeService {
    void makeLikes() throws InterruptedException;
    String getCurrnetLikes();
    void makeLikesRedisson();
}
