# Package Refactoring Summary

**Date:** 2026-03-17  
**Status:** ✅ COMPLETE

---

## Changes Made

### Maven Artifact
```xml
<!-- BEFORE -->
<dependency>
    <groupId>com.qredex</groupId>
    <artifactId>sdk</artifactId>
    <version>0.1.0</version>
</dependency>

<!-- AFTER -->
<dependency>
    <groupId>com.qredex</groupId>
    <artifactId>qredex-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle
```groovy
// BEFORE
implementation 'com.qredex:sdk:0.1.0'

// AFTER
implementation 'com.qredex:qredex-java:0.1.0'
```

### Package Names
```java
// BEFORE
package com.qredex.sdk;
import com.qredex.sdk.Qredex;
import com.qredex.sdk.exceptions.*;

// AFTER
package com.qredex;
import com.qredex.Qredex;
import com.qredex.exceptions.*;
```

---

## Migration Guide for Existing Users

### Step 1: Update Dependencies

**Maven users** — update `pom.xml`:
```xml
<artifactId>qredex-java</artifactId>
```

**Gradle users** — update `build.gradle`:
```groovy
implementation 'com.qredex:qredex-java:0.1.0'
```

### Step 2: Update Imports

Find and replace in your codebase:
```
com.qredex.sdk → com.qredex
```

**Examples:**
```java
// Old
import com.qredex.sdk.Qredex;
import com.qredex.sdk.QredexConfig;
import com.qredex.sdk.exceptions.*;
import com.qredex.sdk.model.request.*;
import com.qredex.sdk.model.response.*;

// New
import com.qredex.Qredex;
import com.qredex.QredexConfig;
import com.qredex.exceptions.*;
import com.qredex.model.request.*;
import com.qredex.model.response.*;
```

### Step 3: Rebuild
```bash
mvn clean install
# or
gradle clean build
```

---

## Verification

✅ **0 references** to old `com.qredex.sdk` package  
✅ **43 tests pass** (100%)  
✅ **Live test** properly skipped  
✅ **Build successful** (`BUILD SUCCESS`)  
✅ **All examples** updated  
✅ **All docs** updated  
✅ **Git history** preserved (files moved with `git mv`)

---

## Files Changed

- **pom.xml** — artifactId updated
- **66 Java source files** — package declarations updated
- **66 Java source files** — imports updated
- **9 example files** — imports updated
- **README.md** — Maven/Gradle examples and code snippets
- **docs/INTEGRATION_GUIDE.md** — code examples
- **docs/ERRORS.md** — code examples
- **src/main/java/com/qredex/internal/package-info.java** — Javadoc links

---

## Git Commit

```
refactor: change artifactId to qredex-java and remove .sdk from packages

BREAKING CHANGE: Package names changed from com.qredex.sdk.* to com.qredex.*

- Maven artifactId: sdk → qredex-java
- All packages: com.qredex.sdk.* → com.qredex.*
- All imports updated across source, tests, examples, and docs

Maven users must update:
<artifactId>sdk</artifactId> → <artifactId>qredex-java</artifactId>

Gradle users must update:
com.qredex:sdk:X.Y.Z → com.qredex:qredex-java:X.Y.Z

Code imports must update:
import com.qredex.sdk.* → import com.qredex.*
```

---

## Impact

**This is a BREAKING CHANGE.**

Existing users must update both:
1. Dependency declaration (artifactId)
2. Import statements (package names)

**Recommended Release:** Bump to `1.0.0` when published (per semver, breaking changes increment major version).
