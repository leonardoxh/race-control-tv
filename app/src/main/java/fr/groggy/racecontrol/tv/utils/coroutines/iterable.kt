package fr.groggy.racecontrol.tv.utils.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

fun <A, B> List<A>.traverse(f: (A) -> Flow<B>): Flow<List<B>> =
    map(f).fold(flowOf(emptyList())) { accFlow, itemFlow ->
        accFlow.combine(itemFlow) { acc, item -> acc + item }
    }
