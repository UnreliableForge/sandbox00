import { useState } from "react";

export function NiseLoginDialog() {
  const [token, setToken] = useState("");

  const login = async () => {
    await fetch("/api/v1/session", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idToken: token }),
      credentials: "include",
    });
    window.location.reload();
  };

  return (
    <div>
      <textarea
        value={token}
        onChange={(e) => setToken(e.target.value)}
        placeholder="Paste fake IDToken"
      />
      <button onClick={login}>Create Local Session</button>
    </div>
  );
}
