// src/router.tsx
import { lazy } from "react";
import { createBrowserRouter } from "react-router-dom";
import ErrorPage from "./ErrorPage";
import { Layout } from "./layout";

//const modules = import.meta.glob("./pages/**/*.tsx");

const modules = import.meta.glob("./pages/**/*.tsx", {
  eager: false,
}) as Record<string, () => Promise<{ default: React.ComponentType }>>;

/*
 * Routerの自動設定。
 * pages/* 以下のtsxを自動的に読み込みます。ディレクトリ構成とパスの構成は同じ。
 * ディレクトリのほうは、大文字、小文字を区別し、tsxファイルはPascalCaseとすること。
 * パスはすべて小文字になります。tsxファイルは、PascalCaseからkebab-caseへ変換されます。
 * Intex.tsx(index.tsx)は、ディレクトリ名までの指定で自動的に index.tsxとなります。index.tsxだけは一般的なWEBサーバーっぽい感じになる。
 */
export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    errorElement: <ErrorPage />,
    children: Object.entries(modules).map(([path, loader]) => {
      // 1. 拡張子とプレフィックスを取り除く
      const relative = path.replace("./pages/", "").replace(/\.tsx$/, "");

      // 2. パスをスラッシュで分割して、セグメントごとにケバブケースに変換する
      const segments = relative.split("/").map((segment) => {
        if (segment.toLowerCase() === "index") return "";
        return toKebabCase(segment);
      });

      // 3. 再びスラッシュで結合する（末尾や不要な空文字を調整）
      const url = segments.filter(Boolean).join("/");
      const Component = lazy(loader);

      return {
        path: url ? "/" + url : "", // ルート（index）の場合はパスを空（または "/"）にする
        element: <Component />,
      };
    }),
  },
]);

function toKebabCase(str: string) {
  return str
    .replace(/([a-z0-9])([A-Z])/g, "$1-$2")
    .replace(/([A-Z])([A-Z][a-z])/g, "$1-$2")
    .toLowerCase();
}
