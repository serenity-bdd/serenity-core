# How to build a new release

The release process involves using the Maven Nexus plugin to deploy to the Maven Central OSS repository.
It does not use the maven release plguin.

## Step 1 - set the new version number

```mvn versions:set -DnewVersion=2.3.25```

## Step 2 - 