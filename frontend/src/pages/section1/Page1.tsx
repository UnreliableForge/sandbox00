import { useApiQuery } from "@api/query";
import { Button } from "@base-ui/react";
import { useQueryClient } from "@tanstack/react-query";

export default function Page() {
  const queryClient = useQueryClient();

  // データ取得。tanstackのuseQueryをそのまま使う。
  // const useHello = () => {
  //   return useQuery({
  //     queryKey: ["v1/hello", "GET", { param1: "A" }],
  //   });
  // };

  // 型安全なuseQuery. 今は型がanyとなっているので、あまり意味がない。
  // param : any となっているところは、ApiMapで定義されているものを指定すること。useApiQueryパラメータ[1]が型エラーになります。
  const useHello = (param: any) => {
    return useApiQuery(["v1/hello", param], { enabled: !!param });
  };

  // 値が変わったらAPIを呼ぶようにするには、useStateとか使う。
  // Reactのふつうの使い方なので、詳しく書かない。
  const { data, isLoading, isError, refetch } = useHello(undefined);

  console.log(data);

  let hello: React.ReactNode = null;
  if (isLoading) {
    hello = <div>読み込み中...</div>;
  } else if (isError) {
    hello = <div>エラーが発生しました</div>;
  } else {
    hello = JSON.stringify(data);
  }

  return (
    <>
      <h1>Section1</h1>
      <div className="div-x1">page1.</div>
      <div className="div-x2">page1.</div>

      <Button className="button-confirm" onClick={() => refetch()}>
        useHelloのrefetchを使う
      </Button>
      <Button
        className="button-confirm button-large"
        onClick={() =>
          queryClient.invalidateQueries({
            queryKey: ["v1/hello", "GET", { param1: "A" }],
          })
        }
      >
        queryClientでキャッシュを無効にして再fetch
      </Button>
      <div style={{ marginTop: "20px" }}>{hello}</div>
    </>
  );
}
