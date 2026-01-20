package com.example.mcp.prompts;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Handles prompt operations for the MCP server.
 */
public class PromptHandlers {
    
    private static final Logger log = LoggerFactory.getLogger(PromptHandlers.class);
    
    /**
     * Registers all prompt handlers with the MCP server.
     * 
     * @param server the MCP server to register handlers with
     */
    public static void register(McpServer server) {
        // Register prompt list handler
        server.addPromptListHandler(() -> {
            log.debug("Listing available prompts");
            return Mono.just(PromptDefinitions.getPrompts());
        });
        
        // Register prompt get handler
        server.addPromptGetHandler(PromptHandlers::handleGetPrompt);
    }
    
    /**
     * Handles getting a prompt by name with arguments.
     * 
     * @param name the prompt name
     * @param arguments the prompt arguments
     * @return the prompt result
     */
    private static Mono<GetPromptResult> handleGetPrompt(String name, Map<String, String> arguments) {
        log.info("Getting prompt: {} with arguments: {}", name, arguments);
        
        switch (name) {
            case "research":
                return handleResearchPrompt(arguments);
            case "fact-check":
                return handleFactCheckPrompt(arguments);
            default:
                return Mono.error(new IllegalArgumentException("Unknown prompt: " + name));
        }
    }
    
    /**
     * Handles the research prompt generation.
     * 
     * @param arguments the prompt arguments
     * @return the prompt result
     */
    private static Mono<GetPromptResult> handleResearchPrompt(Map<String, String> arguments) {
        String topic = arguments.getOrDefault("topic", "general topic");
        String depth = arguments.getOrDefault("depth", "standard");
        
        String description = "Research prompt for: " + topic + " (depth: " + depth + ")";
        
        String systemMessage = buildResearchSystemMessage(depth);
        String userMessage = buildResearchUserMessage(topic, depth);
        
        List<PromptMessage> messages = List.of(
            new PromptMessage(Role.USER, new TextContent(systemMessage)),
            new PromptMessage(Role.ASSISTANT, new TextContent(
                "I'll help you research \"" + topic + "\" using web search. " +
                "I'll conduct a " + depth + " investigation and provide you with comprehensive findings."
            )),
            new PromptMessage(Role.USER, new TextContent(userMessage))
        );
        
        log.debug("Generated research prompt for topic: {} ({})", topic, depth);
        
        return Mono.just(new GetPromptResult(description, messages));
    }
    
    /**
     * Builds the system message for research prompts.
     * 
     * @param depth the research depth
     * @return the system message
     */
    private static String buildResearchSystemMessage(String depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a research assistant with web search capabilities. ");
        
        switch (depth) {
            case "quick":
                sb.append("Provide a brief overview with 2-3 key points from search results.");
                break;
            case "comprehensive":
                sb.append("Conduct thorough research with multiple searches, cross-reference sources, ");
                sb.append("and provide detailed analysis with citations.");
                break;
            default: // standard
                sb.append("Search for relevant information and summarize key findings with sources.");
        }
        
        return sb.toString();
    }
    
    /**
     * Builds the user message for research prompts.
     * 
     * @param topic the research topic
     * @param depth the research depth
     * @return the user message
     */
    private static String buildResearchUserMessage(String topic, String depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please research the following topic: ").append(topic).append("\n\n");
        
        switch (depth) {
            case "quick":
                sb.append("I need a quick summary. Focus on the most important points.");
                break;
            case "comprehensive":
                sb.append("Please conduct comprehensive research including:\n");
                sb.append("1. Background and context\n");
                sb.append("2. Current state and recent developments\n");
                sb.append("3. Key perspectives and debates\n");
                sb.append("4. Reliable sources and citations\n");
                sb.append("5. Summary and key takeaways");
                break;
            default: // standard
                sb.append("Please provide a balanced overview with key facts and sources.");
        }
        
        return sb.toString();
    }
    
    /**
     * Handles the fact-check prompt generation.
     * 
     * @param arguments the prompt arguments
     * @return the prompt result
     */
    private static Mono<GetPromptResult> handleFactCheckPrompt(Map<String, String> arguments) {
        String claim = arguments.getOrDefault("claim", "");
        
        if (claim.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Missing required 'claim' argument"));
        }
        
        String description = "Fact-check prompt for claim verification";
        
        List<PromptMessage> messages = List.of(
            new PromptMessage(Role.USER, new TextContent(
                "You are a fact-checker with web search capabilities. Your job is to verify claims " +
                "by searching for reliable sources and evidence. Always cite your sources and " +
                "rate the claim as: TRUE, FALSE, PARTIALLY TRUE, or UNVERIFIABLE."
            )),
            new PromptMessage(Role.ASSISTANT, new TextContent(
                "I'll fact-check the claim by searching for reliable sources and evidence. " +
                "I'll provide a clear verdict with supporting sources."
            )),
            new PromptMessage(Role.USER, new TextContent(
                "Please fact-check the following claim:\n\n\"" + claim + "\"\n\n" +
                "Search for evidence both supporting and contradicting this claim, " +
                "then provide your assessment."
            ))
        );
        
        log.debug("Generated fact-check prompt for claim: {}", claim);
        
        return Mono.just(new GetPromptResult(description, messages));
    }
}
