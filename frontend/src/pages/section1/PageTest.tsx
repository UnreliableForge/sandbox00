import { Button } from "@base-ui/react";
import { ConfigContext } from "@config/config-context";
import { useContext, useState } from "react";

export default function Page() {
  const config = useContext(ConfigContext);
  const [result, setResult] = useState<string>("");

  // ここでは直接fetchを行っている。
  // 実際の開発ではこの方法は用いない。
  // TanStack Query という便利なものがあるので、それを使いましょう。
  const handleClick = async () => {
    try {
      const res = await fetch(`${config?.apiBaseUrl}/hello`, {
        method: "GET",
      });

      if (!res.ok) {
        throw new Error("API error");
      }

      const data = await res.json();
      setResult(JSON.stringify(data));
    } catch (err) {
      console.error(err);
      setResult("error");
    }
  };

  return (
    <>
      <h1>Section1</h1>
      <div>page1.</div>
      <div>{config?.apiBaseUrl}</div>
      <Button className="button-confirm" onClick={handleClick}>
        PUSH
      </Button>
      <div style={{ marginTop: "20px" }}>
        <strong>結果:</strong> {result}
      </div>
    </>
  );
}
