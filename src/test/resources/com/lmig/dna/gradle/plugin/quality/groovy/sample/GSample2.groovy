package com.lmig.dna.gradle.plugin.quality.groovy.sample

class GSample2 {

    def foo(String bar) {
        def res = "123" + bar + "123";
        if (bar)
            return "123"
    }
}