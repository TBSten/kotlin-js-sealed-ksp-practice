package me.tbsten.prac.kotlinjssealedksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class SealedPatternMatcherFunGenerator(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        TODO("Not yet implemented")
    }
}
