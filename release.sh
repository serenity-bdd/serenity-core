#!/bin/bash

set -euo pipefail

### --- Configuration ---
MODULE_NAME="serenity-bdd"
RELEASE_VERSION=$1             # e.g. 4.2.51
NEXT_SNAPSHOT_VERSION=$2       # e.g. 4.2.52-SNAPSHOT
GPG_KEYID="your-key-id"        # Optional: leave empty if default key is used
PROFILE="release"              # Optional Maven profile name

### --- Usage Check ---
if [[ -z "$RELEASE_VERSION" || -z "$NEXT_SNAPSHOT_VERSION" ]]; then
  echo "Usage: ./release.sh <release-version> <next-snapshot-version>"
  exit 1
fi

echo "🚀 Starting release of $MODULE_NAME version $RELEASE_VERSION"

### --- Step 1: Set release version ---
echo "🔧 Updating version to $RELEASE_VERSION"
mvn versions:set -DnewVersion="$RELEASE_VERSION" -DgenerateBackupPoms=false
mvn versions:commit

### --- Step 2: Commit and tag ---
git commit -am "🔖 Release $RELEASE_VERSION"
git tag -a "v$RELEASE_VERSION" -m "Release $RELEASE_VERSION"

### --- Step 3: Deploy with GPG signing and skip tests ---
echo "🔐 Deploying signed artifacts to OSSRH (tests skipped)..."
mvn clean deploy \
  -P$PROFILE \
  -DskipTests \
  -Dgpg.keyname="$GPG_KEYID" \
  -Dgpg.skip=false

### --- Step 4: Set next snapshot version ---
echo "🔄 Setting next development version to $NEXT_SNAPSHOT_VERSION"
mvn versions:set -DnewVersion="$NEXT_SNAPSHOT_VERSION" -DgenerateBackupPoms=false
mvn versions:commit

### --- Step 5: Commit and push ---
git commit -am "🔄 Prepare for $NEXT_SNAPSHOT_VERSION"
git push origin main --tags

echo "✅ Release $RELEASE_VERSION completed and next version set to $NEXT_SNAPSHOT_VERSION"
