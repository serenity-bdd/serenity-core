package net.thucydides.core.reports.html

import net.thucydides.core.model.TestTag
import spock.lang.Specification

class WhenGeneratingBreadcrumbs extends Specification {

    def "breadcrumbs should reflect the parent tags in a tag collection"() {
        given:
            def allTags = [TestTag.withValue("feature:a"), TestTag.withValue("story:a/b"), TestTag.withValue("issue:123")]
        when:
            List<TestTag> breadcrumbs = Breadcrumbs.forRequirementsTag(TestTag.withValue("story:a/b")).fromTagsIn(allTags)
        then:
            breadcrumbs == [TestTag.withValue("feature:a")]
    }

    def "parent features have no breadcrumbs"() {
        given:
        def allTags = [TestTag.withValue("feature:a"), TestTag.withValue("story:a/b"), TestTag.withValue("issue:123")]
        when:
        List<TestTag> breadcrumbs = Breadcrumbs.forRequirementsTag(TestTag.withValue("feature:a")).fromTagsIn(allTags)
        then:
        breadcrumbs == []
    }

}
