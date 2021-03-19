package fr.groggy.racecontrol.tv.core.token

import fr.groggy.racecontrol.tv.f1.F1Token

interface TokenRepository<T> {

    fun find(): T?

    fun save(token: T)

}

interface F1TokenRepository : TokenRepository<F1Token>
