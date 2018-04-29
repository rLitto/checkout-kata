package io.rlitto.kata.checkout.utils

import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Collections.*

class CollectionUtilsTest extends Specification {
    def "test immutableCopyOf null set returns empty set"() {
        given:
           Set set = null
        when:
           Set copy = CollectionUtils.immutableCopyOf((Set) set)
        then:
           copy.size() == 0
    }

    def "test immutableCopyOf null list returns empty list"() {
        given:
           List list = null
        when:
           List copy = CollectionUtils.immutableCopyOf((List) list)
        then:
           copy.size() == 0
    }

    def "test immutableCopyOf null map returns empty map"() {
        given:
           Map map = null
        when:
           Map copy = CollectionUtils.immutableCopyOf((Map) map)
        then:
           copy.size() == 0
    }

    @Unroll
    def "test immutableCopyOf list with #n values returns list with same values"() {
        given:
           List list = n == 0 ? emptyList() : (1..n).toList()
        when:
           List copy = CollectionUtils.immutableCopyOf(list)
        then:
           copy == list
        where:
           n << [0, 1, 2, 4, 7]
    }

    @Unroll
    def "test immutableCopyOf set with #n values returns set with same values"() {
        given:
           Set set = n == 0 ? emptySet() : (1..n).toSet()
        when:
           Set copy = CollectionUtils.immutableCopyOf(set)
        then:
           copy == set
        where:
           n << [0, 1, 2, 4, 7]
    }

    @Unroll
    def "test immutableCopyOf map with #n values returns map with same values"() {
        given:

           Map map = n == 0 ? emptyMap() : (1..n).toList().collectEntries { [(it): it * 2] }
        when:
           Map copy = CollectionUtils.immutableCopyOf(map)
        then:
           copy == map
        where:
           n << [0, 1, 2, 4, 7]
    }

}
