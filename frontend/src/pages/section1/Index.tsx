import { ConfigContext } from "@config/config-context";
import { useContext } from "react";
import { Link } from "react-router-dom";

export default function Page() {
  const config = useContext(ConfigContext);

  return (
    <>
      <h1>Section1 index</h1>
      <div>This is SANDBOX application.</div>
      <div>{config?.apiBaseUrl}</div>
      <Link to="/about">about</Link>
    </>
  );
}
