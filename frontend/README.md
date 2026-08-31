# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some Oxlint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is enabled on this template. See [this documentation](https://react.dev/learn/react-compiler) for more information.

Note: This will impact Vite dev & build performances.

## Expanding the Oxlint configuration

If you are developing a production application, we recommend enabling type-aware lint rules by installing `oxlint-tsgolint` and editing `.oxlintrc.json`:

```json
{
  "$schema": "./node_modules/oxlint/configuration_schema.json",
  "plugins": ["react", "typescript", "oxc"],
  "options": {
    "typeAware": true
  },
  "rules": {
    "react/rules-of-hooks": "error",
    "react/only-export-components": ["warn", { "allowConstantExport": true }]
  }
}
```

See the [Oxlint rules documentation](https://oxc.rs/docs/guide/usage/linter/rules) for the full list of rules and categories.

## プロジェクトを作る。

プロジェクト名を指定。
React
TypeScript + React Compiler

```
npm create vite@latest
```

React Router はあとでつけるので、 TypeScript + React Compiler を選択。
Lintはなんでもいい。

## React Sample

React + React Router + vite-ssg サンプル。
前回は next.jsを使使用した。このときは、CSR, SSRをよく理解せずに使用したので、以下のような問題があった。

- SSR, CSRの区別ができない
- テストがやりづらい。

CSRのみ。SSGもはやらない。いっそのこと、SSR, SSGのことは忘れてしまいましょう。

このようにすることで、S3 + CloudFormation に置くだけというシンプルなものになる。
必要なデータはfetchしてレンダリングする。
認可、認証がないので、ページ自体は表示される。表示するべきデータのfetchができないので、データが表示されていないページにエラーが表示されるだけ。
認可くらいはいれたほうがいいという人もいるかもしれませんが、いらないでしょ。

### React compiler

React Compiler は「React の再レンダー最適化をビルド時に自動で行う公式コンパイラ」。
つまり useMemo / useCallback / React.memo を手で書かなくても、React が自動で最適化してくれる仕組み。

ただし、手動でuse何とかを使う必要がある部分もあるらしい。

### Router

pagesにパスとページの対応を書くのがめんどくさいので、以下のファイルをすべて読み込む。ページのパスとsrc/pages/\*\* のディレクトリ構造と一致させる。
ここにあるファイルは、 PascalCase.tsx となる。routes.tsx で kebab-case に自動的に変換される。

```
/src/pages/**/*.tsx
```

### Layout

レイアウトを追加。layout.tsxでレイアウトを定義する。

### 遅延読み込み

Reactでは最初にすべてのスクリプトを読み込んでしまう。メモリ使用量が大きくなるので、遅延読み込みを行う。
最初に読み込むページでは、 読み込みが完了するまで <Suspense fallback={<></>}> ここにあるものが表示されてしまう。
何も書いていないので、空白のページが表示されるだけ。目立たないので、これで問題ない。
どうしても気になる場合、

- fallbackの背景色を現在のテーマのものにする（なにもしなくてもそうなる？）。
- リンクやボタンのmouse hoverでページを読み込んでしまう

といった方法ががる。どう考えてもmouse hoverは必要ないと思う。
小規模なものなら、そもそも遅延読み込みがいらない。

### Config

設定ファイルを読み込む。/src/config/config.yaml にconfigを書く。
config.yamlはローカル環境用の設定を記述する。
config-<環境名>.yaml を作成しておく。デプロイ時にこのファイルを config.yaml へ上書きすること。

json だとコメントが書けないとか、いろいろ使いづらいので、yamlファイルにしています。
jsはyamlを直接扱うことができないので、configLoader.tsxで yamlからjs objectにしています。

config.yamlにAPIのURLを設定することで環境別にしています。
Reactでは実行時に環境変数を与えることができないので、このような方法になっています。

```
apiBaseUrl: http://localhost:8080
env: local
```

configを用意したのはいいものの、ここに設定する値がない。
apiBaseUrlが必要かと思ったけど、frontendもbackendも同じドメインなので、APIはパスのみ。
backendとfrontendをべつなドメインにする必要がある場合、apBaseUrlを書くかもしれないが、CORSの対応が必要なので、やりたくない。

### 画面

BaseUI+tailwindcss を使用することにしました。

前回はMUIを使用していましたが、これは見た目と動作が一体となっているため、コードを変更せずに見た目を変更するのに手間がかかかる。
動作はBaseUI 見た目はtailwindcss とすることで、これを解消できないかという意図があります。
デザイナーがデザインするという話もありました。そのため、MUIとデザインツールの組み合わせを試してみましたが、見た目と動作が一体となっているため、たいへん困難なものとなりました。
試してみるつもりはありませんが、BaseUI+tailwindcssでは、MUIよりも楽になるのではないでしょうか。
MUIと違い、デフォルトのテーマがありません。ボタンコンポーネントをそのまま使っても、ただのテキストと見分けがつかない。
そのため、簡単でいいので、テーマを作成する必要があります。
テーマ付きコンポーネントを作成することも考えましたが、見た目だけならテーマのCSSを作成しておくだけでいい。
機能の追加が必要な場合は、新しいコンポーネントを作成する。

MUIにも利点はあります。
MUIの見た目でいいのであれば、テーマを作らなくていい分、MUIのほうが楽。
コンポーネントが多い。
レイアウトもコンポーネントになっているので、レイアウトの調整も楽。
とか、そんな感じがします。

### テスト
