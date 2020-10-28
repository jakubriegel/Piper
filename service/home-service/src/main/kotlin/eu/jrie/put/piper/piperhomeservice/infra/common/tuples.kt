package eu.jrie.put.piper.piperhomeservice.infra.common

import reactor.util.function.Tuple2
import reactor.util.function.Tuple3

operator fun <A, B> Tuple2<A, B>.component1() = t1
operator fun <A, B> Tuple2<A, B>.component2() = t2

operator fun <A, B, C> Tuple3<A, B, C>.component1() = t1
operator fun <A, B, C> Tuple3<A, B, C>.component2() = t2
operator fun <A, B, C> Tuple3<A, B, C>.component3() = t3
