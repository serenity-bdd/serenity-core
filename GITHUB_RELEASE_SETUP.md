# GitHub Actions Release Setup

This document describes the required GitHub secrets and configuration for automated releases to Maven Central.

## GitHub Secrets Required

You need to add the following secrets to your GitHub repository (Settings → Secrets and variables → Actions):

### 1. Central Portal Credentials

**`CENTRAL_TOKEN_USERNAME`**
- Your Central Portal user token username
- Generate at: https://central.sonatype.com → Account → Generate User Token

**`CENTRAL_TOKEN_PASSWORD`**
- Your Central Portal user token password
- Generated together with the username above

### 2. GPG Signing Credentials

**`GPG_PRIVATE_KEY`**
- Your GPG private key in ASCII armor format
- Export with: `gpg --armor --export-secret-keys YOUR_KEY_ID`
- Copy the entire output including `-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END PGP PRIVATE KEY BLOCK-----`

**`GPG_PASSPHRASE`**
- The passphrase for your GPG private key
- Same passphrase you use when signing locally

## Exporting Your GPG Key

To export your GPG private key for GitHub:

```bash
# List your keys to find the KEY_ID
gpg --list-secret-keys --keyid-format LONG

# Export the private key
gpg --armor --export-secret-keys YOUR_KEY_ID
```

Copy the entire output (including the header and footer lines) and paste it as the `GPG_PRIVATE_KEY` secret in GitHub.

## Workflow Features

The release workflow (`.github/workflows/release.yml`) includes:

- ✅ **Java 17** - Matches project requirements
- ✅ **Maven Caching** - Speeds up builds by caching dependencies
- ✅ **Central Portal Integration** - Direct publishing to Maven Central via new Central Portal
- ✅ **GPG Signing** - Automatic artifact signing in CI
- ✅ **Git Configuration** - Properly configured for maven-release-plugin commits
- ✅ **Manual Trigger** - Uses `workflow_dispatch` for controlled releases

## Running a Release

1. Go to your GitHub repository
2. Click **Actions** tab
3. Select **"Release to Maven Central"** workflow
4. Click **"Run workflow"** button
5. Confirm by clicking **"Run workflow"**

The workflow will:
1. Check out the code
2. Set up Java 17 and Maven cache
3. Configure Central Portal credentials
4. Import your GPG key for signing
5. Configure Git for version commits
6. Run `mvn release:prepare release:perform -Pdeploy`
   - Removes `-SNAPSHOT` from version
   - Creates a Git tag
   - Builds and signs all artifacts
   - Deploys to Central Portal
   - Increments to next snapshot version

## Monitoring the Release

### In GitHub Actions
- Watch the workflow progress in the Actions tab
- The "Run Maven Release" step will show Maven output
- Build can take 20-40 minutes for large multi-module projects

### In Central Portal
1. Visit https://central.sonatype.com
2. Go to **Deployments**
3. Find your deployment by ID (shown in Maven output)
4. Check validation status and publishing progress

## Troubleshooting

### "401 Unauthorized" Error
- Check that `CENTRAL_TOKEN_USERNAME` and `CENTRAL_TOKEN_PASSWORD` are set correctly
- Verify the token is still valid at central.sonatype.com

### GPG Signing Fails
- Ensure `GPG_PRIVATE_KEY` includes the full ASCII armor format
- Verify `GPG_PASSPHRASE` matches your key's passphrase
- Check that your public key is uploaded to key servers

### Git Commit Fails
- The workflow now includes Git configuration automatically
- If it still fails, check if the repository requires signed commits

### Build Hangs at "Waiting until Deployment is published"
- This is normal! Publishing can take 20-40 minutes
- Monitor progress at central.sonatype.com
- To speed up future builds, change `<waitUntil>published</waitUntil>` to `<waitUntil>validated</waitUntil>` in pom.xml

## Configuration Files

### Main Configuration
- `.github/workflows/release.yml` - GitHub Actions workflow
- `pom.xml` - Maven configuration with central-publishing-maven-plugin

### Key POM Settings
```xml
<plugin>
    <groupId>org.sonatype.central</groupId>
    <artifactId>central-publishing-maven-plugin</artifactId>
    <version>0.9.0</version>
    <extensions>true</extensions>
    <configuration>
        <publishingServerId>central</publishingServerId>
        <autoPublish>true</autoPublish>
        <waitUntil>published</waitUntil>
    </configuration>
</plugin>
```

## Migration from OSSRH

This project has been migrated from the legacy OSSRH system to the new Central Portal:

- ✅ Replaced `nexus-staging-maven-plugin` with `central-publishing-maven-plugin`
- ✅ Removed obsolete `distributionManagement` section
- ✅ Disabled `maven-deploy-plugin` (replaced by central-publishing-maven-plugin)
- ✅ Updated GitHub Actions workflow for Central Portal
- ✅ Namespace migrated at central.sonatype.com

## Resources

- Central Portal: https://central.sonatype.com
- Documentation: https://central.sonatype.org/publish/publish-portal-maven/
- Plugin Info: https://central.sonatype.com/artifact/org.sonatype.central/central-publishing-maven-plugin