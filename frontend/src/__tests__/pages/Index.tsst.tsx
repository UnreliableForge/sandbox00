import Page from "@pages/Index";
import { screen } from "@testing-library/react";
import { createMockQueryClient } from "@testutils/create-mock-query-client";
import { renderWithProviders } from "@testutils/render-with-providers";

/*
 * ページとコンポーネントのテスト。
 *
 * ファイル名は もとのファイル名に .test.tsx をつける。
 * ページ（コンポーネントも同じ）はPascalCase.tsxになっているが、そのまま PascalCase.test.tsx とする。
 * ページのimpoerがめんどくさいので、Pageのほうは
 * export default function Page() {
 * で固定とする。
 */

test("テストケースをここに書く", async () => {
  // MockQueryClientを作成する。
  // ここでMockで返したい戻り値を設定する。queryKeyごとに返す値を変更することができる。
  const queryClient = createMockQueryClient(
    async ({ queryKey }: { queryKey: readonly unknown[] }) => {
      const [url] = queryKey;

      if (url === "v1/user") {
        return {
          name: "ここで/v1/userの戻り値を設定する。ページやコンポーネントでは呼ぶAPIは決まっている場合がある。その場合は、いちいちパスを区別する必要はない。",
        };
      }

      return null;
    },
  );

  // ページのレンダリング。
  renderWithProviders(<Page />, { queryClient });

  // 必要ならレンダリングを待つ。

  // ここで検証する。
  expect(screen.getByText("Loading...")).toBeInTheDocument();
});
