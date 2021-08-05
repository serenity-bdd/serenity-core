## Serenity Maven Plugin
We need to build this plugin separately, 
as it depends on the core Serenity libraries being present in the local Maven repo.

## Release process

The current release process for the Serenity Maven Plugin is different to that used by the other Serenity modules. We don't use the release plugin as we want to be able to keep the release numbers in sync with serenity-core. 

The process is:
    * Create a new release branch that contains the new version number, e.g.
        `git checkout -b release-1.5.1-rc.4` 
   * Update the pom to use the version number, e.g. `mvn versions:set -DnewVersion=1.5.1-rc.4`
   * Tag the new version, e.g. `git tag v1.5.1-rc.4`
   * Commit and push
   * Do a deployment build (`mvn clean deploy`)
   * Switch back to the master branch