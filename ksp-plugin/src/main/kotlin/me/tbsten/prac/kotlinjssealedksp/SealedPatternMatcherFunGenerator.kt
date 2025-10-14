package me.tbsten.prac.kotlinjssealedksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

class SealedPatternMatcherFunGenerator(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation("kotlin.js.JsExport")
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.modifiers.contains(Modifier.SEALED) }
            .forEach { sealedClass: KSClassDeclaration ->
                val childClasses = sealedClass.getSealedSubclasses()
                sealedClass.packageName

                codeGenerator.createNewFile(
                    dependencies = Dependencies(
                        aggregating = false,
                        sources = (listOf(sealedClass.containingFile) + childClasses.map { it.containingFile })
                            .filterNotNull()
                            .toTypedArray()
                    ),
                    packageName = "generated",
                    fileName = sealedClass.simpleName.asString(),
                    extensionName = "ts",
                ).bufferedWriter().use { writer ->
                    // import 文
                    val moduleName = options["SealedPatternMatcherFunGenerator.moduleName"]
                        ?: error("ksp.arg(\"SealedPatternMatcherFunGenerator.moduleName\", \"...\") が設定されていません")

                    writer.appendLine("""import { ${(listOf(sealedClass) + childClasses).joinToString(", ") { it.typeName }} } from "$moduleName"""")

                    // 関数定義
                    writer.appendLine("""export function when${sealedClass.typeName}<const R>(""")
                    writer.appendLine("""  ${sealedClass.variableName}: ${sealedClass.typeName},""")
                    writer.appendLine("""  blocks: {""")
                    childClasses.forEach { childClass ->
                        writer.appendLine("""    ${childClass.variableName}: (${childClass.variableName}: ${childClass.typeName}) => R,""")
                    }
                    writer.appendLine("""  },""")
                    writer.appendLine(""") {""")

                    // 分岐して 該当のブロックを実行する
                    childClasses.forEachIndexed { index, childClass ->
                        writer.appendLine("""  ${if (index == 0) "if" else "} else if"}(${sealedClass.variableName} instanceof ${childClass.typeName}) {""")
                        writer.appendLine("""    return blocks.${childClass.variableName}(${sealedClass.variableName})""")
                    }

                    // どれにも該当しなかった場合のエラー
                    writer.appendLine("""  } else {""")
                    writer.appendLine("""    throw new TypeError()""")
                    writer.appendLine("""  }""")

                    writer.appendLine("""}""")
                }
            }

        return emptyList()
    }
}

private val KSClassDeclaration.typeName: String get() = this.simpleName.asString()
private val KSClassDeclaration.variableName: String get() = this.typeName.replaceFirstChar { it.lowercase() }
