# My MCP Server

A Model Context Protocol (MCP) server built with Java and the official MCP Java SDK, providing web search capabilities.

## Features

- ✅ **Tools**: `websearch` - Search the web for information
- ✅ **Resources**: Search history, server configuration
- ✅ **Prompts**: Research assistant, fact-checking
- ✅ Reactive Streams with Project Reactor
- ✅ Structured logging with SLF4J/Logback
- ✅ Unit tests

## Requirements

- Java 17 or later
- Gradle 7+

## Project Structure

```
my-mcp-server/
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/java/com/example/mcp/
│   │   ├── McpServerApplication.java      # Main entry point
│   │   ├── tools/
│   │   │   ├── ToolDefinitions.java       # Tool schemas
│   │   │   └── ToolHandlers.java          # Tool implementations
│   │   ├── resources/
│   │   │   ├── ResourceDefinitions.java   # Resource schemas
│   │   │   └── ResourceHandlers.java      # Resource implementations
│   │   └── prompts/
│   │       ├── PromptDefinitions.java     # Prompt schemas
│   │       └── PromptHandlers.java        # Prompt implementations
│   ├── main/resources/
│   │   └── logback.xml                    # Logging configuration
│   └── test/java/com/example/mcp/
│       └── McpServerTest.java             # Unit tests
└── gradle/wrapper/
    └── gradle-wrapper.properties
```

## Build

```bash
cd my-mcp-server
./gradlew build
```

## Run

```bash
./gradlew run
```

Or run the JAR directly:

```bash
java -jar build/libs/my-mcp-server-1.0.0.jar
```

## Testing

```bash
./gradlew test
```

## Available Tools

### websearch

Search the web for information on any topic.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | The search query |
| `numResults` | integer | No | Number of results (default: 5, max: 10) |

**Example:**
```json
{
  "name": "websearch",
  "arguments": {
    "query": "latest AI developments 2026",
    "numResults": 5
  }
}
```

## Available Resources

| URI | Name | Description |
|-----|------|-------------|
| `resource://search/history` | Search History | Recent web search queries and results |
| `resource://config` | Server Configuration | Current server configuration settings |

## Available Prompts

### research

Generate a research prompt for investigating a topic.

| Argument | Required | Description |
|----------|----------|-------------|
| `topic` | Yes | The topic to research |
| `depth` | No | 'quick', 'standard', or 'comprehensive' |

### fact-check

Generate a fact-checking prompt to verify claims.

| Argument | Required | Description |
|----------|----------|-------------|
| `claim` | Yes | The claim or statement to fact-check |

## Integration with Claude Desktop

Add to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "my-mcp-server": {
      "command": "java",
      "args": ["-jar", "/path/to/my-mcp-server/build/libs/my-mcp-server-1.0.0.jar"]
    }
  }
}
```

## Configuration

The server uses DuckDuckGo's Instant Answer API for web search by default. For production use, consider integrating:

- Google Custom Search API
- Bing Search API  
- Brave Search API

Update `ToolHandlers.java` to use your preferred search provider.

## License

MIT
