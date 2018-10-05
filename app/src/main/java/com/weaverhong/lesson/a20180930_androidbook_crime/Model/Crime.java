package com.weaverhong.lesson.a20180930_androidbook_crime.Model;

import java.util.Date;
import java.util.UUID;

public class Crime {

    // 注意，这里更新的字段要在Schema里同步更新
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    // 注意，这里更新的字段要在Schema里同步更新

    // 这个转换方法放到这里，纯粹是因为与持久层相关，没地方放，就放这儿了
    public String getPhotoFilename() {
        return "CRIMEIMG_" + getId().toString() + ".jpg";
    }

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
