# How to build a new release

The release process involves using the Maven Nexus plugin to deploy to the Maven Central OSS repository.
It does not use the maven release plguin.

## Step 1 - set the new version number

```
mvn versions:set -DnewVersion=2.3.25
mvn versions:commit
```

## Step 2 - Commit and tag this version

```
git commit -a -m"Updated to version 2.3.25"
git tag v2.3.25
git push
```

## Deploy to Nexus

```
mvn clean deploy -Prelease
```

## Update to next snapshot version

```
mvn versions:set -DnewVersion=2.3.26-SNAPSHOT
mvn versions:commit
```

