package com.colin.java.bitmap;

import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.List;

public class RMBitmapStore {
    private final List<RoaringBitmap> data = new ArrayList<>();

    public void addFriend(int userInnerId, int friendInnerId) {
        ensureCapacity(userInnerId);
        data.get(userInnerId).add(friendInnerId);
    }

    public RoaringBitmap getFriends(int userInnerId) {
        ensureCapacity(userInnerId);
        return data.get(userInnerId);
    }

    public RoaringBitmap commonFriends(int a, int b) {
        RoaringBitmap bmA = getFriends(a);
        RoaringBitmap bmB = getFriends(b);
        RoaringBitmap result = RoaringBitmap.and(bmA, bmB);
        return result;
    }

    private void ensureCapacity(int id) {
        while (data.size() <= id) data.add(new RoaringBitmap());
    }
}