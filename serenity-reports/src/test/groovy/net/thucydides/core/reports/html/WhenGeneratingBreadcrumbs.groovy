package net.thucydides.core.reports.html

import net.thucydides.model.domain.TestTag
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

    def "breadcrumbs should not be confused by duplicated names"() {
        given:
        def allTags = [TestTag.withValue("feature:Search"), TestTag.withValue("feature:Search2"),
                       TestTag.withValue("feature:Search with different folder name"), TestTag.withValue("feature:Search with different folder name2"),
                       TestTag.withValue("story:Search/Search in google"),TestTag.withValue("story:Search with different folder name/"),
                       TestTag.withValue("story:Search with different folder name2/Search with after2"), TestTag.withValue("story:Search2/Search2")]
        when:
        List<TestTag> breadcrumbs = Breadcrumbs.forRequirementsTag(TestTag.withValue("story:Search2/Search2")).fromTagsIn(allTags)
        then:
        breadcrumbs == [TestTag.withValue("feature:Search2")]
    }


    /*
    TestTag{name='Search/Search in google', type='story'},
TestTag{name='Search', type='feature'},
TestTag{name='Search2/Search2', type='story'},
TestTag{name='Search2', type='feature'},
TestTag{name='Search with different folder name/', type='story'},
TestTag{name='Search with different folder name', type='feature'},
TestTag{name='Search with different folder name2', type='feature'},
TestTag{name='Search with different folder name2/Search with after2', type='story'}
     */

}
