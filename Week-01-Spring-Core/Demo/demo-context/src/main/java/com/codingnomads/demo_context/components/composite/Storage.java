package com.codingnomads.demo_context.components.composite;

import com.codingnomads.demo_context.components.simple.Hdd;
import com.codingnomads.demo_context.components.simple.Ssd;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Storage {

    private final Hdd hdd1;
    private final Hdd hdd2;
    private final Ssd ssd;

    public Storage(Hdd seagate, @Qualifier("westernDigital") Hdd hdd, Ssd ssd) {
        this.hdd1 = seagate;
        this.hdd2 = hdd;
        this.ssd = ssd;
    }

    @Override
    public String toString() {
        return "Storage(" + Integer.toHexString(this.hashCode()) + "){" +
                "hdd1=" + hdd1 +
                "hdd2=" + hdd2 +
                ", ssd=" + ssd +
                '}';
    }
}
