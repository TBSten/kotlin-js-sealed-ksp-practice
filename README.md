# kotlin-js-sealed-ksp-practice

Kotlin/JSでsealed interface/classの網羅性を失う問題をKSPでTypeScriptのパターンマッチング関数を自動生成して解決するプロジェクト

記事:

https://zenn.dev/tbsten/articles/8519b3643ac9d5

## 概要

`@JsExport` される MyScreenState がある場合以下のように kotlin の when 文のような記述が TypeScript でできるようになる。

```ts
const stateString =
    whenMyScreenState(someMyScreenState, {
        successState: (state: SuccessState) => "success !!",
        errorState: (state: ErrorState) => "error !!",
        successState: (state: LoadingState) => "loading...",
    })
```

## 主要なファイル

### KSP Plugin

- [
  `ksp-plugin/src/main/kotlin/me/tbsten/prac/kotlinjssealedksp/SealedPatternMatcherFunGeneratorProvider.kt`](./ksp-plugin/src/main/kotlin/me/tbsten/prac/kotlinjssealedksp/SealedPatternMatcherFunGeneratorProvider.kt) -
  KSPのSymbolProcessorProvider実装
- [
  `ksp-plugin/src/main/kotlin/me/tbsten/prac/kotlinjssealedksp/SealedPatternMatcherFunGenerator.kt`](./ksp-plugin/src/main/kotlin/me/tbsten/prac/kotlinjssealedksp/SealedPatternMatcherFunGenerator.kt) -
  TypeScriptコード生成のメインロジック
    - 記事で扱った内容に加え、多少リファクタ・改善してあります。

### Shared Module

- [
  `shared/src/commonMain/kotlin/me/tbsten/prac/kotlinjssealedksp/MyScreenState.kt`](./shared/src/commonMain/kotlin/me/tbsten/prac/kotlinjssealedksp/MyScreenState.kt) -
  サンプルのsealed interface定義

### Web App

- [`webApp/src/index.tsx`](./webApp/src/index.tsx) - 生成されたパターンマッチング関数の使用例

### 設定ファイル

- [`shared/build.gradle.kts`](./shared/build.gradle.kts) - KSP Pluginの設定
- [`package.json`](./package.json) - npm workspacesの設定
