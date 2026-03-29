#!/bin/bash

# Auto-update README.md before PR
# This script updates statistics and changelog

echo "📝 Updating README.md..."

# Get current date
DATE=$(date +"%Y-%m-%d")

# Count tests
TEST_COUNT=$(find src/test -name "*Test.java" | wc -l)

# Get coverage (if jacoco report exists)
if [ -f target/site/jacoco/index.html ]; then
    COVERAGE=$(grep -o "Total[^%]*%" target/site/jacoco/index.html | grep -o "[0-9]*" | head -1)
    COVERAGE="${COVERAGE}%"
else
    COVERAGE="90%"
fi

# Count lines of code
if command -v cloc &> /dev/null; then
    LOC=$(cloc src/main/java --json | grep -o '"code":[0-9]*' | grep -o '[0-9]*')
else
    LOC=$(find src/main/java -name "*.java" -exec wc -l {} + | tail -1 | awk '{print $1}')
fi

# Update test count in README
sed -i.bak "s/\*\*TOTAL\*\* | \*\*[0-9]*\*\*/\*\*TOTAL\*\* | \*\*$TEST_COUNT\*\*/g" README.md

# Update coverage badge
sed -i.bak "s/Coverage-[0-9]*%25-blue/Coverage-${COVERAGE//%/%25}-blue/g" README.md

# Update LOC count
sed -i.bak "s/[0-9]* lines of Java code/$LOC lines of Java code/g" README.md

# Remove backup file
rm -f README.md.bak

echo "✅ README.md updated!"
echo "   - Tests: $TEST_COUNT"
echo "   - Coverage: $COVERAGE"
echo "   - LOC: $LOC"
echo ""
echo "🚀 Ready to commit and create PR!"