import { ConfigContext } from "@config/config-context";
import { useContext } from "react";
import { Link } from "react-router-dom";

/**
 * ページのファイル名は、PascalCase.tsx とすること。
 * router.tscで kebab-case に返還されます。
 * 関数の名前はなんでもいいですが、Pageとしておきましょう。
 */
export default function Page() {
  const config = useContext(ConfigContext);

  return (
    <>
      <h1>index</h1>
      <div>This is SANDBOX application.</div>
      <div>{config?.apiBaseUrl}</div>
      <Link to="/about">about</Link>
    </>
  );
}
