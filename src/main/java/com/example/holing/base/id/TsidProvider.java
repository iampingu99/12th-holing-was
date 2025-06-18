package com.example.holing.base.id;

import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.stereotype.Component;

@Component
public class TsidProvider implements IdProvider {

    @Override
    public long nextId() {
        return TsidCreator.getTsid().toLong();
    }
}
