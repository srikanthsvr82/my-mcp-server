package com.example.mcp.prompts;

import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptArgument;

import java.util.List;

/**
 * Defines all available prompts for the MCP server.
 */
public class PromptDefinitions {
    
    /**
     * Returns the list of all available prompts.
     * 
     * @return list of prompt definitions
     */
    public static List<Prompt> getPrompts() {
        return List.of(
            createResearchPrompt(),
            createFactCheckPrompt()
        );
    }
    
    /**
     * Creates the research assistant prompt.
     * 
     * @return research prompt
     */
    private static Prompt createResearchPrompt() {
        return new Prompt(
            "research",
            "Generate a research prompt for investigating a topic using web search",
            List.of(
                new PromptArgument(
                    "topic",
                    "The topic to research",
                    true
                ),
                new PromptArgument(
                    "depth",
                    "Research depth: 'quick', 'standard', or 'comprehensive'",
                    false
                )
            )
        );
    }
    
    /**
     * Creates the fact-checking prompt.
     * 
     * @return fact-check prompt
     */
    private static Prompt createFactCheckPrompt() {
        return new Prompt(
            "fact-check",
            "Generate a fact-checking prompt to verify claims using web search",
            List.of(
                new PromptArgument(
                    "claim",
                    "The claim or statement to fact-check",
                    true
                )
            )
        );
    }
}
