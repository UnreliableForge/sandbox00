// import { ConfigContext } from "@config/config-context";
// import { useContext } from "react";

import { Button, Tabs } from "@base-ui/react";

export default function About() {
  //  const config = useContext(ConfigContext);

  return (
    <div className="page-bg-color">
      <h1 className="mb-12 text-center text-4xl font-bold text-gray-800"></h1>
      <div>
        <p className="text-[40px]">ここがメインコンテントになります。</p>　{" "}
      </div>
      <div>
        <Button onClick={() => {}}>ボタン1</Button>
      </div>
      <div>
        <div className="textarea-severity-success">SUCESS TEXT AREA</div>
        <div className="textarea-severity-info">INFO TEXT AREA</div>
        <div className="textarea-severity-warning">WARNING TEXT AREA</div>
        <div className="textarea-severity-error">ERROR TEXT AREA</div>
      </div>
      <div>
        <Tabs.Root defaultValue="overview">
          <Tabs.List>
            <Tabs.Tab value="overview">Overview</Tabs.Tab>
            <Tabs.Tab value="projects">Projects</Tabs.Tab>
            <Tabs.Tab value="account">Account</Tabs.Tab>
            <Tabs.Indicator />
          </Tabs.List>
          <div>
            <Tabs.Panel value="overview">
              <p>Workspace stats and activity.</p>
            </Tabs.Panel>
            <Tabs.Panel value="projects">
              <p>Milestones and deadlines.</p>
            </Tabs.Panel>
            <Tabs.Panel value="account">
              <p>Profile and preferences.</p>
            </Tabs.Panel>
          </div>
        </Tabs.Root>
      </div>
    </div>
  );
}
