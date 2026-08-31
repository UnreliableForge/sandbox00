// 型安全なuseQuery. そのうち useMutation も作りましょう。

export type ApiMap = {
  // APIのパス。Controllerからいい感じに自動生成するようにしたい。
  "v1/hello": {
    // v1/hello のパラメータと戻り値の型を定義し、それを指定する。最初にfetchする必要がない場合は、undefinedも使えるようにする。
    // この型定義は、Spring BootにあるControllerクラスから自動的に生成する。とはいえ、? とか | null もあるので、全自動とはならないかもしれない。
    // Bean定義に
    // @NotNull → TS では string
    // @Nullable → TS では string | null
    // Optional<T> → TS では T | null
    //とすることである程度自動化できる。nullよりはundefinedのほうがいいかもしれない。
    input: any | undefined;
    output: any;
  };
};

// 型安全な useQueryを使う。使い方は tanstackのuseQueryと同じ。
// useApiQuery("v1/hello", { param1: "A" });
