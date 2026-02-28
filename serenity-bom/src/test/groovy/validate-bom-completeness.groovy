// Validates that every reactor module is listed in the BOM's <dependencyManagement>.

// Modules that should NOT appear in the BOM:
//  - serenity-bom:        the BOM itself, not a consumable dependency
//  - serenity-smoketests: internal test-only module (only in the 'experimental' profile)
// Add any additional modules that should be excluded from the check here
def excluded = ['serenity-bom', 'serenity-smoketests'] as Set

// Collect all module names from the root pom (default modules + all profiles)
def rootPom = new groovy.xml.XmlSlurper().parse(new File(project.basedir.parentFile, 'pom.xml'))

def allModules = [] as Set
rootPom.modules.module.each { allModules << it.text() }
rootPom.profiles.profile.modules.module.each { allModules << it.text() }

// Collect all artifactIds managed by the BOM
def bomPom = new groovy.xml.XmlSlurper().parse(new File(project.basedir, 'pom.xml'))
def bomArtifacts = [] as Set
bomPom.dependencyManagement.dependencies.dependency.each { bomArtifacts << it.artifactId.text() }

// Fail the build if any publishable module is missing from the BOM
def missing = (allModules - bomArtifacts - excluded).sort()

if (missing) {
    throw new RuntimeException("BOM is missing modules: ${missing}")
}
log.info("BOM completeness check passed: all ${allModules.size() - excluded.size()} modules are listed.")
