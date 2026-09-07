/*
 * ローカル開発用の偽ログイン画面。
 * ダイアログはめんどくさいので、とりあえず画面にしてみた。
 */

import { Input } from "@base-ui/react";
import { useState } from "react";

function KaraPage() {
  return (
    <>
      <h1>開発用メンテナンスページ</h1>
      <div>開発用メンテナンスページは無効です。</div>
    </>
  );
}

function generateFakeJwt(sub: string, email?: string) {
  const header = {
    alg: "none",
    typ: "JWT",
  };

  const payload = {
    sub,
    email: email ?? `${sub}@local.example.com`,
    "cognito:groups": ["local"],
  };

  const base64url = (obj: any) =>
    btoa(JSON.stringify(obj))
      .replace(/=/g, "")
      .replace(/\+/g, "-")
      .replace(/\//g, "_");

  return `${base64url(header)}.${base64url(payload)}.`; // 署名なし
}

function Page() {
  const [sub, setSub] = useState("");

  const login = async () => {
    const fakeJwt = generateFakeJwt(sub);

    const body = {
      idToken: fakeJwt,
    };

    console.log(fakeJwt);
    console.log(body);

    await fetch("/api/v1/session", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
  };

  const whoami = async () => {
    const body = {};
    console.log(body);
    await fetch("/api/v1/whoami", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
  };

  return (
    <>
      <h1>開発用メンテナンスページ</h1>
      <div>
        <h2>テスト用セッションを開始する。</h2>
        <div>
          <Input placeholder="sub" onChange={(e) => setSub(e.target.value)} />
          <button onClick={login}>Create Local Session</button>
        </div>
      </div>
      <div>
        <h2></h2>
        <div>
          <button onClick={whoami}>Whoami</button>
        </div>
      </div>
    </>
  );
}

let p = KaraPage;

if (import.meta.env.VITE_LOCAL === "true") {
  p = Page;
}

export default p;
