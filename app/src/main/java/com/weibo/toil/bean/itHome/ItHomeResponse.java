package com.weibo.toil.bean.itHome;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss")
public class ItHomeResponse {
    @Element(name = "channel")
    ItHomeChannel channel;

    @Attribute(name = "version")
    String version;

    public ItHomeChannel getChannel() {
        return channel;
    }

    public void setChannel(ItHomeChannel channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
