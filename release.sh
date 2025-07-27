#!/bin/bash

set -e  # Fail on error
set -o pipefail

### CONFIGURATION ###
MODULE_NAME="serenity-bdd"                # Your module/artifact ID
RELEASE_VERSION=$1                        # e.g. 4.2.51
NEXT_SNAPSHOT_VERSION=$2                  # e.g. 4.2.52-SNAPSHOT
GPG_KEYID="your-key-id"                   # Optional: set if not default key
PROFILE="release"                         # Maven profile if needed

if [[ -z "$RELEASE_VERSION" || -z "$NEXT_SNAPSHOT_VERSION" ]]; then
  echo "Usage: ./release.sh <release-version> <next-snapshot-version>"
  exit 1
fi

echo "🚀 Releasing $MODULE_NAME version $RELEASE_VERSION"

# Step 1: Set release version
echo "🔧 Setting release version to $RELEASE_VERSION"
mvn versions:set -DnewVersion="$RELEASE_VERSION"
mvn versions:commit

# Step 2: Commit release version
git commit -am "🔖 Release $RELEASE_VERSION"
git tag -a "v$RELEASE_VERSION" -m "Release $RELEASE_VERSION"

# Step 3: Deploy with GPG signing
echo "🔐 Signing and deploying to OSSRH..."
mvn clean deploy -P$PROFILE -Dgpg.keyname="$GPG_KEYID"

# Step 4: Set next snapshot version
echo "🔧 Bumping version to $NEXT_SNAPSHOT_VERSION"
mvn versions:set -DnewVersion="$NEXT_SNAPSHOT_VERSION"
mvn versions:commit

# Step 5: Commit and push changes
git commit -am "🔄 Prepare for $NEXT_SNAPSHOT_VERSION"
git push origin main --tags

echo "✅ Release complete."

