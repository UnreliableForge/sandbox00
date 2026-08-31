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
      const relative = path.replace("./pages/", "").replace(".tsx", "");
      const cleaned = relative.replace(/\/index$/i, "").toLowerCase();

      const url = cleaned === "index" ? "" : toKebabCase(cleaned);
      // PascalCase → kebab-case
      //      const url = toKebabCase(cleaned);
      const Component = lazy(loader);

      return {
        path: "/" + url,
        // loader: loader,
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
