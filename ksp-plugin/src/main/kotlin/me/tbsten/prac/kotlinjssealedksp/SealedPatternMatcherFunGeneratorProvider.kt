package me.tbsten.prac.kotlinjssealedksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class SealedPatternMatcherFunGeneratorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        SealedPatternMatcherFunGenerator(
            codeGenerator = environment.codeGenerator,
            options = environment.options,
        )
}
