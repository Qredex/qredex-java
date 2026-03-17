#!/usr/bin/env bash
# Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
# Licensed under the Apache License, Version 2.0.

set -e

VERSION="$1"

if [ -z "$VERSION" ]; then
    echo "Usage: ./scripts/release-version.sh <version>"
    echo "Example: ./scripts/release-version.sh 0.2.0"
    exit 1
fi

# Validate semver format
if ! echo "$VERSION" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+(-[0-9A-Za-z.-]+)?$'; then
    echo "Error: Invalid semver format: $VERSION"
    echo "Expected format: X.Y.Z or X.Y.Z-prerelease"
    exit 1
fi

echo "Updating version to: $VERSION"

# Update pom.xml
mvn versions:set -DnewVersion="$VERSION" -DgenerateBackupPoms=false

# Update QredexUserAgent.java
sed -i.bak "s/SDK_VERSION = \"[^\"]*\"/SDK_VERSION = \"$VERSION\"/" \
    src/main/java/com/qredex/sdk/internal/QredexUserAgent.java
rm -f src/main/java/com/qredex/sdk/internal/QredexUserAgent.java.bak

# Update README.md
sed -i.bak "s/<version>[^<]*<\/version>/<version>$VERSION<\/version>/" README.md
sed -i.bak "s/implementation 'com.qredex:sdk:[^']*'/implementation 'com.qredex:sdk:$VERSION'/" README.md
rm -f README.md.bak

echo ""
echo "✓ Version updated in:"
echo "  - pom.xml"
echo "  - src/main/java/com/qredex/sdk/internal/QredexUserAgent.java"
echo "  - README.md"
echo ""
echo "Next steps:"
echo "  1. Update CHANGELOG.md"
echo "  2. Run: mvn clean verify"
echo "  3. Commit: git add -A && git commit -m 'chore: bump version to $VERSION'"
echo "  4. Push: git push origin main"
echo "  5. GitHub Actions will automatically:"
echo "     - Create tag v$VERSION"
echo "     - Run tests"
echo "     - Publish to Maven Central (if credentials configured)"
echo "     - Create GitHub Release"
