package com.cnblogs.yjmyzz.langchain4j.study.listener;

import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.observability.api.event.AiServiceCompletedEvent;
import dev.langchain4j.observability.api.listener.AiServiceCompletedListener;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomAiServiceCompletedListener implements AiServiceCompletedListener {
    @Override
    public void onEvent(AiServiceCompletedEvent event) {
        InvocationContext invocationContext = event.invocationContext();
        Optional<Object> result = event.result();

        UUID invocationId = invocationContext.invocationId();
        String aiServiceInterfaceName = invocationContext.interfaceName();
        String aiServiceMethodName = invocationContext.methodName();
        List<Object> aiServiceMethodArgs = invocationContext.methodArguments();
        Object chatMemoryId = invocationContext.chatMemoryId();
        Instant eventTimestamp = invocationContext.timestamp();

        System.out.println("AiServiceCompletedListener: " +
                "invocationId=" + invocationId +
                ", aiServiceInterfaceName=" + aiServiceInterfaceName +
                ", aiServiceMethodName=" + aiServiceMethodName +
                ", aiServiceMethodArgs=" + aiServiceMethodArgs +
                ", chatMemoryId=" + chatMemoryId +
                ", eventTimestamp=" + eventTimestamp +
                ", result=" + result);
    }
}
