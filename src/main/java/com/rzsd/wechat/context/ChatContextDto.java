package com.rzsd.wechat.context;

import java.sql.Timestamp;

public class ChatContextDto {

    private String type;
    private Timestamp lastUpdTms;
    private String word1;
    private String word2;
    private String word3;
    private String word4;
    private String word5;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getLastUpdTms() {
        return lastUpdTms;
    }

    public void setLastUpdTms(Timestamp lastUpdTms) {
        this.lastUpdTms = lastUpdTms;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }

    public String getWord4() {
        return word4;
    }

    public void setWord4(String word4) {
        this.word4 = word4;
    }

    public String getWord5() {
        return word5;
    }

    public void setWord5(String word5) {
        this.word5 = word5;
    }

}
