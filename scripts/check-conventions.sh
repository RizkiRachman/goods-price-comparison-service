#!/bin/bash
set -euo pipefail

SRC="src/main/java/com/example/goodsprice"
EXIT=0

echo "=== Checking coding conventions ==="

# 1. Verbose ternary null-check (should use ObjectUtils.getOrNull)
echo ""
echo "--- Pattern: Objects.nonNull(x) ? x.method() : null (use ObjectUtils.getOrNull) ---"
results=$(rg -n 'Objects\.nonNull\(\w+\) \? \w+\.\w+\(\) : null' --include="*.java" "$SRC" 2>/dev/null || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 2. Verbose lambda -> direct method call (should use method reference)
echo ""
echo "--- Pattern: .map(x -> method(x)) (use method reference) ---"
results=$(rg -n '\.map\(\w+ -> \w+\(\w+\)\)' --include="*.java" "$SRC" 2>/dev/null || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 3. JPA annotation in domain model
echo ""
echo "--- Pattern: JPA annotations in application/domain/model ---"
results=$(rg -n '@(Entity|Table|Column|Id|ManyToOne|OneToMany|OneToOne|ManyToMany)' --include="*.java" "$SRC/../" 2>/dev/null | rg 'application/domain/model' || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 4. Optional return in port interfaces
echo ""
echo "--- Pattern: Optional return type in port interfaces ---"
results=$(rg -n 'Optional<' --include="*.java" "$SRC" 2>/dev/null | rg 'port/' || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 5. collect(Collectors.toList()) - should use toList()
echo ""
echo "--- Pattern: .collect(Collectors.toList()) (use .toList()) ---"
results=$(rg -n 'collect\(Collectors\.toList\(\)\)' --include="*.java" "$SRC" 2>/dev/null || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 6. String.format() - should use String.formatted()
echo ""
echo "--- Pattern: String.format( (use String.formatted()) ---"
results=$(rg -n 'String\.format\(' --include="*.java" "$SRC" 2>/dev/null || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

# 7. @Data in domain model (should use @Getter @Setter @Builder)
echo ""
echo "--- Pattern: @Data in application/domain/model ---"
results=$(rg -n '@Data' --include="*.java" "$SRC" 2>/dev/null | rg 'application/domain/model' || true)
if [ -n "$results" ]; then
    echo "$results"
    EXIT=1
else
    echo "✅ None found"
fi

echo ""
if [ $EXIT -eq 0 ]; then
    echo "=== All conventions passed ==="
else
    echo "=== Some conventions FAILED (see above) ==="
fi
exit $EXIT
