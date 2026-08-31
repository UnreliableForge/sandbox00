import { QueryClient } from "@tanstack/react-query";
import { apiClient } from "@utils/ApiClient";

/*
 * queryKey: ["v1/hello", { param1: "A" }],
 * からURLとパラメータを生成し、fetchを行う。APIなので、メソッドはPOST固定。
 * パラメータ1は APIのパス。同じドメインなので、パスだけでよい。
 * パラメータ2は、リクエストボディ(データ)。
 *
 * パスパラメータには未対応。個人的な好みの問題で、これからもこれに対応する予定はありません。
 * APIであり、リソースの位置を表すものではないので、パスパラメータなんてないほうがいい。
 * パスも パッケージ、クラス名、メソッド名のようにする。リソースの場所ではなく、APIの名前として命名する。
 * だとすると、Controllerのメソッドでいちいちパスを定義しなくても、クラス名とメソッド名で自動的にパスが生成されてもいいよね。
 * REST原理主義者たちは、パスパラメータとかHTTPメソッドを厳密に守り、リソースとAPIという深くて大きな溝を埋める努力をしたらいいと思います。
 */

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      queryFn: async ({ queryKey }) => {
        const [url, params] = queryKey;

        const response = await apiClient.post(url as string, params);
        return response.data;
      },
    },
  },
});
