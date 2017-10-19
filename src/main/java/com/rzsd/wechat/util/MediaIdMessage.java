package com.rzsd.wechat.util;

import com.rzsd.wechat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class MediaIdMessage {  
    @XStreamAlias("MediaId")  
    @XStreamCDATA  
    private String MediaId;  
  
    public String getMediaId() {  
        return MediaId;  
    }  
  
    public void setMediaId(String mediaId) {  
        MediaId = mediaId;  
    }  
  
}  