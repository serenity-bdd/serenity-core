package net.thucydides.core.requirements


import spock.lang.Specification

class WhenRepresentingTheRequirementsTreeInJson extends Specification {

    def "Should be able to find a requirements provider on the classpath"() {

        given:
        def requirements = new FileSystemRequirementsTagProvider().requirements;
        when:
        def jsonRequirements = JSONRequirementsTree.forRequirements(requirements).asString()
        then:
        jsonRequirements == """[{"text":"Maintain my todo list","type":"capability","href":"6754ed30773116911442e38ebe772acd8c029076cd1b60c9588abd4c26ff3a12.html","nodes":[{"text":"Filtering things I need to do","type":"feature","href":"227bdc56c9739babf67663c648ba6a3a3b3b462e7875700f0254a31ab5f4772f.html","nodes":[],"selectable":false,"tags":["",""]}],"selectable":false,"tags":["","\\u003cspan class\\u003d\\u0027feature-count\\u0027\\u003e1 feature\\u003c/span\\u003e"]},{"text":"Record todos","type":"capability","href":"d193b3ce5ba9f4db3d379ec5d481383d53c5488c0328c6c9ab81312d305b6eba.html","nodes":[{"text":"Adding new items to the todo list","type":"feature","href":"39d0ceaf9da173bd59e541386c2609c9ec3b24831fb52aa5f2fa49e129995611.html","nodes":[],"selectable":false,"tags":["",""]}],"selectable":false,"tags":["","\\u003cspan class\\u003d\\u0027feature-count\\u0027\\u003e1 feature\\u003c/span\\u003e"]}]"""
    }


}

