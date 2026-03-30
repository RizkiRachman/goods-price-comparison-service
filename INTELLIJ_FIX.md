# IntelliJ IDEA Configuration

## Fix for ExceptionInInitializerError

This error occurs due to IntelliJ IDEA's internal compiler issues with Java 17.

### Solution 1: Build and Run via Maven (Recommended)

Instead of using IntelliJ's Run button, use Maven:

```bash
# Terminal 1 - Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Or build and run JAR
mvn clean package
java -jar target/goods-price-comparison-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

### Solution 2: Configure IntelliJ Run Configuration

1. **Open Run Configuration:**
   - Run → Edit Configurations
   - Find your Application run config

2. **Modify Options:**
   - Click "Modify options" dropdown
   - Select "Do not build before run"
   - Select "Add VM options"

3. **Set VM Options:**
   ```
   -Dspring.profiles.active=local
   ```

4. **Before Launch:**
   - Remove "Build"
   - Add "Run Maven Goal" → `clean compile`

### Solution 3: Disable IntelliJ Build

1. Settings → Build → Build Tools → Maven → Runner
2. Check "Delegate IDE build/run actions to Maven"
3. Apply and restart IntelliJ

### Solution 4: Manual Build

```bash
# Build using Maven
mvn clean compile

# Then in IntelliJ:
# 1. Build → Rebuild Project
# 2. Run without build
```

## Current Status

Maven builds successfully from command line. This is an IntelliJ IDE-specific issue.

Workaround: Use Maven to run the application instead of IntelliJ's Run button.
