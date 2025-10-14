package me.tbsten.prac.kotlinjssealedksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

class SealedPatternMatcherFunGenerator(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {
    val moduleName = options["SealedPatternMatcherFunGenerator.moduleName"]
        ?: error("ksp.arg(\"SealedPatternMatcherFunGenerator.moduleName\", \"...\") が設定されていません")

    init {
        codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "generated",
            fileName = "package",
            extensionName = "json",
        ).bufferedWriter().use { writer ->
            writer.appendLine(
                """
                {
                  "name": "$moduleName-generated",
                  "version": "0.0.0-unspecified",
                  "devDependencies": {
                    "typescript": "5.8.3"
                  },
                  "dependencies": {},
                  "peerDependencies": {},
                  "optionalDependencies": {},
                  "bundledDependencies": []
                }
                """.trimIndent()
            )
        }
    }

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
                        writer.appendLine(
                            """  
                            |${if (index == 0) "if" else "} else if"}(
                            |${sealedClass.variableName} ${
                                // object の場合は instanceof がうまく機能しないため 
                                // getInstance() と一致するかチェックする
                                if (childClass.classKind == ClassKind.OBJECT)
                                    " == ${childClass.typeName}.getInstance()"
                                else
                                    " instanceof ${childClass.typeName}"
                            }
                            |) {""".trimMargin()
                        )
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
