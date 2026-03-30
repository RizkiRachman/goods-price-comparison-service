# Modular Configuration Guide

This project uses a **parent-child configuration pattern** with Spring Boot's `spring.config.import` feature.

## Architecture

```
application.properties (Parent - imports children)
├── config/
│   ├── database.properties (Database config)
│   ├── llm.properties (LLM/OCR configuration)
│   ├── features.properties (Feature flags)
│   ├── logging.properties (Logging levels)
│   └── service.properties (Service settings)
└── application-{profile}.properties (Profile overrides)
```

## How It Works

### 1. Parent Configuration (application.properties)

The parent file imports child configurations:

```properties
spring.config.import=classpath:config/database.properties
```

### 2. Child Configurations

Each child file contains related settings:

**config/llm.properties:**
```properties
# Select provider
core.llm.provider=local

# Provider configs
llm.local.base-url=http://localhost:11434
llm.openai.base-url=https://api.openai.com/v1

# Dynamic reference (uses selected provider)
llm.config.base-url=${llm.${core.llm.provider}.base-url}
```

### 3. Property References

You can reference properties from other files:

```properties
# In database.properties
db.url=jdbc:postgresql://${db.host}:${db.port}/${db.name}

# In service.properties (references database config)
storage.upload-path=${storage.base-path}/${spring.application.name}/uploads

# In llm.properties (references database config)
ocr.timeout-seconds=${db.pool.max-size:10}
```

## Key Benefits

1. **Separation of Concerns** - Each config file has a single purpose
2. **Easy Maintenance** - Find settings quickly by category
3. **Environment-Specific** - Override entire files for different environments
4. **Human-Friendly** - Less scrolling through one giant file
5. **Validation** - Can test individual config files

## Usage Examples

### Switch LLM Provider

Just change one line in `config/llm.properties`:

```properties
# From
core.llm.provider=local

# To
core.llm.provider=openai

# All other settings automatically update via references!
```

### Profile-Specific Overrides

Create `application-local.properties`:

```properties
# Import local overrides
spring.config.import=classpath:config/local.properties
```

Or modify the parent import:

```properties
spring.config.import=classpath:config/database.properties,\
                     classpath:config/llm.properties,\
                     optional:classpath:config/local.properties
```

### Testing Configuration

Run the `ModularConfigurationTest` to verify all properties load:

```bash
mvn test -Dtest=ModularConfigurationTest
```

## Adding New Configuration

1. Create a new file in `src/main/resources/config/`:
   ```properties
   # config/new-feature.properties
   feature.enabled=true
   feature.timeout=30
   ```

2. Import it in `application.properties`:
   ```properties
   spring.config.import=classpath:config/database.properties,\
                        classpath:config/new-feature.properties
   ```

3. Reference it anywhere:
   ```properties
   service.timeout=${feature.timeout}
   ```

## Property Precedence

Spring Boot loads properties in this order (later overrides earlier):

1. `application.properties` (parent)
2. `config/*.properties` (imported children)
3. `application-{profile}.properties` (profile-specific)
4. Command-line arguments

## Environment Variables

All properties support environment variable fallbacks:

```properties
db.password=${DB_PASSWORD:default_password}
llm.api.key=${OPENAI_API_KEY:}
```

## Best Practices

1. **Group by Feature** - Put all LLM settings in one file
2. **Use References** - Avoid duplicating values
3. **Provide Defaults** - Use `${property:default}` syntax
4. **Document** - Add comments explaining each section
5. **Validate** - Write tests for complex configurations

## Troubleshooting

### Property Not Found
Check import order - children are loaded after parent, so parent can't reference child's properties unless imported first.

### Circular References
Avoid:
```properties
# Bad!
a=${b}
b=${a}
```

### Import Errors
Use `optional:` prefix for optional imports:
```properties
spring.config.import=optional:classpath:config/local.properties
```

## Testing

Run configuration tests:
```bash
mvn test -Dtest=ModularConfigurationTest
```

This validates:
- All imports work correctly
- Property references resolve
- Default values are set
- Cross-file references work
