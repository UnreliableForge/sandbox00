import { QueryClient } from "@tanstack/react-query";

// QueryClientエラーの切り替え。
let shouldError = true;
export const setMockError = (v: boolean) => {
  shouldError = v;
};

export const mockQueryClient = new QueryClient({
  defaultOptions: {
    queries: {
      queryFn: async ({ queryKey }) => {
        const [url, params] = queryKey;

        // trueに設定した場合、必ずエラーとなる。エラー時の挙動もこれて試すことができる。
        // 実行中の切り替えが可能。たぶんコンソールからsetMockError(true)でもできる。
        if (shouldError) {
          throw new Error(`Mock error for ${url} ${params}`);
        }
        // ここでパスごとのMock戻り値を設定する。
        // テスト時はテスト中にモックから返す値を設定することができる。
        // ここで設定するのは、backendなしてfrontendの動作を確認する場合など、好きなタイミングでMockの戻り値を設定できない場合に使用する。
        switch (url) {
          case "v1/hello":
            return { message: "モック Hello" };
        }
      },
    },
  },
});
