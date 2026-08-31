// src/layout.tsx
// import { Tabs } from "@base-ui/react";
//import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "@base-ui/react";
import { Link, Outlet } from "react-router-dom";
import { setMockError } from "./__tests__/utils/mock-query-client";

// 成功パターン
setMockError(false);

// 失敗パターン
setMockError(true);

const applyTheme = (themeName: "default" | "light" | "dark" | "MSX1") => {
  if (themeName === "default") {
    document.documentElement.removeAttribute("data-theme");
    localStorage.removeItem("theme");
  } else {
    document.documentElement.setAttribute("data-theme", themeName);
    localStorage.setItem("theme", themeName);
  }
};

export function Layout() {
  // const navigate = useNavigate();
  // const location = useLocation();

  /*
  Tabsを使ってパスごとにヘッダを変更。
  ページのレイアウトを変更したい場合、 location.pathnameを使う。
  ページのほうに、使用するレイアウトを定義することもできるらしいけど、そこまではいらないと思う。

  Reactなので、レイアウトの変更もコンポーネントの追加とかで簡単にできるでしょう。
  ただし、MUIと違い、レイアウト用のコンポーネントとかないので、自分で作りましょう。
  */

  const useMock =
    import.meta.env.VITE_USE_MOCK === "true" ? "MOCK IN USE. " : "";
  const toggleAlwaysError =
    import.meta.env.VITE_USE_MOCK !== "true" ? (
      ""
    ) : (
      <>
        MOCK FETCT ERROR:
        <Button className="button-confirm" onClick={() => setMockError(true)}>
          ON
        </Button>
        <Button className="button-confirm" onClick={() => setMockError(false)}>
          OFF
        </Button>
      </>
    );

  return (
    <div className="h-screen">
      {/* --- Top Bar --- */}
      <header className="header-area fixed top-0 right-0 left-0 h-16 flex-row items-center border-b border-gray-200 px-4">
        <div className="flex">
          <div className="basis-14/16">SAMPLE APPLICATION.</div>
          <div className="basis-2/12">
            <Button className="" onClick={() => applyTheme("default")}>
              標準
            </Button>
            <Button className="" onClick={() => applyTheme("MSX1")}>
              MSX1
            </Button>
          </div>
        </div>
      </header>
      <main className="main-area h-full overflow-auto pt-16 pb-16">
        {/* --- Main Area: Left Menu + Content --- */}
        <div className="main-leftside-area flex h-full flex-1 overflow-hidden">
          {/* Left Menu */}
          <aside className="w-64 overflow-auto border-r border-gray-200 p-4">
            <div>左メニュー</div>
            <div>
              <Link to={"/"}>HOME</Link>
            </div>
            <div>
              <Link to={"./about"}>about</Link>
            </div>
            <div>
              <Link to={"./section1"}>Section1</Link>
            </div>
          </aside>
          {/* Content Area */}
          <main className="main-content-area flex-1 overflow-auto p-4">
            <Outlet />
          </main>
        </div>
      </main>
      {/* --- Bottom Bar --- */}
      <footer className="footer-area mixed fixed right-0 bottom-0 left-0 h-16 items-center border-t border-gray-200 px-4">
        MADE BY UnreliableForge. {useMock} {toggleAlwaysError}
      </footer>
    </div>
  );

  // return (
  //   <div className="root">
  //     <div className="flex flex-col h-screen">
  //       {/* --- Header Navigation (Tabs) --- */}

  //       <Tabs.Root
  //         value={location.pathname}
  //         onValueChange={(val) => navigate(val)}
  //         className="border-b border-gray-200 px-4"
  //       >
  //         <Tabs.Panel value="/" className="px-4 py-2">
  //           Home
  //         </Tabs.Panel>
  //         <Tabs.Panel value="/about" className="px-4 py-2">
  //           About
  //         </Tabs.Panel>
  //       </Tabs.Root>

  //       {/* --- Main Content --- */}
  //       <div>
  //         Here is main Content.
  //         <div className="flex-1 overflow-auto p-4">
  //           <Outlet />
  //         </div>
  //       </div>
  //     </div>
  //   </div>
  // );
}
