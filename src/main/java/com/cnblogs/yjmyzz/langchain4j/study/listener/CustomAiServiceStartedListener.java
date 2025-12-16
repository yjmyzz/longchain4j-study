package com.cnblogs.yjmyzz.langchain4j.study.listener;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.observability.api.event.AiServiceStartedEvent;
import dev.langchain4j.observability.api.listener.AiServiceStartedListener;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author junmingyang
 */
public class CustomAiServiceStartedListener implements AiServiceStartedListener {

    @Override
    public void onEvent(AiServiceStartedEvent event) {
        InvocationContext invocationContext = event.invocationContext();
        Optional<SystemMessage> systemMessage = event.systemMessage();
        UserMessage userMessage = event.userMessage();

        // The invocationId will be the same for all events related to the same LLM invocation
        UUID invocationId = invocationContext.invocationId();
        String aiServiceInterfaceName = invocationContext.interfaceName();
        String aiServiceMethodName = invocationContext.methodName();
        List<Object> aiServiceMethodArgs = invocationContext.methodArguments();
        Object chatMemoryId = invocationContext.chatMemoryId();
        Instant eventTimestamp = invocationContext.timestamp();

        System.out.println("AiServiceStartedEvent: " +
                "invocationId=" + invocationId +
                ", aiServiceInterfaceName=" + aiServiceInterfaceName +
                ", aiServiceMethodName=" + aiServiceMethodName +
                ", aiServiceMethodArgs=" + aiServiceMethodArgs +
                ", chatMemoryId=" + chatMemoryId +
                ", eventTimestamp=" + eventTimestamp +
                ", userMessage=" + userMessage +
                ", systemMessage=" + systemMessage);
    }


}
