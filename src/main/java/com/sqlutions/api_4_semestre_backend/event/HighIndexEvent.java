package com.sqlutions.api_4_semestre_backend.event;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import org.springframework.context.ApplicationEvent;

public class HighIndexEvent extends ApplicationEvent {

    private final Index index;

    public HighIndexEvent(Object source, Index index) {
        super(source);
        this.index = index;
    }

    public Index getIndex() {
        return index;
    }
}
